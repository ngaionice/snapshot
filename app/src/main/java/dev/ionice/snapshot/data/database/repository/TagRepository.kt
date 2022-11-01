package dev.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.Flow
import dev.ionice.snapshot.core.database.model.Tag
import dev.ionice.snapshot.core.database.model.TagEntry
import dev.ionice.snapshot.core.database.model.TagProperties

interface TagRepository {

    suspend fun get(tagId: Long): Tag?

    suspend fun add(name: String): Long

    suspend fun update(tagId: Long, name: String, entries: List<TagEntry>)

    suspend fun getAllProperties(): List<TagProperties>

    fun getAllPropertiesFlow(): Flow<List<TagProperties>>

    fun getRecentlyUsedFlow(): Flow<List<TagProperties>>
}