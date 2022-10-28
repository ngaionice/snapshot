package dev.ionice.snapshot.testtools.data.database.repository

import kotlinx.coroutines.flow.*
import dev.ionice.snapshot.data.database.model.Tag
import dev.ionice.snapshot.data.database.model.TagEntry
import dev.ionice.snapshot.data.database.model.TagProperties
import dev.ionice.snapshot.data.database.repository.TagRepository
import kotlin.math.min

class FakeTagRepository : TagRepository {
    private val backingFlow = FakeRepositoryData.tagBackingFlow
    private var lastUsedId = -1L
    override suspend fun get(tagId: Long): Tag? = backingFlow.value.find { it.properties.id == tagId }

    override suspend fun add(name: String): Long {
        lastUsedId++
        backingFlow.tryEmit(backingFlow.value + Tag(
            properties = TagProperties(
                id = lastUsedId,
                name = name,
                lastUsedAt = 0
            ),
            entries = emptyList()
        )
        )
        return lastUsedId
    }

    override suspend fun update(tagId: Long, name: String, entries: List<TagEntry>) {
        val toUpdate = backingFlow.value.find { it.properties.id == tagId } ?: return
        val toInsert = toUpdate.copy(
            properties = TagProperties(
                id = tagId,
                name = name,
                lastUsedAt = 0
            ),
            entries = entries
        )
        val tags = backingFlow.value
        backingFlow.tryEmit((tags.filter { it.properties.id != tagId } + toInsert).sortedBy { it.properties.id })
    }

    override suspend fun getAllProperties(): List<TagProperties> {
        return backingFlow.value.map { it.properties }
    }

    override fun getAllPropertiesFlow(): Flow<List<TagProperties>> {
        return backingFlow.map { lst -> lst.map { it.properties } }
    }

    override fun getRecentlyUsedFlow(): Flow<List<TagProperties>> {
        return backingFlow.map { lst -> lst.sortedByDescending { it.properties.lastUsedAt }.subList(0, min(10, lst.size)).map { it.properties } }
    }

    fun sendTags(tags: List<Tag>) {
        backingFlow.tryEmit(tags)
    }
}