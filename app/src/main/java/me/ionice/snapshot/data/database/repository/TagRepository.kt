package me.ionice.snapshot.data.database.repository

import kotlinx.coroutines.flow.Flow
import me.ionice.snapshot.data.database.model.Tag
import me.ionice.snapshot.data.database.model.TagEntry
import me.ionice.snapshot.data.database.model.TagProperties

interface TagRepository {

    suspend fun get(tagId: Long): Tag?

    suspend fun add(name: String): Long

    suspend fun update(tagId: Long, name: String, entries: List<TagEntry>)

    suspend fun getAllProperties(): List<TagProperties>

    fun getAllPropertiesFlow(): Flow<List<TagProperties>>
}