package dev.ionice.snapshot.core.database.dao

import androidx.room.*
import dev.ionice.snapshot.core.database.model.Location
import dev.ionice.snapshot.core.database.model.LocationEntry
import dev.ionice.snapshot.core.database.model.LocationProperties
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert
    suspend fun insertProperties(location: LocationProperties): Long

    @Update
    suspend fun updateProperties(location: LocationProperties)

    @Delete
    suspend fun delete(location: LocationProperties)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntry(entry: LocationEntry)

    @Insert
    suspend fun insertEntries(entries: List<LocationEntry>)

    @Delete
    suspend fun deleteEntry(entry: LocationEntry)

    @Delete
    suspend fun deleteEntries(entries: List<LocationEntry>)

    @Transaction
    @Query("select * from Location where id = :id")
    suspend fun get(id: Long): Location?

    @Query("select * from Location")
    suspend fun getAllProperties(): List<LocationProperties>

    @Query("select * from Location")
    fun getAllPropertiesFlow(): Flow<List<LocationProperties>>
}