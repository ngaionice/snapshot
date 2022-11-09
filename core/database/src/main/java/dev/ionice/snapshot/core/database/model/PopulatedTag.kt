package dev.ionice.snapshot.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PopulatedTag(
    @Embedded
    val properties: TagEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DayTagCrossRef::class,
            parentColumn = "tagId",
            entityColumn = "dayId"
        )
    )
    val entries: List<DayEntity>
)