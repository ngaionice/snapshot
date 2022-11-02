package dev.ionice.snapshot.testtools.data.database.repository

import dev.ionice.snapshot.core.database.model.*
import kotlinx.coroutines.flow.MutableStateFlow

object FakeRepositoryData {

    // Aug 1/2 2021, Aug 1 2022
    val dayIds = listOf<Long>(18840, 18841, 19205)
    const val locationId = 9999L
    const val tagId = 9999L

    val emptyDay = DayEntity(
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

    val filledDay = DayEntity(
        properties = DayProperties(
            id = dayIds[0],
            summary = "Fake summary 0",
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(2021, 8, 1)
        ),
        tags = listOf(TagEntryEntity(dayIds[0], tagId)),
        location = LocationEntryEntity(dayIds[0], locationId)
    )

    val daySourceData = listOf(
        DayEntity(
            properties = DayProperties(
                id = dayIds[0],
                summary = "Fake summary 0",
                createdAt = 0,
                lastModifiedAt = 0,
                date = Date(2021, 8, 1)
            ),
            tags = listOf(TagEntryEntity(dayIds[0], tagId)),
            location = LocationEntryEntity(dayIds[0], locationId)
        ),
        DayEntity(
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
        DayEntity(
            properties = DayProperties(
                id = dayIds[2],
                summary = "Fake summary 2",
                createdAt = 0,
                lastModifiedAt = 0,
                isFavorite = true,
                date = Date(2022, 8, 1)
            ),
            tags = emptyList(),
            location = null
        )
    )

    val locationSourceData = listOf(
        LocationEntity(
            properties = LocationPropertiesEntity(
                id = locationId,
                coordinates = CoordinatesEntity(0.0, 0.0),
                name = "FakeLocation",
                lastUsedAt = 0
            ),
            entries = listOf(
                LocationEntryEntity(
                    dayIds[0],
                    locationId
                )
            )
        )
    )

    val tagSourceData = listOf(
        TagEntity(
            properties = TagPropertiesEntity(
                id = tagId,
                name = "FakeTag",
                lastUsedAt = 0
            ),
            entries = listOf(TagEntryEntity(dayIds[0], tagId))
        )
    )

    val dayBackingFlow = MutableStateFlow(daySourceData.reversed())
    val locationBackingFlow = MutableStateFlow(locationSourceData)
    val tagBackingFlow = MutableStateFlow(tagSourceData)
}

typealias FRD = FakeRepositoryData