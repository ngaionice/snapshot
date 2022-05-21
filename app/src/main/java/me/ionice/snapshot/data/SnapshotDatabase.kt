package me.ionice.snapshot.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.ionice.snapshot.data.day.Day
import me.ionice.snapshot.data.day.DayDao
import me.ionice.snapshot.data.metric.MetricDao
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey

@Database(
    entities = [Day::class, MetricKey::class, MetricEntry::class],
    version = 1,
    exportSchema = false
)
abstract class SnapshotDatabase : RoomDatabase() {

    abstract val dayDao: DayDao
    abstract val metricDao: MetricDao

    companion object {
        @Volatile
        private var INSTANCE: SnapshotDatabase? = null

        fun getInstance(context: Context): SnapshotDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SnapshotDatabase::class.java,
                        "snapshot_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}