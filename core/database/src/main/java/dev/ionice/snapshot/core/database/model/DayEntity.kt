package dev.ionice.snapshot.core.database.model

import androidx.room.*
import dev.ionice.snapshot.core.model.Day
import java.time.LocalDate

data class DayEntity(
    @Embedded
    val properties: DayProperties,
    // DayProperties.id, need to specify when writing SQL query
    @Relation(parentColumn = "id", entityColumn = "dayId")
    val tags: List<TagEntryEntity>,
    @Relation(parentColumn = "id", entityColumn = "dayId")
    val location: LocationEntryEntity?
)

fun DayEntity.toExternalModel(): Day {
    val (id, summary, createdAt, lastModifiedAt, isFavorite) = properties
    return Day(
        id,
        summary,
        createdAt,
        lastModifiedAt,
        isFavorite,
        null,
        emptyList()
    )
}


@Entity(tableName = "Day")
data class DayProperties(
    @PrimaryKey
    val id: Long = LocalDate.now().toEpochDay(),
    val summary: String,
    val createdAt: Long,         // epoch second
    val lastModifiedAt: Long,    // epoch second
    val isFavorite: Boolean = false,
    @Embedded
    val date: Date
)

@Entity
@Fts4(contentEntity = DayProperties::class)
data class DaySummaryFts(
    val id: Long,
    val summary: String
)

data class Date(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int
)