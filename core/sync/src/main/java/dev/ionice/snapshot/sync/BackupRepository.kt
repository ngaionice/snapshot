package dev.ionice.snapshot.sync

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface BackupRepository {

    fun isOnline(): Boolean

    fun getLoggedInAccountEmail(): String?

    suspend fun getLastBackupTime(): LocalDateTime?

    fun startDatabaseBackup()

    fun startDatabaseRestore()

    fun getBackupStatusFlow(): Flow<BackupStatus>

    data class BackupStatus(val isInProgress: Boolean, val action: String?, val isSuccess: Boolean)
}