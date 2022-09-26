package me.ionice.snapshot.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import me.ionice.snapshot.data.backup.GDriveBackupModule

class PeriodicBackupSyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val backupModule = GDriveBackupModule(applicationContext)
        return if (backupModule.backupDatabase(id).isFailure) {
            Result.retry()
        } else Result.success()
    }

    companion object {
        const val WORK_NAME = "me.ionice.snapshot.work.PeriodicBackupSyncWorker"
    }
}

class OneOffBackupSyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val actionType = inputData.getString(WORK_TYPE)
        val backupModule = GDriveBackupModule(applicationContext)
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