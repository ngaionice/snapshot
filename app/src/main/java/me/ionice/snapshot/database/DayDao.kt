package me.ionice.snapshot.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DayDao {

    @Insert
    suspend fun insert(day: Day)

    @Update
    suspend fun update(day: Day)

    @Query("select * from day_entry where id = :key")
    suspend fun get(key: Long): Day?

    @Query("select * from day_entry order by id desc limit 1")
    suspend fun getLatest(): Day?

    @Query("select * from day_entry order by id desc")
    fun getAll(): LiveData<List<Day>>

    @Transaction
    @Query("select * from day_entry where id = :dayId")
    suspend fun getWithMetrics(dayId: Long) : DayWithMetrics?
}