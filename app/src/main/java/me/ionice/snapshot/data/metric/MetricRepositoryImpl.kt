package me.ionice.snapshot.data.metric

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.ionice.snapshot.data.SnapshotDatabase

class MetricRepositoryImpl(private val database: SnapshotDatabase) : MetricRepository {

    override fun observeKeys(): Flow<List<MetricKey>> {
        return database.metricDao.getAllKeys()
    }

    override suspend fun insertKey(name: String) {
        withContext(Dispatchers.IO) {
            database.metricDao.insertKey(MetricKey(name = name))
        }
    }

}