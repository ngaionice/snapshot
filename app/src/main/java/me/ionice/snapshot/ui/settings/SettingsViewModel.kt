package me.ionice.snapshot.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.backup.BackupUtil
import java.time.LocalDateTime
import kotlin.reflect.KClass

class SettingsViewModel(private val backupUtil: BackupUtil) : ViewModel() {

    private val viewModelState = MutableStateFlow(SettingsViewModelState(loading = false))
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    fun <T : SettingsViewModelState.Subsection> switchScreens(targetClass: KClass<T>) {
        viewModelState.update {
            it.copy(loading = true)
        }
        viewModelScope.launch {
            viewModelState.update {
                when (targetClass) {
                    SettingsViewModelState.Subsection.Home::class -> {
                        it.copy(
                            loading = false,
                            subsection = SettingsViewModelState.Subsection.Home
                        )
                    }
                    SettingsViewModelState.Subsection.Backup::class -> {
                        it.copy(loading = false, subsection = initializeBackupState())
                    }
                    SettingsViewModelState.Subsection.Notifications::class -> {
                        it.copy(loading = false, subsection = initializeNotificationsState())
                    }
                    else -> throw IllegalArgumentException("Invalid class passed in.")
                }
            }
        }
    }

    fun setBackupEnabled(value: Boolean) {
        backupUtil.setBackupEnabled(value)
        check(viewModelState.value.subsection is SettingsViewModelState.Subsection.Backup)
        viewModelScope.launch {
            viewModelState.update {
                if (it.subsection is SettingsViewModelState.Subsection.Backup) {
                    it.copy(
                        subsection = it.subsection.copy(
                            backupEnabled = value,
                            lastBackupTime = backupUtil.getLastBackupTime()
                        )
                    )
                } else {
                    it.copy(subsection = initializeBackupState())
                }
            }
        }
    }

    fun loggedInToGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            viewModelState.update {
                if (it.subsection is SettingsViewModelState.Subsection.Backup) {
                    it.copy(
                        subsection = it.subsection.copy(
                            signedInGoogleAccountEmail = account.email,
                            lastBackupTime = backupUtil.getLastBackupTime()
                        )
                    )
                } else {
                    it.copy(subsection = initializeBackupState())
                }
            }
        }
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
            viewModelState.update {
                it.copy(
                    snackbarMessage = "Backup successful",
                    subsection = if (it.subsection is SettingsViewModelState.Subsection.Backup) it.subsection.copy(
                        lastBackupTime = backupUtil.getLastBackupTime()
                    ) else initializeBackupState()
                )
            }
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

    fun clearSnackbarMessage() {
        viewModelState.update { it.copy(snackbarMessage = null) }
    }

    private suspend fun initializeBackupState(): SettingsViewModelState.Subsection.Backup =
        SettingsViewModelState.Subsection.Backup(
            backupEnabled = backupUtil.isBackupEnabled(),
            signedInGoogleAccountEmail = backupUtil.getLoggedInAccountEmail(),
            lastBackupTime = backupUtil.getLastBackupTime()
        )

    private fun initializeNotificationsState(): SettingsViewModelState.Subsection.Notifications =
        SettingsViewModelState.Subsection.Notifications("")

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
    val snackbarMessage: String? = null,
    val subsection: Subsection = Subsection.Home
) {
    fun toUiState(): SettingsUiState =
        when (subsection) {
            is Subsection.Home -> {
                SettingsUiState.Home(
                    loading = loading,
                    snackbarMessage = snackbarMessage
                )
            }
            is Subsection.Backup -> {
                SettingsUiState.Backup(
                    loading = loading,
                    backupEnabled = subsection.backupEnabled,
                    signedInGoogleAccountEmail = subsection.signedInGoogleAccountEmail,
                    lastBackupTime = subsection.lastBackupTime,
                    snackbarMessage = snackbarMessage
                )
            }
            is Subsection.Notifications -> {
                SettingsUiState.Notifications(
                    loading = loading,
                    snackbarMessage = snackbarMessage
                )
            }
        }

    sealed interface Subsection {

        object Home : Subsection

        data class Backup(
            val backupEnabled: Boolean,
            val signedInGoogleAccountEmail: String?,
            val lastBackupTime: LocalDateTime?
        ) : Subsection

        data class Notifications(
            val placeholder: Any
        ) : Subsection
    }
}


sealed interface SettingsUiState {

    val loading: Boolean
    val snackbarMessage: String?

    data class Home(
        override val loading: Boolean,
        override val snackbarMessage: String?
    ) : SettingsUiState

    data class Backup(
        override val loading: Boolean,
        override val snackbarMessage: String?,
        val backupEnabled: Boolean,
        val signedInGoogleAccountEmail: String?,
        val lastBackupTime: LocalDateTime?
    ) : SettingsUiState

    data class Notifications(
        override val loading: Boolean,
        override val snackbarMessage: String?
    ) : SettingsUiState
}