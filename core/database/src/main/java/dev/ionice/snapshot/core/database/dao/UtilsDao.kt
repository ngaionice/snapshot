package dev.ionice.snapshot.core.database.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface UtilsDao {

    @RawQuery
    suspend fun runRawQuery(supportSQLiteQuery: SupportSQLiteQuery): Int
}