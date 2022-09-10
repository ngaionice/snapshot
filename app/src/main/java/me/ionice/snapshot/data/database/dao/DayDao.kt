package me.ionice.snapshot.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.ionice.snapshot.data.database.model.Day
import me.ionice.snapshot.data.database.model.DayProperties

@Dao
interface DayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProperties(day: DayProperties): Long

    @Update
    suspend fun updateProperties(day: DayProperties)

    @Transaction
    @Query("select * from Day where id = :id")
    suspend fun get(id: Long): Day?

    @Transaction
    @Query("select * from Day where id = :id")
    fun getFlow(id: Long): Flow<Day?>

    @Transaction
    @Query("select * from Day where year = :year order by id desc")
    fun getListFlowByYear(year: Int): Flow<List<Day>>

    /**
     * Returns a Flow of DayProperties that are in the range of [start] and [end], inclusive.
     *
     * @param start The first epoch day to get a flow for. The definition of epoch day follows that of [java.time.LocalDate]
     * @param end The last epoch day to get a flow for.
     */
    @Transaction
    @Query("select * from Day where id >= :start and id <= :end order by id desc")
    fun getListFlowByIdRange(start: Long, end: Long): Flow<List<Day>>

    @Transaction
    @Query("select * from Day where month = :month and dayOfMonth = :dayOfMonth order by id desc")
    fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<Day>>
}
