package dev.ionice.snapshot.core.data.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface PreferencesRepository {

    fun getBackupPrefsFlow(): Flow<BackupPrefs>

    fun getNotifsPrefsFlow(): Flow<NotifsPrefs>

    suspend fun setBackupEnabled(enabled: Boolean)

    /**
     * Allowed frequencies must be one of the values in [PreferencesRepository.BackupPrefs.ALLOWED_FREQS].
     * 0 represents never, and all other values represent the number of days between automatic backups.
     */
    suspend fun setAutomaticBackups(frequency: Int, time: LocalTime, useMeteredData: Boolean)

    suspend fun setNotifsEnabled(enabled: Boolean)

    suspend fun setDailyReminders(enabled: Boolean, time: LocalTime)

    suspend fun setMemoriesEnabled(enabled: Boolean)

    /**
     * - `isEnabled`: is backup (manual or automatic) enabled
     * - `autoBackupFrequency`: 0 represents never, and all other values represent the number of days between automatic backups.
     * - `autoBackupTime`: the scheduled time for automatic backups; value is meaningless if autoBackupFrequency = 0
     */
    data class BackupPrefs(
        val isEnabled: Boolean,
        val autoBackupFrequency: Int,
        val autoBackupTime: LocalTime,
        val autoBackupOnCellular: Boolean
    ) {
        companion object {
            val DEFAULT = BackupPrefs(false, 0, LocalTime.MIDNIGHT, false)
            val ALLOWED_FREQS = listOf(0, 1, 7, 30)
        }
    }

    /**
     * - `isRemindersEnabled`: is daily reminders enabled
     * - `reminderTime`: the scheduled time for reminder notification; value is meaningless if isReminderEnabled is false
     * - `isMemoriesEnabled`: is memories enabled
     */
    data class NotifsPrefs(
        val areNotifsEnabled: Boolean,
        val isRemindersEnabled: Boolean,
        val reminderTime: LocalTime,
        val isMemoriesEnabled: Boolean
    ) {
        companion object {
            val DEFAULT = NotifsPrefs(areNotifsEnabled = false,
                isRemindersEnabled = false,
                reminderTime = LocalTime.of(22, 0),
                isMemoriesEnabled = false
            )
        }
    }
}