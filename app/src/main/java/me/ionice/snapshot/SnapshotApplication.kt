package me.ionice.snapshot

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.data.AppContainerImpl

class SnapshotApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val remindersChannel = NotificationChannel(
            getString(R.string.notification_reminders_channel_id),
            getString(R.string.notification_reminders_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        remindersChannel.enableLights(true)
        remindersChannel.lightColor = Color.YELLOW
        remindersChannel.enableVibration(true)
        remindersChannel.description =
            getString(R.string.notification_reminders_channel_description)

        val memoriesChannel = NotificationChannel(
            getString(R.string.notification_memories_channel_id),
            getString(R.string.notification_memories_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        memoriesChannel.enableLights(true)
        memoriesChannel.lightColor = Color.YELLOW
        memoriesChannel.enableVibration(true)
        memoriesChannel.description = getString(R.string.notification_memories_channel_description)

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(remindersChannel)
        manager.createNotificationChannel(memoriesChannel)
    }
}