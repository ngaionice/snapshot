package dev.ionice.snapshot.sync.work

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ionice.snapshot.sync.BackupModule
import dev.ionice.snapshot.sync.receivers.BackupStatusReceiver
import dev.ionice.snapshot.sync.work.BackupSyncWorker.WORK_IN_PROGRESS
import dev.ionice.snapshot.sync.work.BackupSyncWorker.WORK_STATUS
import dev.ionice.snapshot.sync.work.BackupSyncWorker.WORK_TIME_MS_PAST_MIDNIGHT
import dev.ionice.snapshot.sync.work.BackupSyncWorker.WORK_TYPE
import dev.ionice.snapshot.sync.work.BackupSyncWorker.WORK_TYPE_BACKUP
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

object BackupSyncWorker {
    const val WORK_STATUS = "status"
    const val WORK_TYPE = "type"
    const val WORK_TYPE_BACKUP = "backup"
    const val WORK_TYPE_RESTORE = "restore"
    const val WORK_IN_PROGRESS = "progress"
    const val WORK_TIME_MS_PAST_MIDNIGHT = "scheduledTime"
}

@HiltWorker
class PeriodicBackupSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val backupModule: BackupModule
) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val scheduledTime = inputData.getInt(WORK_TIME_MS_PAST_MIDNIGHT, -1)
        if (scheduledTime == -1) throw IllegalArgumentException("Missing value for WORK_TIME_MS_PAST_MIDNIGHT.")

        sendStartBroadcast()
        val isSuccess = backupModule.backupDatabase(id).isSuccess
        sendEndBroadcast(isSuccess)

        if (isSuccess) {
            scheduleNewDailyWork(scheduledTime)
            return Result.success()
        }

        return Result.retry()
    }

    private fun sendStartBroadcast() {
        val intent = Intent(BackupStatusReceiver.ACTION)
            .putExtra(WORK_IN_PROGRESS, true)
            .putExtra(WORK_TYPE, WORK_TYPE_BACKUP)

        applicationContext.sendBroadcast(intent)
    }

    private fun sendEndBroadcast(isSuccess: Boolean) {
        val intent = Intent(BackupStatusReceiver.ACTION)
            .putExtra(WORK_IN_PROGRESS, false)
            .putExtra(WORK_TYPE, WORK_TYPE_BACKUP)
            .putExtra(WORK_STATUS, isSuccess)

        applicationContext.sendBroadcast(intent)
    }

    private fun scheduleNewDailyWork(msSinceMidnight: Int) {
        val startTime = LocalTime.ofSecondOfDay((msSinceMidnight / 1000).toLong())
        val dailyWorkRequest = OneTimeWorkRequestBuilder<PeriodicBackupSyncWorker>()
            .setInitialDelay(getInitialDelay(startTime), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(dailyWorkRequest)
    }

    companion object {
        const val WORK_NAME = "dev.ionice.PeriodicBackupSyncWorker"

        fun getInitialDelay(time: LocalTime): Long {
            val currTime = Calendar.getInstance()
            val runTime = Calendar.getInstance()

            runTime.set(Calendar.HOUR_OF_DAY, time.hour)
            runTime.set(Calendar.MINUTE, time.minute)
            runTime.set(Calendar.SECOND, 0)

            if (runTime.before(currTime)) {
                runTime.add(Calendar.HOUR_OF_DAY, 24)
            }

            return runTime.timeInMillis - currTime.timeInMillis
        }
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
            ?: throw IllegalArgumentException("Illegal WORK_TYPE value.")
        sendStartBroadcast(actionType)
        val isSuccess = if (actionType == WORK_TYPE_BACKUP) {
            backupModule.backupDatabase(id).isSuccess
        } else {
            backupModule.restoreDatabase(id).isSuccess
        }
        sendEndBroadcast(actionType, isSuccess)
        return Result.success(workDataOf(WORK_STATUS to isSuccess, WORK_TYPE to actionType))
    }

    private fun sendStartBroadcast(actionType: String) {
        val intent = Intent(BackupStatusReceiver.ACTION)
            .putExtra(WORK_IN_PROGRESS, true)
            .putExtra(WORK_TYPE, actionType)

        applicationContext.sendBroadcast(intent)
    }

    private fun sendEndBroadcast(actionType: String, isSuccess: Boolean) {
        val intent = Intent(BackupStatusReceiver.ACTION)
            .putExtra(WORK_IN_PROGRESS, false)
            .putExtra(WORK_TYPE, actionType)
            .putExtra(WORK_STATUS, isSuccess)

        applicationContext.sendBroadcast(intent)
    }

    companion object {
        const val WORK_NAME = "dev.ionice.OneOffBackupSyncWorker"
    }
}