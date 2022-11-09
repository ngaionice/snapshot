package dev.ionice.snapshot.core.database.model

import androidx.room.*
import dev.ionice.snapshot.core.model.Day
import java.time.LocalDate

@Entity(tableName = "Day")
data class DayEntity(
    @PrimaryKey
    val id: Long = LocalDate.now().toEpochDay(),
    val summary: String,
    val createdAt: Long,         // epoch second
    val lastModifiedAt: Long,    // epoch second
    val isFavorite: Boolean = false,
    @Embedded
    val date: Date
)

fun DayEntity.toExternalModel(): Day {
    return Day(
        id,
        summary,
        isFavorite,
        null,
        emptyList(),
        createdAt,
        lastModifiedAt
    )
}

@Entity
@Fts4(contentEntity = DayEntity::class)
data class DaySummaryFts(
    val id: Long,
    val summary: String
)

data class Date(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int
)