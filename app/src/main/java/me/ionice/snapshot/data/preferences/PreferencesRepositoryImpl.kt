package me.ionice.snapshot.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalTime

class PreferencesRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    PreferencesRepository {

    override val backupPreferencesFlow = dataStore.data.catch { e ->
        if (e is IOException) {
            emit(emptyPreferences())
        } else {
            throw e
        }
    }.map { preferences -> mapBackupPreferences(preferences) }

    override val notificationsPreferencesFlow = dataStore.data.catch { e ->
        if (e is IOException) {
            emit(emptyPreferences())
        } else {
            throw e
        }
    }.map { mapNotificationsPreferences(it) }

    override suspend fun getInitialBackupPreferences(): PreferencesRepository.BackupPreferences = mapBackupPreferences(dataStore.data.first().toPreferences())

    override suspend fun getInitialNotificationsPreferences(): PreferencesRepository.NotificationsPreferences = mapNotificationsPreferences(dataStore.data.first().toPreferences())

    override suspend fun setIsBackupEnabled(enable: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.BACKUP_ENABLED_KEY] = enable
        }
    }

    override suspend fun setBackupFrequency(daysFreq: Int) {
        if (daysFreq < 0 || (daysFreq.mod(7) != 0 && daysFreq != 1)) {
            throw IllegalArgumentException("Backup day frequency has to be either 1, or a non-negative multiple of 7.")
        }
        dataStore.edit {
            it[PreferencesKeys.BACKUP_FREQUENCY_KEY] = daysFreq
        }
    }

    override suspend fun setBackupTime(time: LocalTime) {
        dataStore.edit {
            it[PreferencesKeys.BACKUP_TIME_KEY] = time.toSecondOfDay() * 1000L
        }
    }

    override suspend fun setDailyReminderTime(time: LocalTime) {
        dataStore.edit {
            it[PreferencesKeys.NOTIFICATIONS_REMINDERS_TIME_KEY] = time.toSecondOfDay() * 1000L
        }
    }

    override suspend fun setIsDailyReminderEnabled(enable: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.NOTIFICATIONS_REMINDERS_ENABLED_KEY] = enable
        }
    }

    override suspend fun setIsMemoriesEnabled(enable: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.NOTIFICATIONS_MEMORIES_ENABLED_KEY] = enable
        }
    }

    private fun mapBackupPreferences(preferences: Preferences): PreferencesRepository.BackupPreferences {
        val isEnabled = preferences[PreferencesKeys.BACKUP_ENABLED_KEY] ?: false
        val frequency = preferences[PreferencesKeys.BACKUP_FREQUENCY_KEY] ?: 0
        val time = preferences[PreferencesKeys.BACKUP_TIME_KEY]?.let { LocalTime.ofSecondOfDay(it) } ?: LocalTime.MIDNIGHT
        return PreferencesRepository.BackupPreferences(isEnabled, frequency, time)
    }

    private fun mapNotificationsPreferences(preferences: Preferences): PreferencesRepository.NotificationsPreferences {
        val isRemindersEnabled =
            preferences[PreferencesKeys.NOTIFICATIONS_REMINDERS_ENABLED_KEY] ?: false
        val reminderTime = preferences[PreferencesKeys.NOTIFICATIONS_REMINDERS_TIME_KEY]?.let {
            LocalTime.ofSecondOfDay(it)
        } ?: LocalTime.of(22, 0)
        val isMemoriesEnabled = preferences[PreferencesKeys.NOTIFICATIONS_MEMORIES_ENABLED_KEY] ?: false
        return PreferencesRepository.NotificationsPreferences(isRemindersEnabled, reminderTime, isMemoriesEnabled)
    }

    private object PreferencesKeys {
        private const val BACKUP_BASE_KEY = "backup"
        val BACKUP_ENABLED_KEY = booleanPreferencesKey("${BACKUP_BASE_KEY}_enabled")
        val BACKUP_FREQUENCY_KEY = intPreferencesKey("${BACKUP_BASE_KEY}_frequency")
        val BACKUP_TIME_KEY = longPreferencesKey("${BACKUP_BASE_KEY}_time")

        private const val NOTIFICATIONS_BASE_KEY = "notifications"
        val NOTIFICATIONS_REMINDERS_ENABLED_KEY =
            booleanPreferencesKey("${NOTIFICATIONS_BASE_KEY}_reminders_enabled")
        val NOTIFICATIONS_REMINDERS_TIME_KEY =
            longPreferencesKey("${NOTIFICATIONS_BASE_KEY}_reminders_time")
        val NOTIFICATIONS_MEMORIES_ENABLED_KEY =
            booleanPreferencesKey("${NOTIFICATIONS_BASE_KEY}_memories_enabled")
    }


}