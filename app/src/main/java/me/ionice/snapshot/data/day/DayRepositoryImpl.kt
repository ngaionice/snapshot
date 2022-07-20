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
            val existing = database.dayDao.getWithMetrics(day.day.id)

            // delete removed keys
            if (existing != null) {
                val newKeys = day.metrics.map { entry -> entry.metricId }.toSet()
                val deleted = existing.metrics.filter { entry -> !newKeys.contains(entry.metricId) }
                deleted.forEach {
                    database.metricDao.deleteEntry(it)
                }
            }

            // update existing data
            database.dayDao.upsert(day.day)
            database.metricDao.upsertManyEntries(day.metrics)

        }
    }

    override suspend fun getDays(startDay: Long, endDayInclusive: Long): List<DayWithMetrics> {
        return withContext(Dispatchers.IO) {
            database.dayDao.getInRangeWithMetrics(startDay, endDayInclusive)
        }
    }

    override fun getDaysFlow(
        startDay: Long,
        endDayInclusive: Long
    ): Flow<List<DayWithMetrics>> {
        return database.dayDao.observeInRangeWithMetrics(startDay, endDayInclusive)
    }

}