package me.ionice.snapshot.data.network

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface NetworkRepository {

    fun isOnline(): Boolean

    fun getLoggedInAccountEmail(): String?

    suspend fun getLastBackupTime(): LocalDateTime?

    fun startDatabaseBackup()

    fun startDatabaseRestore()

    fun getBackupStatusFlow(): Flow<BackupState>

    fun getBackupStatus(): BackupState

    data class BackupState(val isInProgress: Boolean, val action: String?, val isSuccess: Boolean?)
}