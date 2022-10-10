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
import me.ionice.snapshot.utils.Result
import me.ionice.snapshot.utils.asResult
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val snackbarManager: SnackbarManager = SnackbarManager

    private val backupState = MutableStateFlow<BackupInfoState>(BackupInfoState.Loading)

    val uiState: StateFlow<SettingsUiState> = combine(
        backupState,
        preferencesRepository.getBackupPrefsFlow().asResult(),
        preferencesRepository.getNotifsPrefsFlow().asResult()
    ) { backupStateResult, backupPrefsResult, notifsPrefsResult ->
        val backupState = when (backupPrefsResult) {
            is Result.Loading -> BackupUiState.Loading
            is Result.Error -> BackupUiState.Error
            is Result.Success -> {
                if (backupStateResult is BackupInfoState.Success) {
                    val (isEnabled, autoBackupFrequency, autoBackupTime, autoBackupOnCellular) =
                        backupPrefsResult.data
                    BackupUiState.Success(
                        isEnabled = isEnabled,
                        signedInGoogleAccountEmail = backupStateResult.signedInGoogleAccountEmail,
                        lastBackupTime = backupStateResult.lastBackupTime,
                        isBackupInProgress = backupStateResult.isBackupInProgress,
                        autoBackupFrequency = autoBackupFrequency,
                        autoBackupTime = autoBackupTime,
                        autoBackupOnCellular = autoBackupOnCellular
                    )
                } else {
                    BackupUiState.Loading
                }
            }
        }
        val notifsState = when (notifsPrefsResult) {
            is Result.Loading -> NotifsUiState.Loading
            is Result.Error -> NotifsUiState.Error
            is Result.Success -> {
                val (areNotifsEnabled, isRemindersEnabled, reminderTime) = notifsPrefsResult.data
                NotifsUiState.Success(
                    areNotifsEnabled = areNotifsEnabled,
                    isRemindersEnabled = isRemindersEnabled,
                    reminderTime = reminderTime
                )
            }
        }
        SettingsUiState(backupUiState = backupState, notifsUiState = notifsState)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(
            backupUiState = BackupUiState.Loading, notifsUiState = NotifsUiState.Loading
        )
    )

    init {
        viewModelScope.launch {
            loadAndObserveBackupStatus()
        }
    }

    /**
     * A call to this function never completes normally, as it calls Flow.collect internally.
     */
    private suspend fun loadAndObserveBackupStatus() {

        val currEmail = backupRepository.getLoggedInAccountEmail()
        backupState.update {
            BackupInfoState.Success(
                signedInGoogleAccountEmail = currEmail,
                isBackupInProgress = false,
                lastBackupTime = if (currEmail != null) backupRepository.getLastBackupTime() else null
            )
        }

        backupRepository.getBackupStatusFlow().collect { actionState ->
            if (!actionState.isInProgress && !actionState.action.isNullOrBlank()) {
                if (actionState.action == "Backup") {
                    snackbarManager.showMessage(if (actionState.isSuccess) R.string.snackbar_backup_success else R.string.snackbar_backup_failure)
                } else {
                    snackbarManager.showMessage(if (actionState.isSuccess) R.string.snackbar_restore_success else R.string.snackbar_restore_failure)
                }
            }

            backupState.update {
                if (it is BackupInfoState.Success) {
                    it.copy(
                        isBackupInProgress = actionState.isInProgress,
                        lastBackupTime = if (!actionState.isInProgress) {
                            backupRepository.getLastBackupTime()
                        } else it.lastBackupTime
                    )
                } else it
            }
        }
    }

    fun loggedInToGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            backupState.update {
                if (it is BackupInfoState.Success) {
                    it.copy(
                        signedInGoogleAccountEmail = account.email,
                        lastBackupTime = backupRepository.getLastBackupTime()
                    )
                } else it

            }
        }
    }

    fun setBackupEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setBackupEnabled(enabled)
        }
    }

    fun setAutoBackups(frequency: Int, time: LocalTime, useMeteredData: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAutomaticBackups(
                frequency = frequency,
                time = time,
                useMeteredData = useMeteredData
            )
        }
    }

    fun backupDatabase() {
        backupRepository.startDatabaseBackup()
    }

    fun restoreDatabase() {
        backupRepository.startDatabaseRestore()
    }

    fun setNotifsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotifsEnabled(enabled)
        }
    }

    fun setDailyReminders(enabled: Boolean, time: LocalTime) {
        viewModelScope.launch {
            preferencesRepository.setDailyReminders(enabled = enabled, time = time)
        }
    }
}

private sealed interface BackupInfoState {

    object Loading : BackupInfoState
    data class Success(
        val signedInGoogleAccountEmail: String?,
        val lastBackupTime: LocalDateTime?,
        val isBackupInProgress: Boolean
    ) : BackupInfoState
}

sealed interface BackupUiState {
    object Loading : BackupUiState
    object Error : BackupUiState
    data class Success(
        val isEnabled: Boolean,
        val signedInGoogleAccountEmail: String?,
        val lastBackupTime: LocalDateTime?,
        val isBackupInProgress: Boolean,
        val autoBackupFrequency: Int,
        val autoBackupTime: LocalTime,
        val autoBackupOnCellular: Boolean
    ) : BackupUiState
}

sealed interface NotifsUiState {
    object Loading : NotifsUiState
    object Error : NotifsUiState
    data class Success(
        val areNotifsEnabled: Boolean = true,
        val isRemindersEnabled: Boolean,
        val reminderTime: LocalTime
    ) : NotifsUiState
}

data class SettingsUiState(
    val backupUiState: BackupUiState, val notifsUiState: NotifsUiState
)
