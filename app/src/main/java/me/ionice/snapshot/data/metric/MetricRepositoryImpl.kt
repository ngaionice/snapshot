package me.ionice.snapshot.data.metric

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.ionice.snapshot.data.SnapshotDatabase

class MetricRepositoryImpl(private val database: SnapshotDatabase) : MetricRepository {

    override suspend fun insertKey(name: String) {
        withContext(Dispatchers.IO) {
            database.metricDao.insertKey(MetricKey(name = name))
        }
    }

    override suspend fun getMetric(metricId: Long): Metric? {
        return withContext(Dispatchers.IO) {
            database.metricDao.getMetric(metricId)
        }
    }

    override suspend fun upsertEntry(entry: MetricEntry) {
        withContext(Dispatchers.IO) {
            database.metricDao.upsertEntry(entry)
        }
    }

    override suspend fun deleteEntry(entry: MetricEntry) {
        withContext(Dispatchers.IO) {
            database.metricDao.deleteEntry(entry)
        }
    }

    override suspend fun getKeys(): List<MetricKey> {
        return withContext(Dispatchers.IO) {
            database.metricDao.getKeys()
        }
    }

    override fun getKeysFlow(): Flow<List<MetricKey>> {
        return database.metricDao.observeKeys()
    }
}