package me.ionice.snapshot.data.metric

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricDao {

    @Insert
    suspend fun insertKey(key: MetricKey)

    @Update
    suspend fun updateKey(key: MetricKey)

    @Query("select * from metric_key")
    fun getAllKeys(): Flow<List<MetricKey>>

    @Insert
    suspend fun insertEntry(metricEntry: MetricEntry): Long

    @Update
    suspend fun updateEntry(metricEntry: MetricEntry)

    @Transaction
    suspend fun upsertEntry(metricEntry: MetricEntry) {
        if (insertEntry(metricEntry) == -1L) updateEntry(metricEntry)
    }

    @Transaction
    suspend fun upsertManyEntries(metricEntries: List<MetricEntry>) {
        metricEntries.forEach { metricEntry ->
            if (insertEntry(metricEntry) == -1L) updateEntry(metricEntry)
        }
    }

    @Delete
    suspend fun deleteEntry(metricEntry: MetricEntry)

    @Transaction
    @Query("select * from metric_key where id = :keyId")
    suspend fun getMetric(keyId: Long): Metric?
}