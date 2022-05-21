package me.ionice.snapshot.data.metric

import kotlinx.coroutines.flow.Flow

interface MetricRepository {

    fun observeKeys(): Flow<List<MetricKey>>

    suspend fun insertKey(name: String)
}