package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.ionice.snapshot.data.database.model.Coordinates
import me.ionice.snapshot.data.database.model.Location
import me.ionice.snapshot.data.database.model.LocationEntry
import me.ionice.snapshot.data.database.model.LocationProperties
import java.time.Instant

class FakeLocationRepository : LocationRepository {
    private val locations = MutableStateFlow<List<Location>>(emptyList())
    private var lastUsedId = -1L
    override suspend fun get(locationId: Long): Location? =
        locations.value.find { it.properties.id == locationId }

    override suspend fun add(coordinates: Coordinates, name: String): Long {
        lastUsedId++
        locations.update {
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
        val toUpdate = locations.value.find { it.properties.id == locationId } ?: return
        val toInsert = toUpdate.copy(
            properties = LocationProperties(
                id = locationId,
                coordinates = coordinates,
                name = name,
                lastUsedAt = Instant.now().epochSecond
            ), entries = entries
        )
        locations.update { curr ->
            (curr.filter { it.properties.id == locationId } + toInsert).sortedBy { it.properties.id }
        }
    }

    override suspend fun getAllProperties(): List<LocationProperties> {
        return locations.value.map { it.properties }
    }

    override fun getAllPropertiesFlow(): Flow<List<LocationProperties>> {
        return locations.map { lst -> lst.map { it.properties } }
    }
}