package me.ionice.snapshot.data.network

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.ionice.snapshot.work.OneOffBackupSyncWorker
import java.time.LocalDateTime

class NetworkRepositoryImpl(private val applicationContext: Context) : NetworkRepository {

    private val backupUtil = BackupUtil(applicationContext)
    private var isReceiverRegistered = false

    private val backupStatus = MutableStateFlow(NetworkRepository.BackupState(false, null, null))

    private fun registerReceiverIfNotRegistered() {
        if (!isReceiverRegistered) {
            val callback = { type: String, isSuccess: Boolean ->
                if (type != OneOffBackupSyncWorker.WORK_TYPE_BACKUP && type != OneOffBackupSyncWorker.WORK_TYPE_RESTORE) {
                    throw IllegalArgumentException("type must be one of WORK_TYPE_BACKUP or WORK_TYPE_RESTORE")
                }
                backupStatus.update {
                    NetworkRepository.BackupState(
                        isInProgress = false,
                        action = if (type == OneOffBackupSyncWorker.WORK_TYPE_BACKUP) ACTION_TYPE_BACKUP else ACTION_TYPE_RESTORE,
                        isSuccess = isSuccess
                    )
                }
            }
            applicationContext.registerReceiver(
                BackupStatusReceiver(callback),
                IntentFilter(BackupStatusReceiver.ACTION)
            )
            isReceiverRegistered = true
        }
    }

    override fun getBackupStatus(): NetworkRepository.BackupState = backupStatus.value

    override fun getBackupStatusFlow(): Flow<NetworkRepository.BackupState> = backupStatus

    override fun isOnline(): Boolean {
        val cm = ContextCompat.getSystemService(applicationContext, ConnectivityManager::class.java)

        val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override fun getLoggedInAccountEmail(): String? {
        return if (isOnline()) backupUtil.getLoggedInAccountEmail() else null
    }

    override suspend fun getLastBackupTime(): LocalDateTime? {
        return if (isOnline()) backupUtil.getLastBackupTime() else null
    }

    override fun startDatabaseBackup() {
        backupStatus.update { it.copy(isInProgress = true, action = ACTION_TYPE_BACKUP) }
        if (isOnline()) {
            registerReceiverIfNotRegistered()
            backupUtil.startBackup()
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
            registerReceiverIfNotRegistered()
            backupUtil.startRestore()
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

    private companion object {
        const val ACTION_TYPE_BACKUP = "Backup"
        const val ACTION_TYPE_RESTORE = "Restore"
    }
}