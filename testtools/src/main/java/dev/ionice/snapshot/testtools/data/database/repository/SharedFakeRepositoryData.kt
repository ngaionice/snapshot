package dev.ionice.snapshot.testtools.data.database.repository

import dev.ionice.snapshot.core.model.*
import kotlinx.coroutines.flow.MutableStateFlow

object FakeRepositoryData {

    // Aug 1/2 2021, Aug 1 2022
    val dayIds = listOf<Long>(18840, 18841, 19205, 19206, 19207)
    const val locationId = 9999L
    const val tagId = 9999L

    val locationSourceData = listOf(
        Location(
            id = locationId,
            coordinates = Coordinates(0.0, 0.0),
            name = "FakeLocation",
            lastUsedAt = 0
        ),
        Location(
            id = locationId + 1,
            coordinates = Coordinates(1.0, 1.0),
            name = "NewFakeLocation",
            lastUsedAt = 0
        ),
        Location(
            id = locationId + 2,
            coordinates = Coordinates(2.0, 2.0),
            name = "AnotherFakeLocation",
            lastUsedAt = 0
        )
    )

    val tagSourceData = listOf(
        Tag(
            id = tagId,
            name = "FakeTag",
            lastUsedAt = 0
        ),
        Tag(
            id = tagId + 1,
            name = "NewFakeTag",
            lastUsedAt = 0
        ),
        Tag(
            id = tagId + 2,
            name = "AnotherFakeTag",
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
        ),
        Day(
            id = dayIds[3],
            summary = "New fake summary",
            createdAt = 0,
            lastModifiedAt = 0,
            isFavorite = false,
            tags = listOf(ContentTag(tagSourceData[1])),
            location = locationSourceData[1]
        ),
        Day(
            id = dayIds[4],
            summary = "Another fake summary ",
            createdAt = 0,
            lastModifiedAt = 0,
            isFavorite = false,
            tags = listOf(ContentTag(tagSourceData[2])),
            location = locationSourceData[2]
        )
    )

    val dayBackingFlow = MutableStateFlow(daySourceData.reversed())
    val locationBackingFlow = MutableStateFlow(locationSourceData)
    val tagBackingFlow = MutableStateFlow(tagSourceData)
}

typealias FRD = FakeRepositoryData