package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.database.dao.TagDao
import dev.ionice.snapshot.core.database.model.TagEntity
import dev.ionice.snapshot.core.database.model.toExternalModel
import dev.ionice.snapshot.core.model.Tag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OfflineTagRepository(
    private val dispatcher: CoroutineDispatcher, private val tagDao: TagDao
) : TagRepository {

//    override suspend fun get(tagId: Long): PopulatedTag? {
//        return withContext(dispatcher) { tagDao.get(tagId) }
//    }

    override suspend fun add(name: String): Long {
        return withContext(dispatcher) {
            tagDao.insertEntity(
                TagEntity(
                    id = 0, name = name, lastUsedAt = -1
                )
            )
        }
    }

    override suspend fun getAll(): List<Tag> {
        return withContext(dispatcher) { tagDao.getAllEntities().map { it.toExternalModel() } }
    }

    override fun getAllFlow(): Flow<List<Tag>> {
        return tagDao.getAllEntitiesFlow().map { lst -> lst.map { it.toExternalModel() } }
    }

    override fun getRecentlyUsedFlow(): Flow<List<Tag>> {
        return tagDao.getRecentEntitiesFlow().map { lst -> lst.map { it.toExternalModel() } }
    }
}