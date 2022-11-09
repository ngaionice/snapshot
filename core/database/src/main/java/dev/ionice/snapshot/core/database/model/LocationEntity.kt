package dev.ionice.snapshot.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.ionice.snapshot.core.model.Coordinates
import dev.ionice.snapshot.core.model.Location

@Entity(indices = [Index(value = ["lon", "lat"], unique = true)], tableName = "Location")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @Embedded
    val coordinates: CoordinatesEntity,
    val name: String,
    val lastUsedAt: Long
)

fun LocationEntity.toExternalModel(): Location {
    return Location(
        id,
        name,
        Coordinates(coordinates.lat, coordinates.lon),
        lastUsedAt
    )
}

/**
 * Accurate up to 6 decimal places.
 */
data class CoordinatesEntity(
    val lat: Double,
    val lon: Double
)