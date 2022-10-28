package dev.ionice.snapshot

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SnapshotApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .setWorkerFactory(workerFactory)
                .build()
        } else {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.ERROR)
                .setWorkerFactory(workerFactory)
                .build()
        }
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