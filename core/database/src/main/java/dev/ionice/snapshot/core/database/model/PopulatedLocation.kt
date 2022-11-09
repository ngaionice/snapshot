package dev.ionice.snapshot.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PopulatedLocation(
    @Embedded
    val properties: LocationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DayLocationCrossRef::class,
            parentColumn = "locationId",
            entityColumn = "dayId"
        )
    )
    val entries: List<DayEntity>
)
