package dev.ionice.snapshot.core.database.dao

import androidx.room.*
import dev.ionice.snapshot.core.database.model.PopulatedLocation
import dev.ionice.snapshot.core.database.model.DayLocationCrossRef
import dev.ionice.snapshot.core.database.model.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert
    suspend fun insertEntity(location: LocationEntity): Long

    @Update
    suspend fun updateEntity(location: LocationEntity)

    @Delete
    suspend fun delete(location: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(entry: DayLocationCrossRef)

    @Insert
    suspend fun insertCrossRefs(entries: List<DayLocationCrossRef>)

    @Delete
    suspend fun deleteCrossRef(entry: DayLocationCrossRef)

    @Delete
    suspend fun deleteCrossRefs(entries: List<DayLocationCrossRef>)

    @Transaction
    @Query("select * from Location where id = :id")
    suspend fun get(id: Long): PopulatedLocation?

    @Query("select * from Location")
    suspend fun getAllEntities(): List<LocationEntity>

    @Query("select * from Location")
    fun getAllEntitiesFlow(): Flow<List<LocationEntity>>
}