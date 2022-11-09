package dev.ionice.snapshot.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.ionice.snapshot.core.model.ContentTag
import dev.ionice.snapshot.core.model.Day

data class PopulatedDay(
    @Embedded
    val properties: DayEntity,
    // DayProperties.id, need to specify when writing SQL query
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DayLocationCrossRef::class,
            parentColumn = "dayId",
            entityColumn = "locationId"
        )
    )
    val location: LocationEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DayTagCrossRef::class,
            parentColumn = "dayId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>,
    // compromise: some duplicate data is stored in this model since Junction does not support fetching data from the junction table
    @Relation(parentColumn = "id", entityColumn = "dayId")
    val tagContents: Set<DayTagCrossRef>
)

fun PopulatedDay.toExternalModel(): Day {
    val (id, summary, createdAt, lastModifiedAt, isFavorite) = properties
    val contentTags = tags.map {
        ContentTag(
            it.toExternalModel(),
            tagContents.find { crossRef -> crossRef.tagId == it.id }!!.content
        )
    }
    return Day(
        id,
        summary,
        isFavorite,
        location?.toExternalModel(),
        contentTags,
        createdAt,
        lastModifiedAt
    )
}