package dev.ionice.snapshot.core.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.ionice.snapshot.core.notifications.receivers.ReminderAlarmReceiver
import java.time.LocalTime

const val DAILY_REMINDER_ID = 0
//private const val memoriesId = 1

fun NotificationManager.sendDailyReminder(appContext: Context) {

    val contentIntent = appContext.packageManager.getLaunchIntentForPackage("dev.ionice.snapshot")
    val pendingIntent = PendingIntent.getActivity(
        appContext,
        DAILY_REMINDER_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat
        .Builder(appContext, appContext.getString(R.string.notification_reminders_channel_id))

        .setSmallIcon(R.drawable.ic_edit_notification)
        .setContentTitle(appContext.getString(R.string.notification_reminder_title))
        .setContentText(appContext.getString(R.string.notification_reminder_body))

        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(DAILY_REMINDER_ID, builder.build())
}

fun NotificationManager.createNotificationChannels(appContext: Context) {
    val remindersChannel = NotificationChannel(
        appContext.getString(R.string.notification_reminders_channel_id),
        appContext.getString(R.string.notification_reminders_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    )

    remindersChannel.enableLights(true)
    remindersChannel.lightColor = Color.YELLOW
    remindersChannel.enableVibration(true)
    remindersChannel.description =
        appContext.getString(R.string.notification_reminders_channel_description)

    val memoriesChannel = NotificationChannel(
        appContext.getString(R.string.notification_memories_channel_id),
        appContext.getString(R.string.notification_memories_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    )

    memoriesChannel.enableLights(true)
    memoriesChannel.lightColor = Color.YELLOW
    memoriesChannel.enableVibration(true)
    memoriesChannel.description =
        appContext.getString(R.string.notification_memories_channel_description)

    this.createNotificationChannel(remindersChannel)
    this.createNotificationChannel(memoriesChannel)
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