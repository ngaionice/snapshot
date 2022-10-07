package me.ionice.snapshot.data.backup

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface BackupRepository {

    fun isOnline(): Boolean

    fun getLoggedInAccountEmail(): String?

    suspend fun getLastBackupTime(): LocalDateTime?

    fun startDatabaseBackup()

    fun startDatabaseRestore()

    fun getBackupStatusFlow(): Flow<BackupStatus>

    fun getBackupStatus(): BackupStatus

    data class BackupStatus(val isInProgress: Boolean, val action: String?, val isSuccess: Boolean)
}