package me.ionice.snapshot.testtools.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.ionice.snapshot.data.database.model.*
import me.ionice.snapshot.data.database.repository.DayRepository
import java.time.Instant
import java.time.LocalDate

class FakeDayRepository : DayRepository {

    private val now = LocalDate.now()
    private val backingFlow = FakeRepositoryData.dayBackingFlow

    override suspend fun get(dayId: Long): Day? {
        return backingFlow.value.find { it.properties.id == dayId }
    }

    override suspend fun create(dayId: Long) {
        if (backingFlow.value.any { it.properties.id == dayId }) return
        val date = LocalDate.ofEpochDay(dayId)
        val lst = backingFlow.value
        backingFlow.tryEmit((lst + Day(
            properties = DayProperties(
                id = dayId,
                summary = "",
                createdAt = Instant.now().epochSecond,
                lastModifiedAt = Instant.now().epochSecond,
                date = Date(date.year, date.monthValue, date.dayOfMonth)
            ),
            tags = emptyList(),
            location = null
        )).sortedByDescending { it.properties.id })
    }

    override suspend fun update(
        dayId: Long,
        summary: String,
        isFavorite: Boolean,
        location: LocationEntry?,
        tags: List<TagEntry>
    ) {
        val toUpdate = backingFlow.value.find { it.properties.id == dayId } ?: return
        val toInsert = toUpdate.copy(
            properties = DayProperties(
                id = dayId,
                summary = summary,
                isFavorite = isFavorite,
                createdAt = Instant.now().epochSecond,
                lastModifiedAt = Instant.now().epochSecond,
                date = Date(now.year, now.monthValue, now.dayOfMonth)
            ),
            tags = tags,
            location = location
        )
        val lst = backingFlow.value
        backingFlow.tryEmit((lst.filter { it.properties.id != dayId } + toInsert).sortedByDescending { it.properties.id })

        // TODO: need to update location + tags
    }

    override fun getFlow(dayId: Long): Flow<Day?> =
        backingFlow.map { it.find { day -> day.properties.id == dayId } }

    override fun getListFlowByYear(year: Int): Flow<List<Day>> =
        backingFlow.map { it.filter { day -> day.properties.date.year == year } }

    override fun getListFlowInIdRange(start: Long, end: Long): Flow<List<Day>> =
        backingFlow.map { it.filter { day -> day.properties.id in start..end } }

    override fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<Day>> =
        backingFlow.map { it.filter { day -> day.properties.date.month == month && day.properties.date.dayOfMonth == dayOfMonth } }

    /**
     * A test-only API for sending data to the backing flow to simulate the flow returning results successfully
     */
    fun sendDays(days: List<Day>) {
        backingFlow.tryEmit(days)
    }
}