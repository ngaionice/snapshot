package dev.ionice.snapshot.testtools.data.database.repository

import dev.ionice.snapshot.core.database.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate

class FakeDayRepository : dev.ionice.snapshot.core.data.repository.DayRepository {

    private val now = LocalDate.now()
    private val backingFlow = FakeRepositoryData.dayBackingFlow

    override suspend fun get(dayId: Long): DayEntity? {
        return backingFlow.value.find { it.properties.id == dayId }
    }

    override suspend fun create(dayId: Long) {
        if (backingFlow.value.any { it.properties.id == dayId }) return
        val date = LocalDate.ofEpochDay(dayId)
        val lst = backingFlow.value
        backingFlow.tryEmit((lst + DayEntity(
            properties = DayProperties(
                id = dayId,
                summary = "",
                createdAt = Instant.now().epochSecond,
                lastModifiedAt = Instant.now().epochSecond,
                date = Date(
                    date.year,
                    date.monthValue,
                    date.dayOfMonth
                )
            ),
            tags = emptyList(),
            location = null
        )).sortedByDescending { it.properties.id })
    }

    override suspend fun update(
        dayId: Long,
        summary: String,
        isFavorite: Boolean,
        location: LocationEntryEntity?,
        tags: List<TagEntryEntity>
    ) {
        val toUpdate = backingFlow.value.find { it.properties.id == dayId } ?: return
        val toInsert = toUpdate.copy(
            properties = DayProperties(
                id = dayId,
                summary = summary,
                isFavorite = isFavorite,
                createdAt = Instant.now().epochSecond,
                lastModifiedAt = Instant.now().epochSecond,
                date = Date(
                    now.year,
                    now.monthValue,
                    now.dayOfMonth
                )
            ),
            tags = tags,
            location = location
        )
        val lst = backingFlow.value
        backingFlow.tryEmit((lst.filter { it.properties.id != dayId } + toInsert).sortedByDescending { it.properties.id })

        // TODO: need to update location + tags
    }

    override fun getFlow(dayId: Long): Flow<DayEntity?> =
        backingFlow.map { it.find { day -> day.properties.id == dayId } }

    override fun getListFlowByYear(year: Int): Flow<List<DayEntity>> =
        backingFlow.map { it.filter { day -> day.properties.date.year == year } }

    override fun getListFlowInIdRange(start: Long, end: Long): Flow<List<DayEntity>> =
        backingFlow.map { it.filter { day -> day.properties.id in start..end } }

    override fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<DayEntity>> =
        backingFlow.map { it.filter { day -> day.properties.date.month == month && day.properties.date.dayOfMonth == dayOfMonth } }

    override fun getListFlowForFavorites(): Flow<List<DayEntity>> =
        backingFlow.map { it.filter { day -> day.properties.isFavorite } }

    /**
     * A test-only API for sending data to the backing flow to simulate the flow returning results successfully
     */
    fun sendDays(days: List<DayEntity>) {
        backingFlow.tryEmit(days)
    }
}