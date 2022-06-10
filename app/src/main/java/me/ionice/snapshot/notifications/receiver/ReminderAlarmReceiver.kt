package me.ionice.snapshot.notifications.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import me.ionice.snapshot.notifications.sendDailyReminder

class ReminderAlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val manager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        manager.sendDailyReminder(context)
    }

}