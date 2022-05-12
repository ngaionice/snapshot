package me.ionice.snapshot.database

import androidx.room.*
import java.util.*

@Entity(tableName = "metric_key")
class Metric {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    @ColumnInfo(name = "name")
    var name: String = ""
}

@Entity(tableName = "metric_entry", primaryKeys = ["metric_id", "date"])
class MetricEntry(@ColumnInfo(name = "metric_id") val metricId: Long) {

    @ColumnInfo(name = "date")
    val date: Date = Calendar.getInstance().time

}

data class MetricWithEntries(
    @Embedded val metric: Metric,
    @Relation(
        parentColumn = "id",
        entityColumn = "metric_id"
    )
    val entries: List<MetricEntry>
)