package me.ionice.snapshot.data.backup

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import androidx.work.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.ionice.snapshot.work.BackupStatusNotifyWorker
import me.ionice.snapshot.work.OneOffBackupSyncWorker
import java.time.LocalDateTime

class GDriveBackupRepository(private val appContext: Context) : BackupRepository {

    private val backupModule = GDriveBackupModule(appContext)
    private val backupStatus = MutableStateFlow(BackupRepository.BackupStatus(false, null, false))

    init {
        val callback = { type: String, isSuccess: Boolean ->
            if (type != OneOffBackupSyncWorker.WORK_TYPE_BACKUP && type != OneOffBackupSyncWorker.WORK_TYPE_RESTORE) {
                throw IllegalArgumentException("type must be one of WORK_TYPE_BACKUP or WORK_TYPE_RESTORE")
            }
            backupStatus.update {
                BackupRepository.BackupStatus(
                    isInProgress = false,
                    action = if (type == OneOffBackupSyncWorker.WORK_TYPE_BACKUP) ACTION_TYPE_BACKUP else ACTION_TYPE_RESTORE,
                    isSuccess = isSuccess
                )
            }
        }
        appContext.registerReceiver(
            BackupStatusReceiver(callback),
            IntentFilter(BackupStatusReceiver.ACTION)
        )
    }

    override fun getBackupStatus(): BackupRepository.BackupStatus = backupStatus.value

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
        backupStatus.update { it.copy(isInProgress = true, action = ACTION_TYPE_BACKUP) }
        if (isOnline()) {
            enqueueBackupWork(actionType = OneOffBackupSyncWorker.WORK_TYPE_BACKUP)
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
        backupStatus.update { it.copy(isInProgress = true, action = ACTION_TYPE_RESTORE) }
        if (isOnline()) {
            enqueueBackupWork(actionType = OneOffBackupSyncWorker.WORK_TYPE_RESTORE)
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

    private fun enqueueBackupWork(actionType: String) {
        val backupRequest =
            OneTimeWorkRequestBuilder<OneOffBackupSyncWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(workDataOf(OneOffBackupSyncWorker.WORK_TYPE to actionType))
                .build()
        val broadcastRequest =
            OneTimeWorkRequestBuilder<BackupStatusNotifyWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        WorkManager.getInstance(appContext)
            .beginUniqueWork(
                OneOffBackupSyncWorker.WORK_NAME,
                ExistingWorkPolicy.KEEP,
                backupRequest
            )
            .then(broadcastRequest)
            .enqueue()
    }

    private companion object {
        const val ACTION_TYPE_BACKUP = "Backup"
        const val ACTION_TYPE_RESTORE = "Restore"
    }
}