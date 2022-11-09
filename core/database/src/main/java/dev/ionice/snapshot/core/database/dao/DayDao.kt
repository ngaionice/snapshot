package dev.ionice.snapshot.core.database.dao

import androidx.room.*
import dev.ionice.snapshot.core.database.model.PopulatedDay
import dev.ionice.snapshot.core.database.model.DayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntity(day: DayEntity): Long

    @Update
    suspend fun updateEntity(day: DayEntity)

    @Transaction
    @Query("select * from Day where id = :id")
    suspend fun get(id: Long): PopulatedDay?

    @Transaction
    @Query("select * from Day where id = :id")
    fun getFlow(id: Long): Flow<PopulatedDay?>

    @Transaction
    @Query("select * from Day where year = :year order by id desc")
    fun getListFlowByYear(year: Int): Flow<List<PopulatedDay>>

    @Transaction
    @Query("""
        select * from Day d join DaySummaryFts df on d.id = df.id 
            where DaySummaryFts match :queryString 
            and (:startDayId is null or d.id >= :startDayId)
            and (:endDayId is null or d.id <= :endDayId)
            and (:isFavorite is null or d.isFavorite = 1)
    """)
    fun searchBySummary(
        queryString: String,
        startDayId: Long? = null,
        endDayId: Long? = null,
        isFavorite: Boolean? = null
    ): Flow<List<PopulatedDay>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("""
        select * from Day d join TagEntryFts tf on d.id = tf.dayId 
            where TagEntryFts match :queryString
            and (:startDayId is null or d.id >= :startDayId)
            and (:endDayId is null or d.id <= :endDayId)
            and (:isFavorite is null or d.isFavorite = 1)
    """)
    fun searchByTagEntry(
        queryString: String,
        startDayId: Long? = null,
        endDayId: Long? = null,
        isFavorite: Boolean? = null
    ): Flow<List<PopulatedDay>>

    /**
     * Returns a Flow of DayProperties that are in the range of [start] and [end], inclusive.
     *
     * @param start The first epoch day to get a flow for. The definition of epoch day follows that of [java.time.LocalDate]
     * @param end The last epoch day to get a flow for.
     */
    @Transaction
    @Query("""
        select * from Day 
            where (:start is null or id >= :start) 
            and (:end is null or id <= :end)
            order by id desc
    """)
    fun getListFlowByIdRange(start: Long? = null, end: Long? = null): Flow<List<PopulatedDay>>

    @Transaction
    @Query("select * from Day where month = :month and dayOfMonth = :dayOfMonth order by id desc")
    fun getListFlowByDayOfYear(month: Int, dayOfMonth: Int): Flow<List<PopulatedDay>>

    @Transaction
    @Query("select * from Day where isFavorite = 1 order by id desc")
    fun getListFlowForFavorites(): Flow<List<PopulatedDay>>
}
