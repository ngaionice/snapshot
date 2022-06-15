package me.ionice.snapshot.work

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.ionice.snapshot.data.network.BackupStatusReceiver

class BackupStatusNotifyWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val type =
            inputData.getString(OneOffBackupSyncWorker.WORK_TYPE) ?: throw IllegalArgumentException(
                "WORK_TYPE must be set"
            )
        val status = inputData.getBoolean(OneOffBackupSyncWorker.WORK_STATUS, false)

        val intent = Intent(BackupStatusReceiver.ACTION)
        intent.putExtra(OneOffBackupSyncWorker.WORK_TYPE, type)
        intent.putExtra(OneOffBackupSyncWorker.WORK_STATUS, status)

        applicationContext.sendBroadcast(intent)
        return Result.success()
    }

}