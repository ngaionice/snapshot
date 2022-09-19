package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.*
import me.ionice.snapshot.data.database.model.Tag
import me.ionice.snapshot.data.database.model.TagEntry
import me.ionice.snapshot.data.database.model.TagProperties
import kotlin.math.min

class FakeTagRepository : TagRepository {
    private val tag = MutableStateFlow<List<Tag>>(emptyList())
    private var lastUsedId = -1L
    override suspend fun get(tagId: Long): Tag? = tag.value.find { it.properties.id == tagId }

    override suspend fun add(name: String): Long {
        lastUsedId++
        tag.update { it + Tag(
            properties = TagProperties(
                id = lastUsedId,
                name = name,
                lastUsedAt = 0
            ),
            entries = emptyList()
        ) }
        return lastUsedId
    }

    override suspend fun update(tagId: Long, name: String, entries: List<TagEntry>) {
        val toUpdate = tag.value.find { it.properties.id == tagId } ?: return
        val toInsert = toUpdate.copy(
            properties = TagProperties(
                id = tagId,
                name = name,
                lastUsedAt = 0
            ),
            entries = entries
        )
        tag.update { tags -> (tags.filter { it.properties.id != tagId } + toInsert).sortedBy { it.properties.id } }
    }

    override suspend fun getAllProperties(): List<TagProperties> {
        return tag.value.map { it.properties }
    }

    override fun getAllPropertiesFlow(): Flow<List<TagProperties>> {
        return tag.map { lst -> lst.map { it.properties } }
    }

    override fun getRecentlyUsedFlow(): Flow<List<TagProperties>> {
        return tag.map { lst -> lst.sortedByDescending { it.properties.lastUsedAt }.subList(0, min(10, lst.size)).map { it.properties } }
    }
}