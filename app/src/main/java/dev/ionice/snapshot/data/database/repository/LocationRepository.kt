package dev.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.Flow
import dev.ionice.snapshot.data.database.model.Coordinates
import dev.ionice.snapshot.data.database.model.Location
import dev.ionice.snapshot.data.database.model.LocationEntry
import dev.ionice.snapshot.data.database.model.LocationProperties

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