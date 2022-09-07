package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.Flow
import me.ionice.snapshot.data.database.model.Day
import me.ionice.snapshot.data.database.model.LocationEntry
import me.ionice.snapshot.data.database.model.TagEntry

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
}