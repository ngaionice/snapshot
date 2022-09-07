package me.ionice.snapshot.data.database.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface UtilsDao {

    @RawQuery
    fun runRawQuery(supportSQLiteQuery: SupportSQLiteQuery): Int
}