package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.MutableStateFlow
import me.ionice.snapshot.data.database.model.*

object FakeRepositoryData {

    // Aug 1/2 2021, Aug 1 2022
    val dayIds = listOf<Long>(18840, 18841, 19205)
    const val locationId = 9999L
    const val tagId = 9999L

    val dayBackingFlow = MutableStateFlow(
        listOf(
            Day(
                properties = DayProperties(
                    id = FRD.dayIds[2],
                    summary = "Fake summary 2",
                    createdAt = 0,
                    lastModifiedAt = 0,
                    date = Date(2022, 8, 1)
                ),
                tags = emptyList(),
                location = null
            ),
            Day(
                properties = DayProperties(
                    id = FRD.dayIds[1],
                    summary = "Fake summary 1",
                    createdAt = 0,
                    lastModifiedAt = 0,
                    date = Date(2021, 8, 2)
                ),
                tags = emptyList(),
                location = null
            ),
            Day(
                properties = DayProperties(
                    id = FRD.dayIds[0],
                    summary = "Fake summary 0",
                    createdAt = 0,
                    lastModifiedAt = 0,
                    date = Date(2021, 8, 1)
                ),
                tags = listOf(TagEntry(FRD.dayIds[0], FRD.tagId)),
                location = LocationEntry(FRD.dayIds[0], FRD.locationId)
            )
        )
    )
    val locationBackingFlow = MutableStateFlow(listOf(
        Location(
            properties = LocationProperties(id = FRD.locationId, coordinates = Coordinates(0.0, 0.0), name = "FakeLocation", lastUsedAt = 0),
            entries = listOf(LocationEntry(FRD.dayIds[0], FRD.locationId))
        )
    ))
    val tagBackingFlow = MutableStateFlow(listOf(Tag(
        properties = TagProperties(id = FRD.tagId, name ="FakeTag", lastUsedAt = 0),
        entries = listOf(TagEntry(FRD.dayIds[0], FRD.tagId))
    )))
}

typealias FRD = FakeRepositoryData