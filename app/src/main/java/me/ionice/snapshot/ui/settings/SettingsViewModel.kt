package me.ionice.snapshot.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.network.NetworkRepository
import me.ionice.snapshot.data.preferences.PreferencesRepository
import java.time.LocalDateTime
import java.time.LocalTime

class SettingsViewModel(
    private val networkRepository: NetworkRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val backupPreferencesState = preferencesRepository.getBackupPreferencesFlow().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PreferencesRepository.BackupPreferences.DEFAULT
    )

    private val notificationsPreferencesState =
        preferencesRepository.getNotificationsPreferencesFlow().stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            PreferencesRepository.NotificationsPreferences.DEFAULT
        )

    private val viewModelState = MutableStateFlow(
        SettingsViewModelState(
            loading = false,
            backupPreferences = SettingsViewModelState.Backup(
                backupEnabled = backupPreferencesState.value.isEnabled,
                dataAvailable = false
            ),
            notificationsPreferences = SettingsViewModelState.Notifications(
                isRemindersEnabled = notificationsPreferencesState.value.isRemindersEnabled,
                reminderTime = notificationsPreferencesState.value.reminderTime
            ),
            themingPreferences = SettingsViewModelState.Theming
        )
    )
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        viewModelScope.launch {
            observeBackupStatus()
        }
        viewModelScope.launch {
            observeBackupPreferences()
        }
        viewModelScope.launch {
            loadBackupPreferences()
        }
    }

    /**
     * A call to this function never completes normally, as it calls Flow.collect internally.
     */
    private suspend fun observeBackupStatus() {
        networkRepository.getBackupStatusFlow().collect {
            val snackbarMessage = if (it.isInProgress || it.action == null) {
                null
            } else {
                "${it.action} ${if (it.isSuccess == true) "successful" else "failed"}"
            }

            viewModelState.update { state ->
                state.copy(
                    backupPreferences = state.backupPreferences.copy(
                        isBackupInProgress = it.isInProgress,
                        lastBackupTime = if (!it.isInProgress) networkRepository.getLastBackupTime() else state.backupPreferences.lastBackupTime
                    ),
                    snackbarMessage = snackbarMessage
                )
            }
        }
    }

    private suspend fun observeBackupPreferences() {
        backupPreferencesState.collect { bState ->
            viewModelState.update {
                it.copy(
                    backupPreferences = it.backupPreferences.copy(
                        backupEnabled = bState.isEnabled,
                        autoBackupTime = bState.autoBackupTime,
                        autoBackupFrequency = bState.autoBackupFrequency
                    )
                )
            }
        }
    }

    fun setBackupEnabled(enable: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setIsBackupEnabled(enable)
        }
    }

    fun loggedInToGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    backupPreferences = it.backupPreferences.copy(
                        signedInGoogleAccountEmail = account.email,
                        lastBackupTime = networkRepository.getLastBackupTime()
                    )
                )
            }
        }
    }

    fun backupDatabase() {
        networkRepository.startDatabaseBackup()
    }

    fun restoreDatabase() {
        networkRepository.startDatabaseRestore()
    }

    fun setRemindersEnabled(enable: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setIsDailyReminderEnabled(enable)
            viewModelState.update {
                it.copy(
                    notificationsPreferences = it.notificationsPreferences.copy(
                        isRemindersEnabled = enable,
                        reminderTime = notificationsPreferencesState.value.reminderTime
                    )
                )
            }
        }
    }

    // these should be ok to use with viewModelScope because these are really short writes,
    // and not a big deal if they fail
    fun setBackupFrequency(freq: Int) {
        viewModelScope.launch {
            preferencesRepository.setBackupFrequency(freq)
        }
    }

    fun setBackupTime(time: LocalTime) {
        viewModelScope.launch {
            preferencesRepository.setBackupTime(time)
        }
    }

    fun clearSnackbarMessage() {
        viewModelState.update { it.copy(snackbarMessage = null) }
    }

    private suspend fun loadBackupPreferences() {
        println("Loading backup prefs")
        viewModelState.update {
            if (!networkRepository.isOnline()) {
                it.copy(backupPreferences = SettingsViewModelState.Backup(dataAvailable = false, backupEnabled = false))
            } else {
                it.copy(backupPreferences = SettingsViewModelState.Backup(
                    dataAvailable = true,
                    backupEnabled = backupPreferencesState.value.isEnabled,
                    signedInGoogleAccountEmail = networkRepository.getLoggedInAccountEmail(),
                    lastBackupTime = networkRepository.getLastBackupTime(),
                    isBackupInProgress = networkRepository.getBackupStatus().isInProgress,
                    autoBackupFrequency = backupPreferencesState.value.autoBackupFrequency,
                    autoBackupTime = backupPreferencesState.value.autoBackupTime
                ))
            }
        }
    }

    companion object {
        fun provideFactory(
            networkRepository: NetworkRepository,
            preferencesRepository: PreferencesRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(networkRepository, preferencesRepository) as T
                }
            }
    }
}

