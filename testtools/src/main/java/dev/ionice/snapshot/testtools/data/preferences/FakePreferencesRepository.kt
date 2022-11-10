package dev.ionice.snapshot.testtools.data.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import dev.ionice.snapshot.core.data.repository.PreferencesRepository
import java.time.LocalTime

class FakePreferencesRepository : PreferencesRepository {

    private val backupPrefsFlow = MutableStateFlow(PreferencesRepository.BackupPrefs.DEFAULT)
    private val notifsPrefsFlow = MutableStateFlow(PreferencesRepository.NotifsPrefs.DEFAULT)
    private val searchHistoryFlow = MutableStateFlow(PreferencesRepository.SearchHistory(emptyList()))

    override fun getBackupPrefsFlow(): Flow<PreferencesRepository.BackupPrefs> = backupPrefsFlow

    override fun getNotifsPrefsFlow(): Flow<PreferencesRepository.NotifsPrefs> = notifsPrefsFlow
    override fun getRecentSearchesFlow(): Flow<PreferencesRepository.SearchHistory> = searchHistoryFlow

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

    override suspend fun insertRecentSearch(searchString: String) {
        val existing = searchHistoryFlow.value.searches
        searchHistoryFlow.tryEmit(searchHistoryFlow.value.copy(searches = existing.filter { !it.equals(searchString, ignoreCase = true) }.take(4)  + searchString))
    }

    override suspend fun clearRecentSearches() {
        searchHistoryFlow.tryEmit(PreferencesRepository.SearchHistory(emptyList()))
    }
}