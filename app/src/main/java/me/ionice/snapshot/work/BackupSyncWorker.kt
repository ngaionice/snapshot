package me.ionice.snapshot.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.ionice.snapshot.data.network.NetworkRepositoryImpl

class BackupSyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val networkRepository = NetworkRepositoryImpl(applicationContext)

        if (networkRepository.backupDatabase().isFailure) {
            return Result.retry()
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "me.ionice.snapshot.work.BackupSyncWorker"
    }
}