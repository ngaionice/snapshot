package me.ionice.snapshot.data.day

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.ionice.snapshot.data.SnapshotDatabase

class DayRepositoryImpl(private val database: SnapshotDatabase) : DayRepository {

    override suspend fun getDay(epochDay: Long): DayWithMetrics? {
        return withContext(Dispatchers.IO) {
            database.dayDao.getWithMetrics(epochDay)
        }
    }

    override suspend fun upsertDay(day: DayWithMetrics) {
        withContext(Dispatchers.IO) {
            database.dayDao.upsert(day.day)
            database.metricDao.upsertManyEntries(day.metrics)
        }
    }

    override fun observeDays(
        startDay: Long,
        endDayInclusive: Long
    ): Flow<List<DayWithMetrics>> {
        return database.dayDao.getInRangeWithMetrics(startDay, endDayInclusive)
    }

}