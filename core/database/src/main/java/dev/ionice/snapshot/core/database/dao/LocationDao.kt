package dev.ionice.snapshot.core.database.dao

import androidx.room.*
import dev.ionice.snapshot.core.database.model.LocationEntity
import dev.ionice.snapshot.core.database.model.LocationEntryEntity
import dev.ionice.snapshot.core.database.model.LocationPropertiesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert
    suspend fun insertProperties(location: LocationPropertiesEntity): Long

    @Update
    suspend fun updateProperties(location: LocationPropertiesEntity)

    @Delete
    suspend fun delete(location: LocationPropertiesEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntry(entry: LocationEntryEntity)

    @Insert
    suspend fun insertEntries(entries: List<LocationEntryEntity>)

    @Delete
    suspend fun deleteEntry(entry: LocationEntryEntity)

    @Delete
    suspend fun deleteEntries(entries: List<LocationEntryEntity>)

    @Transaction
    @Query("select * from Location where id = :id")
    suspend fun get(id: Long): LocationEntity?

    @Query("select * from Location")
    suspend fun getAllProperties(): List<LocationPropertiesEntity>

    @Query("select * from Location")
    fun getAllPropertiesFlow(): Flow<List<LocationPropertiesEntity>>
}