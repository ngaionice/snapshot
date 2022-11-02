package dev.ionice.snapshot.core.database.model

import androidx.room.*
import dev.ionice.snapshot.core.model.Coordinates
import dev.ionice.snapshot.core.model.Location
import dev.ionice.snapshot.core.model.LocationEntry

data class LocationEntity(
    @Embedded
    val properties: LocationPropertiesEntity,
    @Relation(parentColumn = "id", entityColumn = "locationId")
    val entries: List<LocationEntryEntity>
)

fun LocationEntity.toExternalModel(): Location {
    val (id, coordinates, name, lastUsedAt) = properties
    return Location(
        id,
        name,
        coordinates.toExternalModel(),
        lastUsedAt,
        entries.map { it.toExternalModel() }
    )
}

@Entity(indices = [Index(value = ["lon", "lat"], unique = true)], tableName = "Location")
data class LocationPropertiesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @Embedded
    val coordinates: CoordinatesEntity,
    val name: String,
    val lastUsedAt: Long
)

@Entity(
    tableName = "LocationEntry",
    primaryKeys = ["dayId", "locationId"],
    foreignKeys = [
        ForeignKey(
            entity = DayProperties::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocationPropertiesEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index("dayId"), Index("locationId")]
)
data class LocationEntryEntity(
    val dayId: Long,
    val locationId: Long
)

fun LocationEntryEntity.toExternalModel(): LocationEntry = LocationEntry(dayId, locationId)

/**
 * Accurate up to 6 decimal places.
 */
data class CoordinatesEntity(
    val lat: Double,
    val lon: Double
)

fun CoordinatesEntity.toExternalModel(): Coordinates = Coordinates(lat, lon)