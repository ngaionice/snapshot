package me.ionice.snapshot.data.database.model

import androidx.room.*

data class Location(
    @Embedded
    val properties: LocationProperties,
    @Relation(parentColumn = "id", entityColumn = "locationId")
    val entries: List<LocationEntry>
)

@Entity(indices = [Index(value = ["lon", "lat"], unique = true)], tableName = "Location")
data class LocationProperties(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @Embedded
    val coordinates: Coordinates,
    val name: String,
    val lastUsedAt: Long
)

@Entity(
    primaryKeys = ["dayId", "locationId"],
    foreignKeys = [
        ForeignKey(
            entity = DayProperties::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocationProperties::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index("dayId"), Index("locationId")]
)
data class LocationEntry(
    val dayId: Long,
    val locationId: Long
)

/**
 * Accurate up to 6 decimal places.
 */
data class Coordinates(
    val lat: Double,
    val lon: Double
)