package me.ionice.snapshot.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import java.time.LocalDateTime

class NetworkRepositoryImpl(private val applicationContext: Context) : NetworkRepository {

    private val backupUtil = BackupUtil(applicationContext)

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

    override suspend fun backupDatabase(): Result<Unit> {
        return if (isOnline()) backupUtil.backupDatabase() else Result.failure(Exception("Device is not connected to the internet."))
    }

    override suspend fun restoreDatabase(): Result<Unit> {
        return if (isOnline()) backupUtil.restoreDatabase() else Result.failure(Exception("Device is not connected to the internet."))
    }
}