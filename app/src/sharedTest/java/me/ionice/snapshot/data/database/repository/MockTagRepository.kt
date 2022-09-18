package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import me.ionice.snapshot.data.database.model.Tag
import me.ionice.snapshot.data.database.model.TagEntry
import me.ionice.snapshot.data.database.model.TagProperties

class MockTagRepository : TagRepository {
    private val tag = MutableStateFlow<Tag?>(null)
    override suspend fun get(tagId: Long): Tag? = tag.value

    override suspend fun add(name: String): Long {
        val id = (1..10L).random()
        tag.value = Tag(
            properties = TagProperties(
                id = id,
                name = name,
                lastUsedAt = 0
            ),
            entries = emptyList()
        )
        return id
    }

    override suspend fun update(tagId: Long, name: String, entries: List<TagEntry>) {
        tag.value = Tag(
            properties = TagProperties(
                id = tagId,
                name = name,
                lastUsedAt = 0
            ),
            entries = entries
        )
    }

    override suspend fun getAllProperties(): List<TagProperties> {
        return tag.value?.let { listOf(it.properties) } ?: emptyList()
    }

    override fun getAllPropertiesFlow(): Flow<List<TagProperties>> {
        return flowOf(tag.value?.let { listOf(it.properties) } ?: emptyList())
    }

    override fun getRecentlyUsedFlow(): Flow<List<TagProperties>> {
        return flowOf(tag.value?.let { listOf(it.properties) } ?: emptyList())
    }
}