package dev.ionice.snapshot.testtools.data.backup

import dev.ionice.snapshot.sync.BackupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant
import java.time.LocalDateTime

class FakeBackupRepository : BackupRepository {

    private val statusFlow = MutableStateFlow(
        BackupRepository.BackupStatus(
            isInProgress = false,
            action = null,
            isSuccess = false,
            updatedAt = Instant.now().epochSecond
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
                isSuccess = false,
                updatedAt = Instant.now().epochSecond
            )
        )
    }

    override fun startDatabaseRestore() {
        statusFlow.tryEmit(
            BackupRepository.BackupStatus(
                isInProgress = true,
                action = "Restore",
                isSuccess = false,
                updatedAt = Instant.now().epochSecond
            )
        )
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
                isSuccess = isSuccess,
                updatedAt = Instant.now().epochSecond
            )
        )
    }

    /**
     * A test-only method to simulate the restore process ending.
     */
    fun endRestore(isSuccess: Boolean = true) {
        statusFlow.tryEmit(
            BackupRepository.BackupStatus(
                isInProgress = false,
                action = "Restore",
                isSuccess = isSuccess,
                updatedAt = Instant.now().epochSecond
            )
        )
    }
}