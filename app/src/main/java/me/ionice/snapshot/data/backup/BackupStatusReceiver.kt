package me.ionice.snapshot.data.backup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import me.ionice.snapshot.work.OneOffBackupSyncWorker

class BackupStatusReceiver(val callback: (type: String, status: Boolean) -> Unit) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(OneOffBackupSyncWorker.WORK_TYPE)
            ?: throw IllegalArgumentException("WORK_TYPE must be provided")
        val status = intent.getBooleanExtra(OneOffBackupSyncWorker.WORK_STATUS, false)
        callback(type, status)
    }

    companion object {
        const val ACTION = "me.ionice.snapshot.data.network.BackupStatusReceiver"
    }
}