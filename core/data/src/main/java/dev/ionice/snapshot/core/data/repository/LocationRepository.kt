package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.model.Coordinates
import dev.ionice.snapshot.core.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun add(coordinates: Coordinates, name: String): Long

    suspend fun getAllProperties(): List<Location>

    fun getAllPropertiesFlow(): Flow<List<Location>>
}