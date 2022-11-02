package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.database.model.CoordinatesEntity
import dev.ionice.snapshot.core.database.model.LocationEntity
import dev.ionice.snapshot.core.database.model.LocationEntryEntity
import dev.ionice.snapshot.core.database.model.LocationPropertiesEntity
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun get(locationId: Long): LocationEntity?

    suspend fun add(coordinates: CoordinatesEntity, name: String): Long

    suspend fun update(
        locationId: Long,
        coordinates: CoordinatesEntity,
        name: String,
        entries: List<LocationEntryEntity>
    )

    suspend fun getAllProperties(): List<LocationPropertiesEntity>

    fun getAllPropertiesFlow(): Flow<List<LocationPropertiesEntity>>
}