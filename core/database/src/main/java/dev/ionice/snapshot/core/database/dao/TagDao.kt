package dev.ionice.snapshot.core.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import dev.ionice.snapshot.core.database.model.PopulatedTag
import dev.ionice.snapshot.core.database.model.DayTagCrossRef
import dev.ionice.snapshot.core.database.model.TagEntity

@Dao
interface TagDao {

    @Insert
    suspend fun insertEntity(tag: TagEntity): Long

    @Update
    suspend fun updateEntity(tag: TagEntity)

    @Delete
    suspend fun delete(tag: TagEntity)

    @Insert
    suspend fun insertCrossRef(entry: DayTagCrossRef)

    @Insert
    suspend fun insertCrossRefs(entries: List<DayTagCrossRef>)

    @Update
    suspend fun updateCrossRef(entry: DayTagCrossRef)

    @Update
    suspend fun updateCrossRefs(entries: List<DayTagCrossRef>)

    @Delete
    suspend fun deleteCrossRef(entry: DayTagCrossRef)

    @Delete
    suspend fun deleteCrossRefs(entries: List<DayTagCrossRef>)

    @Transaction
    @Query("select * from Tag where id = :id")
    suspend fun get(id: Long): PopulatedTag?

    @Transaction
    @Query("select * from Tag where id = :id")
    fun getFlow(id: Long): Flow<PopulatedTag?>

    @Query("select * from Tag")
    suspend fun getAllEntities(): List<TagEntity>

    @Query("select * from Tag")
    fun getAllEntitiesFlow(): Flow<List<TagEntity>>

    @Query("select * from Tag order by lastUsedAt desc limit 10")
    fun getRecentEntitiesFlow(): Flow<List<TagEntity>>

}