package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.database.dao.LocationDao
import dev.ionice.snapshot.core.database.model.*
import dev.ionice.snapshot.core.model.Coordinates
import dev.ionice.snapshot.core.model.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant

class OfflineLocationRepository(
    private val dispatcher: CoroutineDispatcher,
    private val locationDao: LocationDao,
) : LocationRepository {

//    override suspend fun get(locationId: Long): PopulatedLocation? {
//        return withContext(dispatcher) { locationDao.get(locationId) }
//    }

    override suspend fun add(coordinates: Coordinates, name: String): Long {
        return withContext(dispatcher) {
            locationDao.insertEntity(
                LocationEntity(
                    id = 0,
                    coordinates = CoordinatesEntity(coordinates.lat, coordinates.lon),
                    name = name,
                    lastUsedAt = Instant.now().epochSecond
                )
            )
        }
    }

    override suspend fun getAllProperties(): List<Location> {
        return withContext(dispatcher) { locationDao.getAllEntities().map { it.toExternalModel() } }
    }

    override fun getAllPropertiesFlow(): Flow<List<Location>> {
        return locationDao.getAllEntitiesFlow().map { lst -> lst.map { it.toExternalModel() } }
    }
}