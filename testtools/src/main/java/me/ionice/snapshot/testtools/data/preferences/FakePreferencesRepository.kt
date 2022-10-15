package me.ionice.snapshot.testtools.data.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.ionice.snapshot.data.preferences.PreferencesRepository
import java.time.LocalTime

class FakePreferencesRepository : PreferencesRepository {

    private val backupPrefsFlow = MutableStateFlow(PreferencesRepository.BackupPrefs.DEFAULT)
    private val notifsPrefsFlow = MutableStateFlow(PreferencesRepository.NotifsPrefs.DEFAULT)

    override fun getBackupPrefsFlow(): Flow<PreferencesRepository.BackupPrefs> = backupPrefsFlow

    override fun getNotifsPrefsFlow(): Flow<PreferencesRepository.NotifsPrefs> = notifsPrefsFlow

    override suspend fun setBackupEnabled(enabled: Boolean) {
        backupPrefsFlow.tryEmit(backupPrefsFlow.value.copy(isEnabled = enabled))
    }

    override suspend fun setAutomaticBackups(
        frequency: Int,
        time: LocalTime,
        useMeteredData: Boolean
    ) {
        backupPrefsFlow.tryEmit(
            backupPrefsFlow.value.copy(
                autoBackupFrequency = frequency,
                autoBackupTime = time,
                autoBackupOnCellular = useMeteredData
            )
        )
    }

    override suspend fun setNotifsEnabled(enabled: Boolean) {
        notifsPrefsFlow.tryEmit(notifsPrefsFlow.value.copy(areNotifsEnabled = enabled))
    }

    override suspend fun setDailyReminders(enabled: Boolean, time: LocalTime) {
        notifsPrefsFlow.tryEmit(
            notifsPrefsFlow.value.copy(
                isRemindersEnabled = enabled,
                reminderTime = time
            )
        )
    }

    override suspend fun setMemoriesEnabled(enabled: Boolean) {
        notifsPrefsFlow.tryEmit(notifsPrefsFlow.value.copy(isMemoriesEnabled = enabled))
    }
}