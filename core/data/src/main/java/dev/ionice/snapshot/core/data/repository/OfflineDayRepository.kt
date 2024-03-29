package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.database.dao.DayDao
import dev.ionice.snapshot.core.database.dao.LocationDao
import dev.ionice.snapshot.core.database.dao.TagDao
import dev.ionice.snapshot.core.database.model.*
import dev.ionice.snapshot.core.model.ContentTag
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.model.Location
import dev.ionice.snapshot.core.model.Tag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate

class OfflineDayRepository(
    private val dispatcher: CoroutineDispatcher,
    private val dayDao: DayDao,
    private val locationDao: LocationDao,
    private val tagDao: TagDao
) : DayRepository {

    override suspend fun create(dayId: Long) {
        val currentTime = Instant.now().epochSecond
        val date = LocalDate.ofEpochDay(dayId)
        dayDao.insertEntity(
            DayEntity(
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
        location: Location?,
        tags: List<ContentTag>
    ) {
        withContext(dispatcher) {
            val existingDbEntry = dayDao.get(dayId)
            val existingModel = existingDbEntry?.toExternalModel()
            val currentTime = Instant.now().epochSecond
            if (existingModel == null) {
                throw IllegalArgumentException("No existing Day found for the given dayId: $dayId")
            }

            val updateTagLastUsed: suspend (Long) -> Unit = {
                tagDao.updateEntity(
                    TagEntity(
                        id = it,
                        name = tags.filter { ct -> ct.tag.id == it }[0].tag.name,
                        lastUsedAt = currentTime
                    )
                )
            }

            val updateLocationLastUsed: suspend (Location) -> Unit = {
                val (lat, lon) = it.coordinates
                locationDao.updateEntity(
                    LocationEntity(
                        id = it.id,
                        coordinates = CoordinatesEntity(lat, lon),
                        name = it.name,
                        lastUsedAt = currentTime
                    )
                )
            }

            dayDao.updateEntity(
                existingDbEntry.properties.copy(
                    summary = summary, isFavorite = isFavorite, lastModifiedAt = currentTime
                )
            )
            // compare location, if not equal then need to delete old and insert new
            val oldLocId = existingModel.location?.id
            if (oldLocId != location?.id) {
                oldLocId?.let { locationDao.deleteCrossRef(DayLocationCrossRef(dayId, it)) }
                location?.let {
                    locationDao.insertCrossRef(DayLocationCrossRef(dayId, it.id))
                    updateLocationLastUsed(it)
                }
            }
            // compare tags: delete old ones, insert new ones, update common ones
            val newTags = tags.associateBy({ it.tag.id }, { it.content })
            val oldTags = existingModel.tags.associateBy({ it.tag.id }, { it.content })

            (oldTags.keys subtract newTags.keys).map { DayTagCrossRef(dayId, it, oldTags[it]) }
                .let { tagDao.deleteCrossRefs(it) }
            (newTags.keys subtract oldTags.keys).map {
                updateTagLastUsed(it)
                DayTagCrossRef(dayId, it, newTags[it])
            }
                .let { tagDao.insertCrossRefs(it) }
            (newTags.keys intersect oldTags.keys).filter { newTags[it] != oldTags[it] }
                .map {
                    updateTagLastUsed(it)
                    DayTagCrossRef(dayId, it, newTags[it])
                }
                .let { tagDao.updateCrossRefs(it) }
        }
    }

    override fun getFlow(dayId: Long): Flow<Day?> {
        return dayDao.getFlow(dayId).map { it?.toExternalModel() }
    }

    override fun getListFlowByYear(year: Int): Flow<List<Day>> {
        return dayDao.getListFlowByYear(year).map { lst -> lst.map { it.toExternalModel() } }
    }

    override fun getListFlowInIdRange(start: Long, end: Long): Flow<List<Day>> {
        return dayDao.getListFlowByIdRange(start, end)
            .map { lst -> lst.map { it.toExternalModel() } }
    }

    override fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<Day>> {
        return dayDao.getListFlowByDayOfYear(month, dayOfMonth)
            .map { lst -> lst.map { it.toExternalModel() } }
    }

    override fun getListFlowForFavorites(): Flow<List<Day>> {
        return dayDao.getListFlowForFavorites().map { lst -> lst.map { it.toExternalModel() } }
    }

    override fun getListFlowByTag(tagId: Long): Flow<List<Day>> {
        return dayDao.getListFlowByTagId(tagId)
            .map { it.map { dayEntity -> dayEntity.toExternalModel() } }
    }

    override suspend fun search(
        queryString: String,
        startDayId: Long?,
        endDayId: Long?,
        isFavorite: Boolean?,
        searchTagEntries: Boolean,
        includedLocations: Set<Location>?,
        includedTags: Set<Tag>?
    ): List<Day> {
        val formattedQuery = queryString.split(" ").filter { it.isNotEmpty() }
            .joinToString(" ") { if ((it[0] != '-' && it.length > 2) || it.length > 3) "$it*" else it }

        val getSummaryFlow = {
            dayDao.searchBySummary(formattedQuery, startDayId, endDayId, isFavorite)
        }
        val getTagEntryFlow = {
            dayDao.searchByTagEntry(formattedQuery, startDayId, endDayId, isFavorite)
        }
        val locationIds = includedLocations?.map { it.id } ?: emptyList()
        val tagIds = includedTags?.map { it.id } ?: emptyList()
        val locationFilter: (PopulatedDay) -> Boolean = { day ->
            locationIds.isEmpty() || (day.location?.let { loc -> locationIds.contains(loc.id) }
                ?: false)
        }
        val tagsFilter: (PopulatedDay) -> Boolean = { day ->
            tagIds.isEmpty() || (day.tags.isNotEmpty() && day.tags.map { it.id }
                .any { tagIds.contains(it) })
        }
        val combinedFilter: (PopulatedDay) -> Boolean = {
            locationFilter(it) && tagsFilter(it)
        }

        val summaryRes = getSummaryFlow().first().filter(combinedFilter).toSet()
        val tagEntryRes = if (searchTagEntries) getTagEntryFlow().first().filter(combinedFilter)
            .toSet() else emptySet()

        val out = summaryRes.toMutableList()
        tagEntryRes.forEach { if (summaryRes.contains(it)) out.add(it) }
        return out.map { it.toExternalModel() }.sortedByDescending { it.id }
    }
}