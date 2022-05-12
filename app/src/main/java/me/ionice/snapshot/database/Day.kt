package me.ionice.snapshot.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate
import java.util.*

@Entity(tableName = "day_entry")
data class Day(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    val date: LocalDate = LocalDate.now(),
    var summary: String = "",
    var location: String? = null
)

data class DayWithMetricEntries(
    @Embedded val day: Day,
    @Relation(
        parentColumn = "id",
        entityColumn = "day_id"
    )
    val metrics: List<MetricEntry>
)