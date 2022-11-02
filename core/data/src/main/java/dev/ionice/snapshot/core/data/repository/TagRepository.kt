package dev.ionice.snapshot.core.data.repository

import kotlinx.coroutines.flow.Flow
import dev.ionice.snapshot.core.database.model.TagEntity
import dev.ionice.snapshot.core.database.model.TagEntryEntity
import dev.ionice.snapshot.core.database.model.TagPropertiesEntity

interface TagRepository {

    suspend fun get(tagId: Long): TagEntity?

    suspend fun add(name: String): Long

    suspend fun update(tagId: Long, name: String, entries: List<TagEntryEntity>)

    suspend fun getAllProperties(): List<TagPropertiesEntity>

    fun getAllPropertiesFlow(): Flow<List<TagPropertiesEntity>>

    fun getRecentlyUsedFlow(): Flow<List<TagPropertiesEntity>>
}