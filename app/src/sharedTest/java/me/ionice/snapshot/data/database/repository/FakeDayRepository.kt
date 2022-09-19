package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.ionice.snapshot.data.database.model.*
import java.time.Instant
import java.time.LocalDate

class FakeDayRepository : DayRepository {

    private val now = LocalDate.now()
    private val days = MutableStateFlow(listOf<Day>())

    override suspend fun get(dayId: Long): Day? {
        return days.value.find { it.properties.id == dayId }
    }

    override suspend fun create(dayId: Long) {
        if (days.value.any { it.properties.id == dayId }) return
        val date = LocalDate.ofEpochDay(dayId)
        days.update { days -> (days + Day(
            properties = DayProperties(
                id = dayId,
                summary = "",
                createdAt = Instant.now().epochSecond,
                lastModifiedAt = Instant.now().epochSecond,
                date = Date(date.year, date.monthValue, date.dayOfMonth)
            ),
            tags = emptyList(),
            location = null
        )).sortedBy { it.properties.id } }
    }

    override suspend fun update(
        dayId: Long,
        summary: String,
        isFavorite: Boolean,
        location: LocationEntry?,
        tags: List<TagEntry>
    ) {
        val toUpdate = days.value.find { it.properties.id == dayId } ?: return
        val toInsert = toUpdate.copy(
            properties = DayProperties(
                summary = "",
                createdAt = Instant.now().epochSecond,
                lastModifiedAt = Instant.now().epochSecond,
                date = Date(now.year, now.monthValue, now.dayOfMonth)
            ),
            tags = tags,
            location = location
        )
        days.update { lst -> (lst.filter { it.properties.id == dayId } + toInsert).sortedBy { it.properties.id } }
    }

    override fun getFlow(dayId: Long): Flow<Day?> =
        days.map { it.find { day -> day.properties.id == dayId } }

    override fun getListFlowByYear(year: Int): Flow<List<Day>> =
        days.map { it.filter { day -> day.properties.date.year == year } }

    override fun getListFlowInIdRange(start: Long, end: Long): Flow<List<Day>> =
        days.map { it.filter { day -> day.properties.id in start..end } }

    override fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<Day>> =
        days.map { it.filter { day -> day.properties.date.month == month && day.properties.date.dayOfMonth == dayOfMonth } }
}