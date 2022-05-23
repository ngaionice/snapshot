package me.ionice.snapshot.data.metric

import kotlinx.coroutines.flow.Flow

interface MetricRepository {

    suspend fun insertKey(name: String)

    suspend fun getMetric(key: MetricKey) : Metric?

    suspend fun upsertEntry(entry: MetricEntry)

    suspend fun deleteEntry(entry: MetricEntry)

    suspend fun getKeys() : List<MetricKey>

    fun observeKeys(): Flow<List<MetricKey>>
}