package dev.ionice.snapshot.testtools.data.database.repository

import dev.ionice.snapshot.core.database.model.CoordinatesEntity
import dev.ionice.snapshot.core.database.model.LocationEntity
import dev.ionice.snapshot.core.database.model.LocationEntryEntity
import dev.ionice.snapshot.core.database.model.LocationPropertiesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant

class FakeLocationRepository : dev.ionice.snapshot.core.data.repository.LocationRepository {
    private val backingFlow = FakeRepositoryData.locationBackingFlow
    private var lastUsedId = -1L
    override suspend fun get(locationId: Long): LocationEntity? =
        backingFlow.value.find { it.properties.id == locationId }

    override suspend fun add(coordinates: CoordinatesEntity, name: String): Long {
        lastUsedId++
        backingFlow.update {
            it + LocationEntity(
                properties = LocationPropertiesEntity(
                    id = lastUsedId, coordinates = coordinates, name = name, lastUsedAt = 0
                ), entries = emptyList()
            )
        }
        return lastUsedId
    }

    override suspend fun update(
        locationId: Long, coordinates: CoordinatesEntity, name: String, entries: List<LocationEntryEntity>
    ) {
        val toUpdate = backingFlow.value.find { it.properties.id == locationId } ?: return
        val toInsert = toUpdate.copy(
            properties = LocationPropertiesEntity(
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

    override suspend fun getAllProperties(): List<LocationPropertiesEntity> {
        return backingFlow.value.map { it.properties }
    }

    override fun getAllPropertiesFlow(): Flow<List<LocationPropertiesEntity>> {
        return backingFlow.map { lst -> lst.map { it.properties } }
    }

    /**
     * A test-only API for sending data to the backing flow to simulate the flow returning results successfully
     */
    fun sendLocations(locations: List<LocationEntity>) {
        backingFlow.tryEmit(locations)
    }
}