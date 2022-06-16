package me.ionice.snapshot.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import me.ionice.snapshot.data.network.BackupUtil

class PeriodicBackupSyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val backupUtil = BackupUtil(applicationContext)

        if (backupUtil.backupDatabase().isFailure) {
            return Result.retry()
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "me.ionice.snapshot.work.PeriodicBackupSyncWorker"
    }
}

class OneOffBackupSyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val actionType = inputData.getString(WORK_TYPE)
        val backupUtil = BackupUtil(applicationContext)
        val isSuccess = if (actionType == WORK_TYPE_BACKUP) {
            backupUtil.backupDatabase().isSuccess
        } else {
            backupUtil.restoreDatabase().isSuccess
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