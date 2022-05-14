package me.ionice.snapshot.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate

@Entity(tableName = "day_entry")
data class Day(

    @PrimaryKey
    var id: Long = LocalDate.now().toEpochDay(),
    var summary: String = "",
    var location: String? = null
)

data class DayWithMetrics(
    @Embedded val day: Day,
    @Relation(
        parentColumn = "id",
        entityColumn = "day_id"
    )
    val metrics: MutableList<MetricEntry>
)