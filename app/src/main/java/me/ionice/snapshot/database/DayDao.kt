package me.ionice.snapshot.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DayDao {

    @Insert
    fun insert(day: Day)

    @Update
    fun update(day: Day)

    @Query("select * from day_entry where id = :key")
    fun get(key: Long): Day?

    @Query("select * from day_entry order by id desc limit 1")
    fun getLatest(): Day?

    @Query("select * from day_entry order by id desc")
    fun getAll(): LiveData<List<Day>>

    @Transaction
    @Query("select * from day_entry where id = :dayId")
    fun getWithMetrics(dayId: Long) : DayWithMetrics?
}