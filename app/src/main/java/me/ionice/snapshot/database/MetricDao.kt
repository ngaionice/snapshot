package me.ionice.snapshot.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MetricDao {

    @Insert
    suspend fun insertKey(key: MetricKey)

    @Update
    suspend fun updateKey(key: MetricKey)

    @Query("select * from metric_key")
    fun getAllKeys(): LiveData<List<MetricKey>>

    @Insert
    suspend fun insertEntry(metricEntry: MetricEntry)

    @Update
    suspend fun updateEntry(metricEntry: MetricEntry)

    @Delete
    suspend fun deleteEntry(metricEntry: MetricEntry)

    @Transaction
    @Query("select * from metric_key where id = :keyId")
    suspend fun getMetric(keyId: Long) : Metric?
}