data class SettingsViewModelState(
    val loading: Boolean,
    val backupPreferences: Backup,
    val notificationsPreferences: Notifications,
    val themingPreferences: Theming,
    val snackbarMessage: String? = null
) {
    fun toUiState(): SettingsUiState {
        if (loading) return SettingsUiState.Loading

        return SettingsUiState.Loaded(
            backupPreferences = toBackupPreferencesUiState(),
            notificationsPreferences = toNotificationsPreferencesUiState(),
            themingPreferences = toThemingPreferencesUiState(),
            snackbarMessage = snackbarMessage
        )
    }

    private fun toBackupPreferencesUiState(): SettingsUiState.Loaded.Backup =
        if (backupPreferences.dataAvailable) {
            SettingsUiState.Loaded.Backup.Available(
                backupEnabled = backupPreferences.backupEnabled,
                signedInGoogleAccountEmail = backupPreferences.signedInGoogleAccountEmail,
                lastBackupTime = backupPreferences.lastBackupTime,
                autoBackupFrequency = backupPreferences.autoBackupFrequency,
                autoBackupTime = backupPreferences.autoBackupTime,
                isBackupInProgress = backupPreferences.isBackupInProgress,
            )
        } else {
            SettingsUiState.Loaded.Backup.NotAvailable
        }

    private fun toNotificationsPreferencesUiState(): SettingsUiState.Loaded.Notifications =
        SettingsUiState.Loaded.Notifications(
            isRemindersEnabled = notificationsPreferences.isRemindersEnabled,
            reminderTime = notificationsPreferences.reminderTime
        )

    private fun toThemingPreferencesUiState(): SettingsUiState.Loaded.Theming =
        SettingsUiState.Loaded.Theming

    data class Backup(
        val backupEnabled: Boolean,
        val dataAvailable: Boolean,
        val signedInGoogleAccountEmail: String? = null,
        val lastBackupTime: LocalDateTime? = null,
        val autoBackupFrequency: Int = 0,
        val autoBackupTime: LocalTime = LocalTime.MIDNIGHT,
        val isBackupInProgress: Boolean = true,
    )

    data class Notifications(
        val isRemindersEnabled: Boolean,
        val reminderTime: LocalTime
    )

    object Theming
}

sealed interface SettingsUiState {

    object Loading : SettingsUiState

    data class Loaded(
        val backupPreferences: Backup,
        val notificationsPreferences: Notifications,
        val themingPreferences: Theming,
        val snackbarMessage: String?
    ) : SettingsUiState {
        sealed interface Backup {
            data class Available(
                val backupEnabled: Boolean,
                val signedInGoogleAccountEmail: String?,
                val lastBackupTime: LocalDateTime?,
                val isBackupInProgress: Boolean,
                val autoBackupFrequency: Int,
                val autoBackupTime: LocalTime,
            ) : Backup

            object NotAvailable : Backup
        }

        data class Notifications(
            val isRemindersEnabled: Boolean,
            val reminderTime: LocalTime
        )

        object Theming
    }
}