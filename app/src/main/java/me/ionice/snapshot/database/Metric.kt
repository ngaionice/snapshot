package me.ionice.snapshot.database

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "metric_key")
data class MetricKey(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var name: String
) : Parcelable

@Parcelize
@Entity(tableName = "metric_entry", primaryKeys = ["metric_id", "day_id"])
data class MetricEntry(
    @ColumnInfo(name = "metric_id")
    val metricId: Long,

    @ColumnInfo(name = "day_id")
    val dayId: Long,

    val value: String = ""
) : Parcelable

data class Metric(
    @Embedded val key: MetricKey,
    @Relation(
        parentColumn = "id",
        entityColumn = "metric_id"
    )
    val entries: List<MetricEntry>
)