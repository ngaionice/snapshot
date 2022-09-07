package me.ionice.snapshot.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.ionice.snapshot.data.database.model.Tag
import me.ionice.snapshot.data.database.model.TagEntry
import me.ionice.snapshot.data.database.model.TagProperties

@Dao
interface TagDao {

    @Insert
    suspend fun insertProperties(tag: TagProperties): Long

    @Update
    suspend fun updateProperties(tag: TagProperties)

    @Delete
    suspend fun delete(tag: TagProperties)

    @Insert
    suspend fun insertEntry(entry: TagEntry)

    @Insert
    suspend fun insertEntries(entries: List<TagEntry>)

    @Update
    suspend fun updateEntry(entry: TagEntry)

    @Update
    suspend fun updateEntries(entries: List<TagEntry>)

    @Delete
    suspend fun deleteEntry(entry: TagEntry)

    @Delete
    suspend fun deleteEntries(entries: List<TagEntry>)

    @Transaction
    @Query("select * from Tag where id = :id")
    suspend fun get(id: Long): Tag?

    @Query("select * from Tag")
    suspend fun getAllProperties(): List<TagProperties>

    @Query("select * from Tag")
    fun getAllPropertiesFlow(): Flow<List<TagProperties>>

}