package me.ionice.snapshot.database

import androidx.room.*

@Entity(tableName = "metric_key")
data class Metric(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var name: String = ""
)

@Entity(tableName = "metric_entry", primaryKeys = ["metric_id", "day_id"])
data class MetricEntry(
    @ColumnInfo(name = "metric_id")
    val metricId: Long,

    @ColumnInfo(name = "day_id")
    val dayId: Long
)

data class MetricWithEntries(
    @Embedded val metric: Metric,
    @Relation(
        parentColumn = "id",
        entityColumn = "metric_id"
    )
    val entries: List<MetricEntry>
)