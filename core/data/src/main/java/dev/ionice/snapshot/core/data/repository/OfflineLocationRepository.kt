package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.database.dao.LocationDao
import dev.ionice.snapshot.core.database.model.CoordinatesEntity
import dev.ionice.snapshot.core.database.model.LocationEntity
import dev.ionice.snapshot.core.database.model.LocationEntryEntity
import dev.ionice.snapshot.core.database.model.LocationPropertiesEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant

class OfflineLocationRepository(
    private val dispatcher: CoroutineDispatcher,
    private val locationDao: LocationDao,
) : LocationRepository {

    override suspend fun get(locationId: Long): LocationEntity? {
        return withContext(dispatcher) { locationDao.get(locationId) }
    }

    override suspend fun add(coordinates: CoordinatesEntity, name: String): Long {
        return withContext(dispatcher) {
            locationDao.insertProperties(
                LocationPropertiesEntity(
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
        coordinates: CoordinatesEntity,
        name: String,
        entries: List<LocationEntryEntity>
    ) {
        withContext(dispatcher) {
            val existing = locationDao.get(locationId)
                ?: throw IllegalArgumentException("No Location found for the given locationId.")
            locationDao.updateProperties(
                LocationPropertiesEntity(
                    id = locationId,
                    coordinates = coordinates,
                    name = name,
                    lastUsedAt = Instant.now().epochSecond
                )
            )
            val oldEntries = existing.entries.map { it.dayId }.toSet()
            val newEntries = entries.map { it.dayId }.toSet()
            (newEntries subtract oldEntries).map { LocationEntryEntity(it, locationId) }
                .let { locationDao.insertEntries(it) }
            (oldEntries subtract newEntries).map { LocationEntryEntity(it, locationId) }
                .let { locationDao.deleteEntries(it) }
        }
    }

    override suspend fun getAllProperties(): List<LocationPropertiesEntity> {
        return withContext(dispatcher) { locationDao.getAllProperties() }
    }

    override fun getAllPropertiesFlow(): Flow<List<LocationPropertiesEntity>> {
        return locationDao.getAllPropertiesFlow()
    }
}