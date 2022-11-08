package dev.ionice.snapshot.core.model

data class Location(
    val id: Long,
    val name: String,
    val coordinates: Coordinates,
    val lastUsedAt: Long
)

data class Coordinates(val lat: Double, val lon: Double)