package me.ionice.snapshot

import android.app.Application
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.data.AppContainerImpl
import me.ionice.snapshot.work.BackupSyncWorker
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class SnapshotApplication : Application() {

    lateinit var container: AppContainer

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setRecurringBackups()
        }
    }

    private fun setRecurringBackups() {

        val backupFreq = container.networkRepository.getBackupFrequency()

        if (backupFreq > 0) {
            // TODO: un-hardcode backup time, add constraints
            val targetBackupTime = LocalTime.of(2, 0).toSecondOfDay()
            val currTime = LocalTime.now().toSecondOfDay()

            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

            val initialDelay =
                if (currTime > targetBackupTime) (24 * 60 * 60 - (currTime - targetBackupTime)) else (targetBackupTime - currTime)
            val request = PeriodicWorkRequestBuilder<BackupSyncWorker>(
                backupFreq.toLong(),
                TimeUnit.DAYS
            ).setInitialDelay(
                initialDelay.toLong(), TimeUnit.SECONDS
            ).setConstraints(constraints)
                .build() // default retry is set to exponential with initial value of 10s, which is good
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                BackupSyncWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        } else {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(BackupSyncWorker.WORK_NAME)
        }
    }
}