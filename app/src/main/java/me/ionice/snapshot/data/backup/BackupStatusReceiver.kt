package me.ionice.snapshot.data.backup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import me.ionice.snapshot.work.BackupSyncWorker

class BackupStatusReceiver(val callback: (isStart: Boolean, type: String, status: Boolean) -> Unit) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getBooleanExtra(BackupSyncWorker.WORK_IN_PROGRESS, true)
        val type = intent.getStringExtra(BackupSyncWorker.WORK_TYPE)
            ?: throw IllegalArgumentException("WORK_TYPE must be provided")
        val status = intent.getBooleanExtra(BackupSyncWorker.WORK_STATUS, false)
        callback(action, type, status)
    }

    companion object {
        const val ACTION = "me.ionice.snapshot.data.network.BackupStatusReceiver"
    }
}