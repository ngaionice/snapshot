package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.database.dao.DayDao
import dev.ionice.snapshot.core.database.dao.LocationDao
import dev.ionice.snapshot.core.database.dao.TagDao
import dev.ionice.snapshot.core.database.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate

class OfflineDayRepository(
    private val dispatcher: CoroutineDispatcher,
    private val dayDao: DayDao,
    private val locationDao: LocationDao,
    private val tagDao: TagDao
) : DayRepository {

    override suspend fun get(dayId: Long): DayEntity? {
        return withContext(dispatcher) { dayDao.get(dayId) }
    }

    override suspend fun create(dayId: Long) {
        val currentTime = Instant.now().epochSecond
        val date = LocalDate.ofEpochDay(dayId)
        dayDao.insertProperties(
            DayProperties(
                id = dayId,
                summary = "",
                createdAt = currentTime,
                lastModifiedAt = currentTime,
                isFavorite = false,
                date = Date(date.year, date.monthValue, date.dayOfMonth)
            )
        )
    }

    override suspend fun update(
        dayId: Long,
        summary: String,
        isFavorite: Boolean,
        location: LocationEntryEntity?,
        tags: List<TagEntryEntity>
    ) {
        withContext(dispatcher) {
            val existingEntry = dayDao.get(dayId)
            val currentTime = Instant.now().epochSecond
            if (existingEntry == null) {
                throw IllegalArgumentException("No existing Day found for the given dayId: $dayId")
            } else {
                dayDao.updateProperties(
                    existingEntry.properties.copy(
                        summary = summary, isFavorite = isFavorite, lastModifiedAt = currentTime
                    )
                )
                // compare location, if not equal then need to delete old and insert new
                val oldLoc = existingEntry.location
                if (oldLoc != location) {
                    println("inserting location")
                    println(location)
                    oldLoc?.let { locationDao.deleteEntry(it) }
                    location?.let { locationDao.insertEntry(it) }
                }
                // compare tags: delete old ones, insert new ones, update common ones
                val newTags = tags.associateBy({ it.tagId }, { it.content })
                val oldTags = existingEntry.tags.associateBy({ it.tagId }, { it.content })
                (oldTags.keys subtract newTags.keys).map { TagEntryEntity(dayId, it, oldTags[it]) }
                    .let { tagDao.deleteEntries(it) }
                (newTags.keys subtract oldTags.keys).map { TagEntryEntity(dayId, it, newTags[it]) }
                    .let { tagDao.insertEntries(it) }
                (newTags.keys intersect oldTags.keys).filter { newTags[it] != oldTags[it] }
                    .map { TagEntryEntity(dayId, it, newTags[it]) }.let { tagDao.updateEntries(it) }
            }
        }
    }

    override fun getFlow(dayId: Long): Flow<DayEntity?> {
        return dayDao.getFlow(dayId)
    }

    override fun getListFlowByYear(year: Int): Flow<List<DayEntity>> {
        return dayDao.getListFlowByYear(year)
    }

    override fun getListFlowInIdRange(start: Long, end: Long): Flow<List<DayEntity>> {
        return dayDao.getListFlowByIdRange(start, end)
    }

    override fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<DayEntity>> {
        return dayDao.getListFlowByDayOfYear(month, dayOfMonth)
    }

    override fun getListFlowForFavorites(): Flow<List<DayEntity>> {
        return dayDao.getListFlowForFavorites()
    }
}