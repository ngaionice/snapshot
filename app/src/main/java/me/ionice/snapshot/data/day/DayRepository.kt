package me.ionice.snapshot.data.day

import kotlinx.coroutines.flow.Flow

interface DayRepository {

    /**
     * Returns a DayWithMetric if such an entry exists, else returns null.
     *
     * `epochDay` should be non-negative.
     */
    suspend fun getDay(epochDay: Long): DayWithMetrics?

    suspend fun upsertDay(day: DayWithMetrics)

    suspend fun getDays(startDay: Long, endDayInclusive: Long): List<DayWithMetrics>

    /**
     * Returns a `Flow` of a list of `DayWithMetrics` in the specified date range.
     *
     * `startDay` and `endDayInclusive` should both be non-negative.
     */
    fun getDaysFlow(startDay: Long, endDayInclusive: Long): Flow<List<DayWithMetrics>>

}