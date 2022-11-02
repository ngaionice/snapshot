package dev.ionice.snapshot.core.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import dev.ionice.snapshot.core.database.model.TagEntity
import dev.ionice.snapshot.core.database.model.TagEntryEntity
import dev.ionice.snapshot.core.database.model.TagPropertiesEntity

@Dao
interface TagDao {

    @Insert
    suspend fun insertProperties(tag: TagPropertiesEntity): Long

    @Update
    suspend fun updateProperties(tag: TagPropertiesEntity)

    @Delete
    suspend fun delete(tag: TagPropertiesEntity)

    @Insert
    suspend fun insertEntry(entry: TagEntryEntity)

    @Insert
    suspend fun insertEntries(entries: List<TagEntryEntity>)

    @Update
    suspend fun updateEntry(entry: TagEntryEntity)

    @Update
    suspend fun updateEntries(entries: List<TagEntryEntity>)

    @Delete
    suspend fun deleteEntry(entry: TagEntryEntity)

    @Delete
    suspend fun deleteEntries(entries: List<TagEntryEntity>)

    @Transaction
    @Query("select * from Tag where id = :id")
    suspend fun get(id: Long): TagEntity?

    @Query("select * from Tag")
    suspend fun getAllProperties(): List<TagPropertiesEntity>

    @Query("select * from Tag")
    fun getAllPropertiesFlow(): Flow<List<TagPropertiesEntity>>

    @Query("select * from Tag order by lastUsedAt desc limit 10")
    fun getRecentlyUsedFlow(): Flow<List<TagPropertiesEntity>>

}