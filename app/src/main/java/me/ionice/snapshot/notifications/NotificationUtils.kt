package me.ionice.snapshot.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import me.ionice.snapshot.MainActivity
import me.ionice.snapshot.R
import me.ionice.snapshot.notifications.receiver.ReminderAlarmReceiver
import java.time.LocalTime
import java.util.*

const val DAILY_REMINDER_ID = 0
//private const val memoriesId = 1

fun NotificationManager.sendDailyReminder(appContext: Context) {

    val contentIntent = Intent(appContext, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(appContext, DAILY_REMINDER_ID, contentIntent, PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat
        .Builder(appContext, appContext.getString(R.string.notification_reminders_channel_id))

        .setSmallIcon(R.drawable.ic_edit_notification)
        .setContentTitle(appContext.getString(R.string.notification_reminder_title))
        .setContentText(appContext.getString(R.string.notification_reminder_body))

        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(DAILY_REMINDER_ID, builder.build())
}

fun setAlarm(context: Context, time: LocalTime) {
    val manager = ContextCompat.getSystemService(context, AlarmManager::class.java) as AlarmManager

    val intent = Intent(context, ReminderAlarmReceiver::class.java)
    val pendingIntent =
        PendingIntent.getBroadcast(context, DAILY_REMINDER_ID, intent, PendingIntent.FLAG_IMMUTABLE)

    val targetTime = time.toSecondOfDay()
    val currTime = LocalTime.now().toSecondOfDay()
    val delay = if (currTime >= targetTime) {
        // remaining time in the day + time from start of day
        targetTime + LocalTime.MAX.toSecondOfDay() - currTime
    } else {
        targetTime - currTime
    } * 1000L

    val repeatInterval = AlarmManager.INTERVAL_DAY
    manager.setInexactRepeating(
        AlarmManager.RTC,
        System.currentTimeMillis() + delay,
        repeatInterval,
        pendingIntent
    )
}

fun cancelAlarm(context: Context) {
    val manager = ContextCompat.getSystemService(context, AlarmManager::class.java) as AlarmManager

    val intent = Intent(context, ReminderAlarmReceiver::class.java)
    val pendingIntent =
        PendingIntent.getBroadcast(context, DAILY_REMINDER_ID, intent, PendingIntent.FLAG_IMMUTABLE)
    manager.cancel(pendingIntent)
}