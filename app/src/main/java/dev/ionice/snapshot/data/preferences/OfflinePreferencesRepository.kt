package dev.ionice.snapshot.data.preferences

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
import dev.ionice.snapshot.notifications.cancelAlarm
import dev.ionice.snapshot.notifications.setAlarm
import dev.ionice.snapshot.work.BackupSyncWorker
import dev.ionice.snapshot.work.PeriodicBackupSyncWorker
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
                    getAutoBackupConstraints(backupPrefs.autoBackupOnCellular)
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

    override suspend fun setAutomaticBackups(
        frequency: Int,
        time: LocalTime,
        useMeteredData: Boolean
    ) {
        val currPrefs = dataStore.data.first().toPreferences()
        if (!PreferencesRepository.BackupPrefs.ALLOWED_FREQS.contains(frequency)) {
            throw IllegalArgumentException("Illegal backup frequency value.")
        }
        dataStore.edit {
            it[Keys.BACKUP_FREQUENCY] = frequency
            it[Keys.BACKUP_TIME] = time.toSecondOfDay() * 1L
            it[Keys.BACKUP_ON_CELLULAR] = useMeteredData
        }
        if (currPrefs[Keys.BACKUP_ENABLED] == true) {
            setRecurringBackups(frequency, time, getAutoBackupConstraints(useMeteredData))
        }
    }

    override suspend fun setNotifsEnabled(enabled: Boolean) {
        dataStore.edit {
            it[Keys.NOTIFS_ENABLED] = enabled
        }
        val currPrefs = dataStore.data.first().toPreferences()
        if (enabled && currPrefs[Keys.NOTIFS_REMINDERS_ENABLED] == true) {
            setAlarm(
                appContext,
                currPrefs[Keys.NOTIFS_REMINDERS_TIME]?.let { LocalTime.ofSecondOfDay(it) }
                    ?: PreferencesRepository.NotifsPrefs.DEFAULT.reminderTime)
        } else {
            cancelAlarm(appContext)
        }
    }

    override suspend fun setDailyReminders(enabled: Boolean, time: LocalTime) {
        dataStore.edit {
            it[Keys.NOTIFS_REMINDERS_ENABLED] = enabled
            it[Keys.NOTIFS_REMINDERS_TIME] = time.toSecondOfDay() * 1L
        }
        val currPrefs = dataStore.data.first().toPreferences()
        if (enabled && currPrefs[Keys.NOTIFS_ENABLED] == true) {
            setAlarm(appContext, time)
        } else cancelAlarm(appContext)
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
            preferences[Keys.BACKUP_TIME]?.let { LocalTime.ofSecondOfDay(it) }
                ?: defaults.autoBackupTime
        val useCellular = preferences[Keys.BACKUP_ON_CELLULAR] ?: defaults.autoBackupOnCellular
        return PreferencesRepository.BackupPrefs(isEnabled, frequency, time, useCellular)
    }

    private fun mapNotificationsPreferences(preferences: Preferences): PreferencesRepository.NotifsPrefs {
        val defaults = PreferencesRepository.NotifsPrefs.DEFAULT
        val areNotifsEnabled = preferences[Keys.NOTIFS_ENABLED] ?: defaults.areNotifsEnabled
        val isRemindersEnabled =
            preferences[Keys.NOTIFS_REMINDERS_ENABLED] ?: defaults.isRemindersEnabled
        val reminderTime = preferences[Keys.NOTIFS_REMINDERS_TIME]?.let {
            LocalTime.ofSecondOfDay(it)
        } ?: defaults.reminderTime
        val isMemoriesEnabled =
            preferences[Keys.NOTIFS_MEMORIES_ENABLED] ?: defaults.isMemoriesEnabled
        return PreferencesRepository.NotifsPrefs(
            areNotifsEnabled, isRemindersEnabled, reminderTime, isMemoriesEnabled
        )
    }

    private fun setRecurringBackups(frequency: Int, time: LocalTime, constraints: Constraints) {
        if (frequency > 0) {
            val timeSinceMidnight = time.toSecondOfDay() * 1000

            val request = OneTimeWorkRequestBuilder<PeriodicBackupSyncWorker>()
                .setInitialDelay(
                    PeriodicBackupSyncWorker.getInitialDelay(time),
                    TimeUnit.MILLISECONDS
                )
                .setConstraints(constraints)
                .setInputData(
                    workDataOf(
                        BackupSyncWorker.WORK_TYPE to BackupSyncWorker.WORK_TYPE_BACKUP,
                        BackupSyncWorker.WORK_TIME_MS_PAST_MIDNIGHT to timeSinceMidnight
                    )
                )
                .build() // default retry is set to exponential with initial value of 10s, which is good
            WorkManager.getInstance(appContext).enqueueUniqueWork(
                PeriodicBackupSyncWorker.WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        } else {
            WorkManager.getInstance(appContext).cancelUniqueWork(PeriodicBackupSyncWorker.WORK_NAME)
        }
    }

    private fun getAutoBackupConstraints(useCellular: Boolean): Constraints {
        val networkType = if (useCellular) NetworkType.CONNECTED else NetworkType.UNMETERED
        return Constraints.Builder().setRequiredNetworkType(networkType).build()
    }

    private object Keys {
        private const val BACKUP_BASE = "backup"
        val BACKUP_ENABLED = booleanPreferencesKey("${BACKUP_BASE}_enabled")
        val BACKUP_FREQUENCY = intPreferencesKey("${BACKUP_BASE}_frequency")
        val BACKUP_TIME = longPreferencesKey("${BACKUP_BASE}_time")
        val BACKUP_ON_CELLULAR = booleanPreferencesKey("${BACKUP_BASE}_cellular")

        private const val NOTIFS_BASE = "notifications"
        val NOTIFS_ENABLED = booleanPreferencesKey("${NOTIFS_BASE}_any_enabled")
        val NOTIFS_REMINDERS_ENABLED = booleanPreferencesKey("${NOTIFS_BASE}_reminders_enabled")
        val NOTIFS_REMINDERS_TIME = longPreferencesKey("${NOTIFS_BASE}_reminders_time")
        val NOTIFS_MEMORIES_ENABLED = booleanPreferencesKey("${NOTIFS_BASE}_memories_enabled")
    }
}

