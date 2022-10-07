package me.ionice.snapshot.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.ionice.snapshot.notifications.cancelAlarm
import me.ionice.snapshot.notifications.setAlarm
import me.ionice.snapshot.work.PeriodicBackupSyncWorker
import java.io.IOException
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class OfflinePreferencesRepository(private val appContext: Context) : PreferencesRepository {

    private val Context.datastore by preferencesDataStore(name = "snapshot_preferences")
    private val dataStore = appContext.datastore

    init {
        val manager = WorkManager.getInstance(appContext)
        registerBackgroundActions(manager)
    }

    private fun registerBackgroundActions(manager: WorkManager) {
        CoroutineScope(Dispatchers.IO).launch {
            val preferences = dataStore.data.first().toPreferences()
            val backupPrefs = mapBackupPreferences(preferences)
            val notificationsPrefs = mapNotificationsPreferences(preferences)
            if (backupPrefs.autoBackupFrequency > 0 && manager.getWorkInfosForUniqueWork(
                    PeriodicBackupSyncWorker.WORK_NAME
                ).get().isEmpty()
            ) {
                setRecurringBackups(
                    backupPrefs.autoBackupFrequency,
                    backupPrefs.autoBackupTime,
                    autoBackupConstraints
                )
            }
            if (notificationsPrefs.isRemindersEnabled) {
                val reminderTime =
                    dataStore.data.first().toPreferences()[Keys.NOTIFS_REMINDERS_TIME]
                setAlarm(
                    appContext,
                    if (reminderTime != null) LocalTime.ofSecondOfDay(reminderTime) else PreferencesRepository.NotifsPrefs.DEFAULT.reminderTime
                )
            }
        }
    }

    override fun getBackupPrefsFlow(): Flow<PreferencesRepository.BackupPrefs> =
        dataStore.data.catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }.map { mapBackupPreferences(it) }

    override fun getNotifsPrefsFlow(): Flow<PreferencesRepository.NotifsPrefs> =
        dataStore.data.catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }.map { mapNotificationsPreferences(it) }

    override suspend fun setBackupEnabled(enabled: Boolean) {
        dataStore.edit {
            it[Keys.BACKUP_ENABLED] = enabled
        }
    }

    override suspend fun setAutomaticBackups(frequency: Int, time: LocalTime) {
        val currPrefs = dataStore.data.first().toPreferences()
        if (!PreferencesRepository.BackupPrefs.ALLOWED_FREQS.contains(frequency)) {
            throw IllegalArgumentException("Illegal backup frequency value.")
        }
        dataStore.edit {
            it[Keys.BACKUP_FREQUENCY] = frequency
            it[Keys.BACKUP_TIME] = time.toSecondOfDay() * 1L
        }
        if (currPrefs[Keys.BACKUP_ENABLED] == true) {
            setRecurringBackups(frequency, time, autoBackupConstraints)
        }
    }

    override suspend fun setDailyReminderTime(time: LocalTime) {
        dataStore.edit {
            it[Keys.NOTIFS_REMINDERS_TIME] = time.toSecondOfDay() * 1L
        }
        if (dataStore.data.first().toPreferences()[Keys.NOTIFS_REMINDERS_ENABLED] == true) {
            setAlarm(appContext, time)
        }
    }

    override suspend fun setDailyReminderEnabled(enabled: Boolean) {
        dataStore.edit {
            it[Keys.NOTIFS_REMINDERS_ENABLED] = enabled
        }
        if (enabled) {
            val existingTime = dataStore.data.first().toPreferences()[Keys.NOTIFS_REMINDERS_TIME]
            val time = if (existingTime != null) {
                LocalTime.ofSecondOfDay(existingTime)
            } else {
                PreferencesRepository.NotifsPrefs.DEFAULT.reminderTime
            }
            setAlarm(appContext, time)
        } else {
            cancelAlarm(appContext)
        }
    }

    override suspend fun setMemoriesEnabled(enabled: Boolean) {
        dataStore.edit {
            it[Keys.NOTIFS_MEMORIES_ENABLED] = enabled
        }
    }

    private fun mapBackupPreferences(preferences: Preferences): PreferencesRepository.BackupPrefs {
        val defaults = PreferencesRepository.BackupPrefs.DEFAULT
        val isEnabled = preferences[Keys.BACKUP_ENABLED] ?: defaults.isEnabled
        val frequency = preferences[Keys.BACKUP_FREQUENCY] ?: defaults.autoBackupFrequency
        val time =
            preferences[Keys.BACKUP_TIME]?.let { LocalTime.ofSecondOfDay(it) } ?: defaults.autoBackupTime
        return PreferencesRepository.BackupPrefs(isEnabled, frequency, time)
    }

    private fun mapNotificationsPreferences(preferences: Preferences): PreferencesRepository.NotifsPrefs {
        val defaults = PreferencesRepository.NotifsPrefs.DEFAULT
        val isRemindersEnabled = preferences[Keys.NOTIFS_REMINDERS_ENABLED] ?: defaults.isRemindersEnabled
        val reminderTime = preferences[Keys.NOTIFS_REMINDERS_TIME]?.let {
            LocalTime.ofSecondOfDay(it)
        } ?: defaults.reminderTime
        val isMemoriesEnabled = preferences[Keys.NOTIFS_MEMORIES_ENABLED] ?: defaults.isMemoriesEnabled
        return PreferencesRepository.NotifsPrefs(
            isRemindersEnabled, reminderTime, isMemoriesEnabled
        )
    }

    private fun setRecurringBackups(frequency: Int, time: LocalTime, constraints: Constraints) {
        if (frequency > 0) {
            val targetBackupTime = time.toSecondOfDay()
            val currTime = LocalTime.now().toSecondOfDay()

            val initialDelay =
                if (currTime > targetBackupTime) (24 * 60 * 60 - (currTime - targetBackupTime)) else (targetBackupTime - currTime)
            val request = PeriodicWorkRequestBuilder<PeriodicBackupSyncWorker>(
                frequency.toLong(), TimeUnit.DAYS
            ).setInitialDelay(
                initialDelay.toLong(), TimeUnit.SECONDS
            ).setConstraints(constraints)
                .build() // default retry is set to exponential with initial value of 10s, which is good
            WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
                PeriodicBackupSyncWorker.WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, request
            )
        } else {
            WorkManager.getInstance(appContext).cancelUniqueWork(PeriodicBackupSyncWorker.WORK_NAME)
        }
    }

    private object Keys {
        private const val BACKUP_BASE = "backup"
        val BACKUP_ENABLED = booleanPreferencesKey("${BACKUP_BASE}_enabled")
        val BACKUP_FREQUENCY = intPreferencesKey("${BACKUP_BASE}_frequency")
        val BACKUP_TIME = longPreferencesKey("${BACKUP_BASE}_time")

        private const val NOTIFS_BASE = "notifications"
        val NOTIFS_REMINDERS_ENABLED = booleanPreferencesKey("${NOTIFS_BASE}_reminders_enabled")
        val NOTIFS_REMINDERS_TIME = longPreferencesKey("${NOTIFS_BASE}_reminders_time")
        val NOTIFS_MEMORIES_ENABLED = booleanPreferencesKey("${NOTIFS_BASE}_memories_enabled")
    }
}

// TODO: set up a proper function/method to store constraints and not hardcode it
val autoBackupConstraints =
    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()