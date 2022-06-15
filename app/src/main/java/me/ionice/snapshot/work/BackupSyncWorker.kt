package me.ionice.snapshot.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import me.ionice.snapshot.data.network.NetworkRepositoryImpl

class PeriodicBackupSyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val networkRepository = NetworkRepositoryImpl(applicationContext)

        if (networkRepository.backupDatabase().isFailure) {
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
        val networkRepository = NetworkRepositoryImpl(applicationContext)
        val isSuccess = if (actionType == WORK_TYPE_BACKUP) {
            networkRepository.backupDatabase().isSuccess
        } else {
            networkRepository.restoreDatabase().isSuccess
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