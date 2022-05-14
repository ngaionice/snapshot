package me.ionice.snapshot.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

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