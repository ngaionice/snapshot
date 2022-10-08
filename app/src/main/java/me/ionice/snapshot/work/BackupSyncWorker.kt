package me.ionice.snapshot.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.ionice.snapshot.data.backup.BackupModule

@HiltWorker
class PeriodicBackupSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val backupModule: BackupModule
) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return if (backupModule.backupDatabase(id).isFailure) {
            Result.retry()
        } else Result.success()
    }

    companion object {
        const val WORK_NAME = "me.ionice.snapshot.work.PeriodicBackupSyncWorker"
    }
}

@HiltWorker
class OneOffBackupSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val backupModule: BackupModule
) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val actionType = inputData.getString(WORK_TYPE)
        val isSuccess = if (actionType == WORK_TYPE_BACKUP) {
            backupModule.backupDatabase(id).isSuccess
        } else {
            backupModule.restoreDatabase(id).isSuccess
        }
        return Result.success(workDataOf(WORK_STATUS to isSuccess, WORK_TYPE to actionType))
    }

    companion object {
        const val WORK_NAME = "me.ionice.snapshot.work.OneOffBackupSyncWorker"
        const val WORK_STATUS = "status"
        const val WORK_TYPE = "type"
        const val WORK_TYPE_BACKUP = "backup"
        const val WORK_TYPE_RESTORE = "restore"
    }
}