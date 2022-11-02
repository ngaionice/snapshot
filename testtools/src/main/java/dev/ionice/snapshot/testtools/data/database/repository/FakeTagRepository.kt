package dev.ionice.snapshot.testtools.data.database.repository

import dev.ionice.snapshot.core.database.model.TagEntity
import dev.ionice.snapshot.core.database.model.TagEntryEntity
import dev.ionice.snapshot.core.database.model.TagPropertiesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.min

class FakeTagRepository : dev.ionice.snapshot.core.data.repository.TagRepository {
    private val backingFlow = FakeRepositoryData.tagBackingFlow
    private var lastUsedId = -1L
    override suspend fun get(tagId: Long): TagEntity? =
        backingFlow.value.find { it.properties.id == tagId }

    override suspend fun add(name: String): Long {
        lastUsedId++
        backingFlow.tryEmit(
            backingFlow.value + TagEntity(
                properties = TagPropertiesEntity(
                    id = lastUsedId,
                    name = name,
                    lastUsedAt = 0
                ),
                entries = emptyList()
            )
        )
        return lastUsedId
    }

    override suspend fun update(tagId: Long, name: String, entries: List<TagEntryEntity>) {
        val toUpdate = backingFlow.value.find { it.properties.id == tagId } ?: return
        val toInsert = toUpdate.copy(
            properties = TagPropertiesEntity(
                id = tagId,
                name = name,
                lastUsedAt = 0
            ),
            entries = entries
        )
        val tags = backingFlow.value
        backingFlow.tryEmit((tags.filter { it.properties.id != tagId } + toInsert).sortedBy { it.properties.id })
    }

    override suspend fun getAllProperties(): List<TagPropertiesEntity> {
        return backingFlow.value.map { it.properties }
    }

    override fun getAllPropertiesFlow(): Flow<List<TagPropertiesEntity>> {
        return backingFlow.map { lst -> lst.map { it.properties } }
    }

    override fun getRecentlyUsedFlow(): Flow<List<TagPropertiesEntity>> {
        return backingFlow.map { lst ->
            lst.sortedByDescending { it.properties.lastUsedAt }.subList(0, min(10, lst.size))
                .map { it.properties }
        }
    }

    fun sendTags(tags: List<TagEntity>) {
        backingFlow.tryEmit(tags)
    }
}