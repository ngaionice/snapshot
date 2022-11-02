package dev.ionice.snapshot.core.model

data class Location(
    val id: Long,
    val name: String,
    val coordinates: Coordinates,
    val lastUsedAt: Long,
    val entries: List<LocationEntry>
)

data class LocationEntry(
    val dayId: Long,
    val locationId: Long
)

data class Coordinates(val lat: Double, val lon: Double)