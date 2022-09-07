package me.ionice.snapshot.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import me.ionice.snapshot.data.database.dao.DayDao
import me.ionice.snapshot.data.database.dao.LocationDao
import me.ionice.snapshot.data.database.dao.TagDao
import me.ionice.snapshot.data.database.dao.UtilsDao
import me.ionice.snapshot.data.database.model.*

@Database(
    entities = [DayProperties::class, DaySummaryFts::class, LocationProperties::class, LocationEntry::class, TagProperties::class, TagEntry::class, TagEntryFts::class],
    version = 2
)
abstract class SnapshotDatabase : RoomDatabase() {

    abstract val dayDao: DayDao
    abstract val locationDao: LocationDao
    abstract val tagDao: TagDao
    abstract val utilsDao: UtilsDao

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
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        fun runCheckpoint() {
            if (INSTANCE == null) {
                throw IllegalStateException("Database instance is null, failed to run checkpoint.")
            }
            INSTANCE!!.utilsDao.runRawQuery(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
        }

        const val DATABASE_NAME = "snapshot_database"
    }
}