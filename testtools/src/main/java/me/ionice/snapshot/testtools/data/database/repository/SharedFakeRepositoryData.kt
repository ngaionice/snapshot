package me.ionice.snapshot.testtools.data.database.repository

import kotlinx.coroutines.flow.MutableStateFlow
import me.ionice.snapshot.data.database.model.*

object FakeRepositoryData {

    // Aug 1/2 2021, Aug 1 2022
    val dayIds = listOf<Long>(18840, 18841, 19205)
    const val locationId = 9999L
    const val tagId = 9999L

    val emptyDay = Day(
        properties = DayProperties(
            id = dayIds[0],
            summary = "",
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(2021, 8, 1)
        ),
        tags = emptyList(),
        location = null
    )

    val filledDay = Day(
        properties = DayProperties(
            id = dayIds[0],
            summary = "Fake summary 0",
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(2021, 8, 1)
        ),
        tags = listOf(TagEntry(dayIds[0], tagId)),
        location = LocationEntry(dayIds[0], locationId)
    )

    val daySourceData = listOf(
        Day(
            properties = DayProperties(
                id = dayIds[0],
                summary = "Fake summary 0",
                createdAt = 0,
                lastModifiedAt = 0,
                date = Date(2021, 8, 1)
            ),
            tags = listOf(TagEntry(dayIds[0], tagId)),
            location = LocationEntry(dayIds[0], locationId)
        ),
        Day(
            properties = DayProperties(
                id = dayIds[1],
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
                id = dayIds[2],
                summary = "Fake summary 2",
                createdAt = 0,
                lastModifiedAt = 0,
                date = Date(2022, 8, 1)
            ),
            tags = emptyList(),
            location = null
        )
    )

    val locationSourceData = listOf(
        Location(
            properties = LocationProperties(
                id = locationId,
                coordinates = Coordinates(0.0, 0.0),
                name = "FakeLocation",
                lastUsedAt = 0
            ),
            entries = listOf(LocationEntry(dayIds[0], locationId))
        )
    )

    val tagSourceData = listOf(
        Tag(
            properties = TagProperties(id = tagId, name = "FakeTag", lastUsedAt = 0),
            entries = listOf(TagEntry(dayIds[0], tagId))
        )
    )

    val dayBackingFlow = MutableStateFlow(daySourceData.reversed())
    val locationBackingFlow = MutableStateFlow(locationSourceData)
    val tagBackingFlow = MutableStateFlow(tagSourceData)
}

typealias FRD = FakeRepositoryData