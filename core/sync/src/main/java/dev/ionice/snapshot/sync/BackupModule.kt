package dev.ionice.snapshot.sync

import java.time.LocalDateTime
import java.util.*

interface BackupModule {

    suspend fun getLastBackupTime(): LocalDateTime?

    suspend fun backupDatabase(selfWorkId: UUID): Result<Unit>

    suspend fun restoreDatabase(selfWorkId: UUID): Result<Unit>
}
