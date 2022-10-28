package dev.ionice.snapshot.testtools.data.backup

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import dev.ionice.snapshot.data.backup.BackupRepository
import java.time.LocalDateTime

class FakeBackupRepository : BackupRepository {

    private val statusFlow = MutableStateFlow(
        BackupRepository.BackupStatus(
            isInProgress = false,
            action = null,
            isSuccess = false
        )
    )

    private var isOnline = false
    private var email: String? = null
    private var lastBackupTime: LocalDateTime? = null

    override fun isOnline(): Boolean = isOnline

    override fun getLoggedInAccountEmail(): String? = email

    override suspend fun getLastBackupTime(): LocalDateTime? = lastBackupTime

    override fun startDatabaseBackup() {
        statusFlow.tryEmit(
            BackupRepository.BackupStatus(
            isInProgress = true,
            action = "Backup",
            isSuccess = false
        ))
    }

    override fun startDatabaseRestore() {
        statusFlow.tryEmit(
            BackupRepository.BackupStatus(
            isInProgress = true,
            action = "Restore",
            isSuccess = false
        ))
    }

    override fun getBackupStatusFlow(): Flow<BackupRepository.BackupStatus> = statusFlow

    /**
     * A test-only method to simulate the backup process ending.
     */
    fun endBackup(isSuccess: Boolean = true) {
        statusFlow.tryEmit(
            BackupRepository.BackupStatus(
            isInProgress = false,
            action = "Backup",
            isSuccess = isSuccess
        ))
    }

    /**
     * A test-only method to simulate the restore process ending.
     */
    fun endRestore(isSuccess: Boolean = true) {
        statusFlow.tryEmit(
            BackupRepository.BackupStatus(
            isInProgress = false,
            action = "Restore",
            isSuccess = isSuccess
        ))
    }
}