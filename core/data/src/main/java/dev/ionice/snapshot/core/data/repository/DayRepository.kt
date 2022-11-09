package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.model.ContentTag
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.model.Location
import kotlinx.coroutines.flow.Flow

interface DayRepository {

    suspend fun create(dayId: Long)

    suspend fun update(
        dayId: Long,
        summary: String,
        isFavorite: Boolean,
        location: Location? = null,
        tags: List<ContentTag>
    )

    fun getFlow(dayId: Long): Flow<Day?>

    fun getListFlowByYear(year: Int): Flow<List<Day>>

    fun getListFlowInIdRange(start: Long, end: Long): Flow<List<Day>>

    fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<Day>>

    fun getListFlowForFavorites(): Flow<List<Day>>
}