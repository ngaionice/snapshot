package dev.ionice.snapshot.testtools.data.database.repository

import dev.ionice.snapshot.core.data.repository.DayRepository
import dev.ionice.snapshot.core.model.ContentTag
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.model.Location
import dev.ionice.snapshot.core.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class FakeDayRepository : DayRepository {

    private val backingFlow = FakeRepositoryData.dayBackingFlow

    override suspend fun create(dayId: Long) {
        if (backingFlow.value.any { it.id == dayId }) return
        val lst = backingFlow.value
        backingFlow.tryEmit((lst + Day(
            id = dayId,
            summary = "",
            createdAt = Instant.now().epochSecond,
            lastModifiedAt = Instant.now().epochSecond,
            isFavorite = false,
            tags = emptyList(),
            location = null
        )).sortedByDescending { it.id })
    }

    override suspend fun update(
        dayId: Long,
        summary: String,
        isFavorite: Boolean,
        location: Location?,
        tags: List<ContentTag>
    ) {
        val toUpdate = backingFlow.value.find { it.id == dayId } ?: return
        val toInsert = toUpdate.copy(
            id = dayId,
            summary = summary,
            isFavorite = isFavorite,
            createdAt = Instant.now().epochSecond,
            lastModifiedAt = Instant.now().epochSecond,
            tags = tags,
            location = location
        )
        val lst = backingFlow.value
        backingFlow.tryEmit((lst.filter { it.id != dayId } + toInsert).sortedByDescending { it.id })

        // TODO: need to update location + tags
    }

    override fun getFlow(dayId: Long): Flow<Day?> =
        backingFlow.map { it.find { day -> day.id == dayId } }

    override fun getListFlowByYear(year: Int): Flow<List<Day>> =
        backingFlow.map { it.filter { day -> day.date().year == year } }

    override fun getListFlowInIdRange(start: Long, end: Long): Flow<List<Day>> =
        backingFlow.map { it.filter { day -> day.id in start..end } }

    override fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<Day>> =
        backingFlow.map { it.filter { day -> day.date().monthValue == month && day.date().dayOfMonth == dayOfMonth } }

    override fun getListFlowForFavorites(): Flow<List<Day>> =
        backingFlow.map { it.filter { day -> day.isFavorite } }

    override fun getListFlowByTag(tagId: Long): Flow<List<Day>> =
        backingFlow.map { it.filter { day -> day.tags.any { tag -> tag.tag.id == tagId } } }

    override suspend fun search(
        queryString: String,
        startDayId: Long?,
        endDayId: Long?,
        isFavorite: Boolean?,
        searchTagEntries: Boolean,
        includedLocations: Set<Location>?,
        includedTags: Set<Tag>?
    ): List<Day> = backingFlow.value
        .asSequence()
        .filter {
            it.summary.contains(queryString) || if (searchTagEntries) it.tags.any { tag ->
                tag.content?.contains(
                    queryString,
                    ignoreCase = true
                ) ?: false
            } else false
        }
        .filter { (startDayId == null || startDayId <= it.id) && (endDayId == null || endDayId >= it.id) }
        .filter { (isFavorite == null || it.isFavorite) }
        .filter { (includedLocations == null || it.location != null && includedLocations.contains(it.location)) }
        .filter { (includedTags == null || it.tags.any { tag -> includedTags.contains(tag.tag) }) }
        .toList()

    /**
     * A test-only API for sending data to the backing flow to simulate the flow returning results successfully
     */
    fun sendDays(days: List<Day>) {
        backingFlow.tryEmit(days)
    }
}