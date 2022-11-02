package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.database.model.DayEntity
import dev.ionice.snapshot.core.database.model.LocationEntryEntity
import dev.ionice.snapshot.core.database.model.TagEntryEntity
import kotlinx.coroutines.flow.Flow

interface DayRepository {

    suspend fun get(dayId: Long): DayEntity?

    suspend fun create(dayId: Long)

    suspend fun update(
        dayId: Long,
        summary: String,
        isFavorite: Boolean,
        location: LocationEntryEntity? = null,
        tags: List<TagEntryEntity>
    )

    fun getFlow(dayId: Long): Flow<DayEntity?>

    fun getListFlowByYear(year: Int): Flow<List<DayEntity>>

    fun getListFlowInIdRange(start: Long, end: Long): Flow<List<DayEntity>>

    fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<DayEntity>>

    fun getListFlowForFavorites(): Flow<List<DayEntity>>
}