package dev.ionice.snapshot.data.database.repository

import dev.ionice.snapshot.core.database.model.Day
import dev.ionice.snapshot.core.database.model.LocationEntry
import dev.ionice.snapshot.core.database.model.TagEntry
import kotlinx.coroutines.flow.Flow

interface DayRepository {

    suspend fun get(dayId: Long): Day?

    suspend fun create(dayId: Long)

    suspend fun update(
        dayId: Long,
        summary: String,
        isFavorite: Boolean,
        location: LocationEntry? = null,
        tags: List<TagEntry>
    )

    fun getFlow(dayId: Long): Flow<Day?>

    fun getListFlowByYear(year: Int): Flow<List<Day>>

    fun getListFlowInIdRange(start: Long, end: Long): Flow<List<Day>>

    fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<Day>>

    fun getListFlowForFavorites(): Flow<List<Day>>
}