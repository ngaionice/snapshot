package me.ionice.snapshot.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MetricDao {

    @Insert
    fun insertKey(key: MetricKey)

    @Update
    fun updateKey(key: MetricKey)

    @Query("select * from metric_key")
    fun getAllKeys(): LiveData<List<MetricKey>>

    @Insert
    fun insertEntry(metricEntry: MetricEntry)

    @Update
    fun updateEntry(metricEntry: MetricEntry)

    @Delete
    fun deleteEntry(metricEntry: MetricEntry)

    @Transaction
    @Query("select * from metric_key where id = :keyId")
    fun getMetric(keyId: Long) : Metric?
}