package dev.ionice.snapshot.testtools.data.backup

import dev.ionice.snapshot.sync.BackupModule
import java.time.LocalDateTime
import java.util.*

object FakeBackupModule : BackupModule {

    private var lastBackupTime = LocalDateTime.of(2022, 8, 1, 0, 0)
    private var willBackupSucceed = true
    private var willRestoreSucceed = true

    var backupCount = 0
    var restoreCount = 0

    override suspend fun getLastBackupTime(): LocalDateTime? {
        return lastBackupTime
    }

    override suspend fun backupDatabase(selfWorkId: UUID): Result<Unit> {
        return if (willBackupSucceed) {
            backupCount++
            Result.success(Unit)
        } else Result.failure(Exception("Test exception"))
    }

    override suspend fun restoreDatabase(selfWorkId: UUID): Result<Unit> {
        return if (willRestoreSucceed) {
            restoreCount++
            Result.success(Unit)
        } else Result.failure(Exception("Test exception"))
    }

    fun setLastBackupTime(time: LocalDateTime) {
        lastBackupTime = time
    }

    fun setWillBackupSucceed(willSucceed: Boolean) {
        willBackupSucceed = willSucceed
    }

    fun setWillRestoreSucceed(willSucceed: Boolean) {
        willRestoreSucceed = willSucceed
    }
}