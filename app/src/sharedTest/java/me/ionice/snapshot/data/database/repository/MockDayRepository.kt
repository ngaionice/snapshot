package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import me.ionice.snapshot.data.database.model.*
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class MockDayRepository : DayRepository {

    private val now = LocalDate.now()
    private val day = MutableStateFlow(
        Day(
            properties = DayProperties(
                summary = "",
                createdAt = Instant.now().epochSecond,
                lastModifiedAt = Instant.now().epochSecond,
                date = Date(now.year, now.monthValue, now.dayOfMonth)
            ),
            tags = emptyList(),
            location = null
        )
    )

    override suspend fun get(dayId: Long): Day? {
        return if (dayId == 0L) {
            null
        } else {
            day.value.copy(properties = day.value.properties.copy(id = dayId))
        }
    }

    override suspend fun create(dayId: Long) {
        val date = LocalDate.ofEpochDay(dayId)
        day.value = Day(
            properties = DayProperties(
                id = dayId,
                summary = "",
                createdAt = Instant.now().epochSecond,
                lastModifiedAt = Instant.now().epochSecond,
                date = Date(date.year, date.monthValue, date.dayOfMonth)
            ),
            tags = emptyList(),
            location = null
        )
    }

    override suspend fun update(
        dayId: Long,
        summary: String,
        isFavorite: Boolean,
        location: LocationEntry?,
        tags: List<TagEntry>
    ) {
        day.value = day.value.copy(
            properties = DayProperties(
                summary = "",
                createdAt = Instant.now().epochSecond,
                lastModifiedAt = Instant.now().epochSecond,
                date = Date(now.year, now.monthValue, now.dayOfMonth)
            )
        )
    }

    override fun getFlow(dayId: Long): Flow<Day?> = day

    override fun getListFlowByYear(year: Int): Flow<List<Day>> =
        day.flatMapLatest { flowOf(listOf(it)) }

    override fun getListFlowInIdRange(start: Long, end: Long): Flow<List<Day>> {
        return emptyFlow()
    }

    override fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<Day>> {
        return emptyFlow()
    }
}