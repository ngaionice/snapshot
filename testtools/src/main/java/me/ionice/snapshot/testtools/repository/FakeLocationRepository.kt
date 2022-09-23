package me.ionice.snapshot.testtools.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.ionice.snapshot.data.database.model.Coordinates
import me.ionice.snapshot.data.database.model.Location
import me.ionice.snapshot.data.database.model.LocationEntry
import me.ionice.snapshot.data.database.model.LocationProperties
import me.ionice.snapshot.data.database.repository.LocationRepository
import java.time.Instant

class FakeLocationRepository : LocationRepository {
    private val backingFlow = FakeRepositoryData.locationBackingFlow
    private var lastUsedId = -1L
    override suspend fun get(locationId: Long): Location? =
        backingFlow.value.find { it.properties.id == locationId }

    override suspend fun add(coordinates: Coordinates, name: String): Long {
        lastUsedId++
        backingFlow.update {
            it + Location(
                properties = LocationProperties(
                    id = lastUsedId, coordinates = coordinates, name = name, lastUsedAt = 0
                ), entries = emptyList()
            )
        }
        return lastUsedId
    }

    override suspend fun update(
        locationId: Long, coordinates: Coordinates, name: String, entries: List<LocationEntry>
    ) {
        val toUpdate = backingFlow.value.find { it.properties.id == locationId } ?: return
        val toInsert = toUpdate.copy(
            properties = LocationProperties(
                id = locationId,
                coordinates = coordinates,
                name = name,
                lastUsedAt = Instant.now().epochSecond
            ), entries = entries
        )
        backingFlow.update { curr ->
            (curr.filter { it.properties.id == locationId } + toInsert).sortedBy { it.properties.id }
        }
    }

    override suspend fun getAllProperties(): List<LocationProperties> {
        return backingFlow.value.map { it.properties }
    }

    override fun getAllPropertiesFlow(): Flow<List<LocationProperties>> {
        return backingFlow.map { lst -> lst.map { it.properties } }
    }

    /**
     * A test-only API for sending data to the backing flow to simulate the flow returning results successfully
     */
    fun sendLocations(locations: List<Location>) {
        backingFlow.tryEmit(locations)
    }
}