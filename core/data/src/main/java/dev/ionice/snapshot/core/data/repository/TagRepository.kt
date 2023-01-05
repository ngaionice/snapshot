package dev.ionice.snapshot.core.data.repository

import dev.ionice.snapshot.core.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {

    fun getFlow(tagId: Long): Flow<Tag?>

    suspend fun add(name: String): Long

    suspend fun getAll(): List<Tag>

    fun getAllFlow(): Flow<List<Tag>>

    fun getRecentlyUsedFlow(): Flow<List<Tag>>
}