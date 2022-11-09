package dev.ionice.snapshot.testtools.data.database.repository

import dev.ionice.snapshot.core.model.*
import kotlinx.coroutines.flow.MutableStateFlow

object FakeRepositoryData {

    // Aug 1/2 2021, Aug 1 2022
    val dayIds = listOf<Long>(18840, 18841, 19205)
    const val locationId = 9999L
    const val tagId = 9999L

    val locationSourceData = listOf(
        Location(
            id = locationId,
            coordinates = Coordinates(0.0, 0.0),
            name = "FakeLocation",
            lastUsedAt = 0
        )
    )

    val tagSourceData = listOf(
        Tag(
            id = tagId,
            name = "FakeTag",
            lastUsedAt = 0
        )
    )

    val emptyDay = Day(
        id = dayIds[0],
        summary = "",
        createdAt = 0,
        lastModifiedAt = 0,
        isFavorite = false,
        tags = emptyList(),
        location = null
    )

    val filledDay = Day(
        id = dayIds[0],
        summary = "Fake summary 0",
        createdAt = 0,
        lastModifiedAt = 0,
        isFavorite = false,
        tags = listOf(ContentTag(tag = tagSourceData[0], content = null)),
        location = locationSourceData[0]
    )

    val daySourceData = listOf(
        Day(
            id = dayIds[0],
            summary = "Fake summary 0",
            createdAt = 0,
            lastModifiedAt = 0,
            isFavorite = false,
            tags = listOf(ContentTag(tag = tagSourceData[0], content = null)),
            location = locationSourceData[0]
        ),
        Day(
            id = dayIds[1],
            summary = "Fake summary 1",
            createdAt = 0,
            lastModifiedAt = 0,
            tags = emptyList(),
            location = null,
            isFavorite = false,
        ),
        Day(
            id = dayIds[2],
            summary = "Fake summary 2",
            createdAt = 0,
            lastModifiedAt = 0,
            isFavorite = true,
            tags = emptyList(),
            location = null
        )
    )

    val dayBackingFlow = MutableStateFlow(daySourceData.reversed())
    val locationBackingFlow = MutableStateFlow(locationSourceData)
    val tagBackingFlow = MutableStateFlow(tagSourceData)
}

typealias FRD = FakeRepositoryData