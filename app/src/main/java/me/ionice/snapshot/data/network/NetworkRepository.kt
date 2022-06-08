package me.ionice.snapshot.data.network

import java.time.LocalDateTime

interface NetworkRepository {

    fun isOnline(): Boolean

    fun getLoggedInAccountEmail(): String?

    suspend fun getLastBackupTime(): LocalDateTime?

    suspend fun backupDatabase(): Result<Unit>

    suspend fun restoreDatabase(): Result<Unit>
}