package dev.ionice.snapshot.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "LocationEntry",
    primaryKeys = ["dayId", "locationId"],
    foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index("dayId"), Index("locationId")]
)
data class DayLocationCrossRef(
    val dayId: Long,
    val locationId: Long
)