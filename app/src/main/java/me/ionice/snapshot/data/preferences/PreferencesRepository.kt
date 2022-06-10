package me.ionice.snapshot.data.preferences

import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface PreferencesRepository {

    val backupPreferencesFlow: Flow<BackupPreferences>

    val notificationsPreferencesFlow: Flow<NotificationsPreferences>

    suspend fun getInitialBackupPreferences(): BackupPreferences

    suspend fun getInitialNotificationsPreferences(): NotificationsPreferences

    suspend fun setIsBackupEnabled(enable: Boolean)

    /**
     * Sets the automatic backup frequency. Allowed values are 1 and non-negative multiples of 7.
     *
     * 0 represents never, and all other values represent the number of days between automatic backups.
     */
    suspend fun setBackupFrequency(daysFreq: Int)

    suspend fun setBackupTime(time: LocalTime)

    /**
     * If set to true, queues up daily reminders.
     */
    suspend fun setIsDailyReminderEnabled(enable: Boolean)

    suspend fun setDailyReminderTime(time: LocalTime)

    suspend fun setIsMemoriesEnabled(enable: Boolean)

    /**
     * - `isEnabled`: is backup (manual or automatic) enabled
     * - `autoBackupFrequency`: 0 represents never, and all other values represent the number of days between automatic backups.
     * - `autoBackupTime`: the scheduled time for automatic backups; value is meaningless if autoBackupFrequency = 0
     */
    data class BackupPreferences(
        val isEnabled: Boolean,
        val autoBackupFrequency: Int,
        val autoBackupTime: LocalTime
    ) {
        companion object {
            val DEFAULT = BackupPreferences(false, 0, LocalTime.MIDNIGHT)
        }
    }

    /**
     * - `isRemindersEnabled`: is daily reminders enabled
     * - `reminderTime`: the scheduled time for reminder notification; value is meaningless if isReminderEnabled is false
     * - `isMemoriesEnabled`: is memories enabled
     */
    data class NotificationsPreferences(
        val isRemindersEnabled: Boolean,
        val reminderTime: LocalTime,
        val isMemoriesEnabled: Boolean
    ) {
        companion object {
            val DEFAULT = NotificationsPreferences(false, LocalTime.of(22, 0), false)
        }
    }
}