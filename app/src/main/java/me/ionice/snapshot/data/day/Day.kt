package me.ionice.snapshot.data.day

import androidx.room.*
import me.ionice.snapshot.data.metric.MetricEntry
import java.time.LocalDate

@Entity(tableName = "day_entry")
data class Day(
    // TODO: rename to DayCore? since this is the 'core'; add Date as an attribute, remove location
    @PrimaryKey
    val id: Long = LocalDate.now().toEpochDay(),
    val summary: String = "",
    val location: String = ""
)

data class DayWithMetrics(
    // TODO: rename to Day, add Location as an attribute
    @Embedded
    val core: Day,
    @Relation(
        parentColumn = "id",
        entityColumn = "day_id"
    )
    val metrics: List<MetricEntry>
)

@Entity(tableName = "location", indices = [Index(value = ["lon", "lat"], unique = true)])
data class Location(
    @PrimaryKey
    val name: String,
    @Embedded
    val coordinates: Coordinates?,
    val createdAt: Long, // epoch second
    val lastUsedAt: Long // epoch second
)

data class Coordinates(
    val lon: Long,
    val lat: Long
)

data class Date(
    val year: Int,
    val month: Int,
    val day: Int
)

