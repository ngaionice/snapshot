package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.ionice.snapshot.data.database.dao.LocationDao
import me.ionice.snapshot.data.database.model.Coordinates
import me.ionice.snapshot.data.database.model.Location
import me.ionice.snapshot.data.database.model.LocationEntry
import me.ionice.snapshot.data.database.model.LocationProperties
import java.time.Instant

class OfflineLocationRepository(
    private val dispatcher: CoroutineDispatcher,
    private val locationDao: LocationDao,
) : LocationRepository {

    override suspend fun get(locationId: Long): Location? {
        return withContext(dispatcher) { locationDao.get(locationId) }
    }

    override suspend fun add(coordinates: Coordinates, name: String): Long {
        return withContext(dispatcher) {
            locationDao.insertProperties(
                LocationProperties(
                    id = 0,
                    coordinates = coordinates,
                    name = name,
                    lastUsedAt = Instant.now().epochSecond
                )
            )
        }
    }

    override suspend fun update(
        locationId: Long,
        coordinates: Coordinates,
        name: String,
        entries: List<LocationEntry>
    ) {
        withContext(dispatcher) {
            val existing = locationDao.get(locationId)
                ?: throw IllegalArgumentException("No Location found for the given locationId.")
            locationDao.updateProperties(
                LocationProperties(
                    id = locationId,
                    coordinates = coordinates,
                    name = name,
                    lastUsedAt = Instant.now().epochSecond
                )
            )
            val oldEntries = existing.entries.map { it.dayId }.toSet()
            val newEntries = entries.map { it.dayId }.toSet()
            (newEntries subtract oldEntries).map { LocationEntry(it, locationId) }
                .let { locationDao.insertEntries(it) }
            (oldEntries subtract newEntries).map { LocationEntry(it, locationId) }
                .let { locationDao.deleteEntries(it) }
        }
    }

    override suspend fun getAllProperties(): List<LocationProperties> {
        return withContext(dispatcher) { locationDao.getAllProperties() }
    }

    override fun getAllPropertiesFlow(): Flow<List<LocationProperties>> {
        return locationDao.getAllPropertiesFlow()
    }
}