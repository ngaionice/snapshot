package me.ionice.snapshot.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.backup.BackupUtil
import java.time.LocalDateTime

class SettingsViewModel(private val backupUtil: BackupUtil) : ViewModel() {

    private val viewModelState = MutableStateFlow(SettingsViewModelState(loading = true))
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    loading = false,
                    backupEnabled = backupUtil.isBackupEnabled(),
                    signedInGoogleAccountEmail = backupUtil.getLoggedInAccountEmail(),
                    lastBackupTime = backupUtil.getLastBackupTime()
                )
            }
        }
    }

    fun setBackupEnabled(value: Boolean) {
        backupUtil.setBackupEnabled(value)
        viewModelScope.launch {
            viewModelState.update {
                it.copy(backupEnabled = value, lastBackupTime = backupUtil.getLastBackupTime())
            }
        }
    }

    fun loggedInToGoogle(account: GoogleSignInAccount) {
        viewModelState.update { it.copy(signedInGoogleAccountEmail = account.email) }
    }

    suspend fun backupDatabase() {
        val result = backupUtil.backupDatabase()
        if (result.isFailure) {
            result.exceptionOrNull().let {
                if (it != null) {
                    viewModelState.update { state -> state.copy(snackbarMessage = "Backup failed: ${it.message}") }
                }
                it?.printStackTrace()
            }
        } else {
            viewModelState.update { it.copy(lastBackupTime = backupUtil.getLastBackupTime(), snackbarMessage = "Backup successful") }
        }
    }

    suspend fun restoreDatabase() {
        val result = backupUtil.restoreDatabase()
        if (result.isFailure) {
            result.exceptionOrNull().let {
                if (it != null) {
                    viewModelState.update { state -> state.copy(snackbarMessage = "Restore failed: ${it.message}") }
                }
                it?.printStackTrace()
            }
        } else {
            viewModelState.update { it.copy(snackbarMessage = "Restore successful") }
        }
    }

    fun clearErrorMessage() {
        viewModelState.update { it.copy(snackbarMessage = null) }
    }

    companion object {
        fun provideFactory(backupUtil: BackupUtil): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(backupUtil) as T
                }
            }
    }
}

data class SettingsViewModelState(
    val loading: Boolean,
    val backupEnabled: Boolean? = null,
    val signedInGoogleAccountEmail: String? = null,
    val lastBackupTime: LocalDateTime? = null,
    val snackbarMessage: String? = null
) {
    fun toUiState(): SettingsUiState =
        SettingsUiState.TempStateClass(
            loading = loading,
            backupEnabled = backupEnabled == true,
            signedInGoogleAccountEmail = signedInGoogleAccountEmail,
            lastBackupTime = lastBackupTime,
            snackbarMessage = snackbarMessage
        )
}

sealed interface SettingsUiState {

    val loading: Boolean
    val snackbarMessage: String?

    // TODO: need to re-evaluate how to properly represent UI state
    data class TempStateClass(
        override val loading: Boolean,
        override val snackbarMessage: String?,
        val backupEnabled: Boolean,
        val signedInGoogleAccountEmail: String?,
        val lastBackupTime: LocalDateTime?
    ) : SettingsUiState
}