package dev.ionice.snapshot.testtools.data.database.repository

import dev.ionice.snapshot.core.data.repository.LocationRepository
import dev.ionice.snapshot.core.model.Coordinates
import dev.ionice.snapshot.core.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update

class FakeLocationRepository : LocationRepository {
    private val backingFlow = FakeRepositoryData.locationBackingFlow
    private var lastUsedId = -1L
//    override suspend fun get(locationId: Long): PopulatedLocation? =
//        backingFlow.value.find { it.id == locationId }

    override suspend fun add(coordinates: Coordinates, name: String): Long {
        lastUsedId++
        backingFlow.update {
            it + Location(
                id = lastUsedId, coordinates = coordinates, name = name, lastUsedAt = 0
            )
        }
        return lastUsedId
    }

    override suspend fun getAllProperties(): List<Location> {
        return backingFlow.value
    }

    override fun getAllPropertiesFlow(): Flow<List<Location>> {
        return backingFlow
    }

    /**
     * A test-only API for sending data to the backing flow to simulate the flow returning results successfully
     */
    fun sendLocations(locations: List<Location>) {
        backingFlow.tryEmit(locations)
    }
}