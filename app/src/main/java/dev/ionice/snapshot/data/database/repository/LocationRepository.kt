package dev.ionice.snapshot.data.database.repository

import dev.ionice.snapshot.core.database.model.Coordinates
import dev.ionice.snapshot.core.database.model.Location
import dev.ionice.snapshot.core.database.model.LocationEntry
import dev.ionice.snapshot.core.database.model.LocationProperties
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun get(locationId: Long): Location?

    suspend fun add(coordinates: Coordinates, name: String): Long

    suspend fun update(
        locationId: Long,
        coordinates: Coordinates,
        name: String,
        entries: List<LocationEntry>
    )

    suspend fun getAllProperties(): List<LocationProperties>

    fun getAllPropertiesFlow(): Flow<List<LocationProperties>>
}