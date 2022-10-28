package dev.ionice.snapshot.data.backup

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import androidx.work.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dev.ionice.snapshot.work.BackupSyncWorker
import dev.ionice.snapshot.work.OneOffBackupSyncWorker
import java.time.LocalDateTime

class GDriveBackupRepository(private val appContext: Context) : BackupRepository {

    private val backupModule = GDriveBackupModule(appContext)
    private val backupStatus = MutableStateFlow(
        BackupRepository.BackupStatus(
            isInProgress = backupModule.hasRunningJobs(),
            action = null,
            isSuccess = false
        )
    )

    init {
        val callback = { isInProgress: Boolean, type: String, isSuccess: Boolean ->
            if (type != BackupSyncWorker.WORK_TYPE_BACKUP && type != BackupSyncWorker.WORK_TYPE_RESTORE) {
                throw IllegalArgumentException("type must be one of WORK_TYPE_BACKUP or WORK_TYPE_RESTORE")
            }
            backupStatus.update {
                BackupRepository.BackupStatus(
                    isInProgress = isInProgress,
                    action = if (type == BackupSyncWorker.WORK_TYPE_BACKUP) ACTION_TYPE_BACKUP else ACTION_TYPE_RESTORE,
                    isSuccess = isSuccess
                )
            }

            if (type == BackupSyncWorker.WORK_TYPE_RESTORE && isSuccess) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(3000)
                    restartApp()
                }
            }
        }
        appContext.registerReceiver(
            BackupStatusReceiver(callback),
            IntentFilter(BackupStatusReceiver.ACTION)
        )
    }

    override fun getBackupStatusFlow(): Flow<BackupRepository.BackupStatus> = backupStatus

    override fun isOnline(): Boolean {
        val cm = ContextCompat.getSystemService(appContext, ConnectivityManager::class.java)

        val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override fun getLoggedInAccountEmail(): String? {
        return if (isOnline()) GoogleSignIn.getLastSignedInAccount(appContext)?.email else null
    }

    override suspend fun getLastBackupTime(): LocalDateTime? {
        return if (isOnline()) backupModule.getLastBackupTime() else null
    }

    override fun startDatabaseBackup() {
        if (isOnline()) {
            enqueueBackupWork(actionType = BackupSyncWorker.WORK_TYPE_BACKUP)
        } else {
            backupStatus.update {
                it.copy(
                    isInProgress = false,
                    action = ACTION_TYPE_BACKUP,
                    isSuccess = false
                )
            }
        }
    }

    override fun startDatabaseRestore() {
        if (isOnline()) {
            enqueueBackupWork(actionType = BackupSyncWorker.WORK_TYPE_RESTORE)
        } else {
            backupStatus.update {
                it.copy(
                    isInProgress = false,
                    action = ACTION_TYPE_RESTORE,
                    isSuccess = false
                )
            }
        }
    }

    private fun restartApp() {
        val packageManager = appContext.packageManager
        val intent = packageManager.getLaunchIntentForPackage(appContext.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        appContext.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    private fun enqueueBackupWork(actionType: String) {
        val backupRequest =
            OneTimeWorkRequestBuilder<OneOffBackupSyncWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(workDataOf(BackupSyncWorker.WORK_TYPE to actionType))
                .build()
        WorkManager.getInstance(appContext)
            .enqueueUniqueWork(
                OneOffBackupSyncWorker.WORK_NAME,
                ExistingWorkPolicy.KEEP,
                backupRequest
            )
    }

    private companion object {
        const val ACTION_TYPE_BACKUP = "Backup"
        const val ACTION_TYPE_RESTORE = "Restore"
    }
}