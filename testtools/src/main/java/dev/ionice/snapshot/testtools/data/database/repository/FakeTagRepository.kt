package dev.ionice.snapshot.testtools.data.database.repository

import dev.ionice.snapshot.core.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.min

class FakeTagRepository : dev.ionice.snapshot.core.data.repository.TagRepository {
    private val backingFlow = FakeRepositoryData.tagBackingFlow
    private var lastUsedId = -1L
//    override suspend fun get(tagId: Long): PopulatedTag? =
//        backingFlow.value.find { it.properties.id == tagId }

    override suspend fun add(name: String): Long {
        lastUsedId++
        backingFlow.tryEmit(
            backingFlow.value + Tag(
                id = lastUsedId,
                name = name,
                lastUsedAt = 0
            )
        )
        return lastUsedId
    }

    override suspend fun getAll(): List<Tag> {
        return backingFlow.value
    }

    override fun getAllFlow(): Flow<List<Tag>> {
        return backingFlow
    }

    override fun getRecentlyUsedFlow(): Flow<List<Tag>> {
        return backingFlow.map { lst ->
            lst.sortedByDescending { it.lastUsedAt }.subList(0, min(10, lst.size))
        }
    }

    fun sendTags(tags: List<Tag>) {
        backingFlow.tryEmit(tags)
    }
}