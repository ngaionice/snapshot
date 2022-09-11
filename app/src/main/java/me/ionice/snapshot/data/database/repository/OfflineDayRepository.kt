package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.ionice.snapshot.data.database.dao.DayDao
import me.ionice.snapshot.data.database.dao.LocationDao
import me.ionice.snapshot.data.database.dao.TagDao
import me.ionice.snapshot.data.database.model.*
import java.time.Instant
import java.time.LocalDate

class OfflineDayRepository(
    private val dispatcher: CoroutineDispatcher,
    private val dayDao: DayDao,
    private val locationDao: LocationDao,
    private val tagDao: TagDao
) : DayRepository {

    override suspend fun get(dayId: Long): Day? {
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
        location: LocationEntry?,
        tags: List<TagEntry>
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
                (oldTags.keys subtract newTags.keys).map { TagEntry(dayId, it, oldTags[it]) }
                    .let { tagDao.deleteEntries(it) }
                (newTags.keys subtract oldTags.keys).map { TagEntry(dayId, it, newTags[it]) }
                    .let { tagDao.insertEntries(it) }
                (newTags.keys intersect oldTags.keys).filter { newTags[it] != oldTags[it] }
                    .map { TagEntry(dayId, it, newTags[it]) }.let { tagDao.updateEntries(it) }
            }
        }
    }

    override fun getFlow(dayId: Long): Flow<Day?> {
        return dayDao.getFlow(dayId)
    }

    override fun getListFlowByYear(year: Int): Flow<List<Day>> {
        return dayDao.getListFlowByYear(year)
    }

    override fun getListFlowInIdRange(start: Long, end: Long): Flow<List<Day>> {
        return dayDao.getListFlowByIdRange(start, end)
    }

    override fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<Day>> {
        return dayDao.getListFlowByDayOfYear(month, dayOfMonth)
    }
}