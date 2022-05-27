package me.ionice.snapshot.data.network

import java.time.LocalDateTime

interface NetworkRepository {

    fun isOnline(): Boolean

    fun isBackupEnabled(): Boolean

    fun getLoggedInAccountEmail(): String?

    fun setBackupEnabled(value: Boolean)

    suspend fun getLastBackupTime(): LocalDateTime?

    suspend fun backupDatabase(): Result<Unit>

    suspend fun restoreDatabase(): Result<Unit>
}