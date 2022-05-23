package me.ionice.snapshot.data.day

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(day: Day): Long

    @Update
    suspend fun update(day: Day)

    @Transaction
    suspend fun upsert(day: Day) {
        if (insert(day) == -1L) update(day)
    }

    @Query("select * from day_entry where id = :key")
    suspend fun get(key: Long): Day?

    @Query("select * from day_entry where id >= :startDay and id <= :endDayInclusive order by id desc")
    suspend fun getInRangeWithMetrics(startDay: Long, endDayInclusive: Long): List<DayWithMetrics>

    @Query("select * from day_entry where id >= :startDay and id <= :endDayInclusive order by id desc")
    fun observeInRangeWithMetrics(startDay: Long, endDayInclusive: Long): Flow<List<DayWithMetrics>>

    @Transaction
    @Query("select * from day_entry where id = :epochDay")
    suspend fun getWithMetrics(epochDay: Long): DayWithMetrics?
}