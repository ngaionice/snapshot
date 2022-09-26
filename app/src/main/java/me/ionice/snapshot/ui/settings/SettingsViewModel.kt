package me.ionice.snapshot.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.R
import me.ionice.snapshot.data.backup.BackupRepository
import me.ionice.snapshot.data.preferences.PreferencesRepository
import me.ionice.snapshot.ui.snackbar.SnackbarManager
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val snackbarManager: SnackbarManager = SnackbarManager

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
        backupRepository.getBackupStatusFlow().collect { actionState ->
            if (!actionState.isInProgress && !actionState.action.isNullOrBlank()) {
                if (actionState.action == "Backup") {
                    snackbarManager.showMessage(if (actionState.isSuccess) R.string.snackbar_backup_success else R.string.snackbar_backup_failure)
                } else {
                    snackbarManager.showMessage(if (actionState.isSuccess) R.string.snackbar_restore_success else R.string.snackbar_restore_failure)
                }
            }

            viewModelState.update {
                it.copy(
                    backupPreferences = it.backupPreferences.copy(
                        isBackupInProgress = actionState.isInProgress,
                        lastBackupTime = if (!actionState.isInProgress) backupRepository.getLastBackupTime() else it.backupPreferences.lastBackupTime
                    )
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
                        lastBackupTime = backupRepository.getLastBackupTime()
                    )
                )
            }
        }
    }

    fun backupDatabase() {
        backupRepository.startDatabaseBackup()
    }

    fun restoreDatabase() {
        backupRepository.startDatabaseRestore()
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

    private suspend fun loadBackupPreferences() {
        viewModelState.update {
            if (!backupRepository.isOnline()) {
                it.copy(
                    backupPreferences = SettingsViewModelState.Backup(
                        dataAvailable = false,
                        backupEnabled = false
                    )
                )
            } else {
                it.copy(
                    backupPreferences = SettingsViewModelState.Backup(
                        dataAvailable = true,
                        backupEnabled = backupPreferencesState.value.isEnabled,
                        signedInGoogleAccountEmail = backupRepository.getLoggedInAccountEmail(),
                        lastBackupTime = backupRepository.getLastBackupTime(),
                        isBackupInProgress = backupRepository.getBackupStatus().isInProgress,
                        autoBackupFrequency = backupPreferencesState.value.autoBackupFrequency,
                        autoBackupTime = backupPreferencesState.value.autoBackupTime
                    )
                )
            }
        }
    }
}

data class SettingsViewModelState(
    val loading: Boolean,
    val backupPreferences: Backup,
    val notificationsPreferences: Notifications,
    val themingPreferences: Theming
) {
    fun toUiState(): SettingsUiState {
        if (loading) return SettingsUiState.Loading

        return SettingsUiState.Loaded(
            backupPreferences = toBackupPreferencesUiState(),
            notificationsPreferences = toNotificationsPreferencesUiState(),
            themingPreferences = toThemingPreferencesUiState()
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
        val themingPreferences: Theming
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