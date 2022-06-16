package me.ionice.snapshot.data.network

import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

interface NetworkRepository {

    val backupStatus: StateFlow<BackupState>

    fun isOnline(): Boolean

    fun getLoggedInAccountEmail(): String?

    suspend fun getLastBackupTime(): LocalDateTime?

    fun startDatabaseBackup()

    fun startDatabaseRestore()

    data class BackupState(val isInProgress: Boolean, val action: String?, val isSuccess: Boolean?)
}