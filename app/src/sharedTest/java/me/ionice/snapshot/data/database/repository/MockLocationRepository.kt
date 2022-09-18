package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import me.ionice.snapshot.data.database.model.Coordinates
import me.ionice.snapshot.data.database.model.Location
import me.ionice.snapshot.data.database.model.LocationEntry
import me.ionice.snapshot.data.database.model.LocationProperties
import java.time.Instant

class MockLocationRepository : LocationRepository {
    private val location = MutableStateFlow<Location?>(null)
    override suspend fun get(locationId: Long): Location? = location.value

    override suspend fun add(coordinates: Coordinates, name: String): Long {
        val id = (1..10L).random()
        location.value =
            Location(
                properties = LocationProperties(
                    id = id,
                    coordinates = coordinates,
                    name = name,
                    lastUsedAt = 0
                ), entries = emptyList()
            )
        return id
    }

    override suspend fun update(
        locationId: Long,
        coordinates: Coordinates,
        name: String,
        entries: List<LocationEntry>
    ) {
        location.value =
            Location(
                properties = LocationProperties(
                    id = locationId,
                    coordinates = coordinates,
                    name = name,
                    lastUsedAt = Instant.now().epochSecond
                ), entries = entries
            )
    }

    override suspend fun getAllProperties(): List<LocationProperties> {
        return location.value?.let { listOf(it.properties)} ?: emptyList()
    }

    override fun getAllPropertiesFlow(): Flow<List<LocationProperties>> {
        return flowOf(location.value?.let { listOf(it.properties)} ?: emptyList())
    }
}