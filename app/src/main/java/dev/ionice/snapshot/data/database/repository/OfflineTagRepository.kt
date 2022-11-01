package dev.ionice.snapshot.data.database.repository

import dev.ionice.snapshot.core.database.dao.TagDao
import dev.ionice.snapshot.core.database.model.Tag
import dev.ionice.snapshot.core.database.model.TagEntry
import dev.ionice.snapshot.core.database.model.TagProperties
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant

class OfflineTagRepository(
    private val dispatcher: CoroutineDispatcher, private val tagDao: TagDao
) : TagRepository {

    override suspend fun get(tagId: Long): Tag? {
        return withContext(dispatcher) { tagDao.get(tagId) }
    }

    override suspend fun add(name: String): Long {
        return withContext(dispatcher) {
            tagDao.insertProperties(
                TagProperties(
                    id = 0, name = name, lastUsedAt = -1
                )
            )
        }
    }

    override suspend fun update(tagId: Long, name: String, entries: List<TagEntry>) {
        withContext(dispatcher) {
            val existing = tagDao.get(tagId)
                ?: throw IllegalArgumentException("No Tag found for the given tagId.")
            tagDao.updateProperties(
                TagProperties(
                    id = tagId, name = name, lastUsedAt = Instant.now().epochSecond
                )
            )
            val newTags = entries.associateBy({ it.dayId }, { it.content })
            val oldTags = existing.entries.associateBy({ it.dayId }, { it.content })
            (oldTags.keys subtract newTags.keys).map { TagEntry(it, tagId, oldTags[it]) }
                .let { tagDao.deleteEntries(it) }
            (newTags.keys subtract oldTags.keys).map { TagEntry(it, tagId, newTags[it]) }
                .let { tagDao.insertEntries(it) }
            (newTags.keys intersect oldTags.keys).filter { newTags[it] != oldTags[it] }
                .map { TagEntry(it, tagId, newTags[it]) }.let { tagDao.updateEntries(it) }
        }
    }

    override suspend fun getAllProperties(): List<TagProperties> {
        return withContext(dispatcher) { tagDao.getAllProperties() }
    }

    override fun getAllPropertiesFlow(): Flow<List<TagProperties>> {
        return tagDao.getAllPropertiesFlow()
    }

    override fun getRecentlyUsedFlow(): Flow<List<TagProperties>> {
        return tagDao.getRecentlyUsedFlow()
    }
}