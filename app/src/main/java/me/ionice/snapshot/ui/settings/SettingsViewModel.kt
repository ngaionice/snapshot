package me.ionice.snapshot.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.network.NetworkRepository
import java.time.LocalDateTime

class SettingsViewModel(private val networkRepository: NetworkRepository) : ViewModel() {

    private val viewModelState = MutableStateFlow(SettingsViewModelState(loading = false))
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    fun switchScreens(targetScreen: SettingsScreenSection) {
        viewModelState.update {
            it.copy(loading = true)
        }
        viewModelScope.launch {
            viewModelState.update {
                when (targetScreen) {
                    SettingsScreenSection.Home -> {
                        it.copy(
                            loading = false,
                            subsection = SettingsViewModelState.Subsection.Home
                        )
                    }
                    SettingsScreenSection.Backup -> {
                        it.copy(loading = false, subsection = initBackupState())
                    }
                    SettingsScreenSection.Notifications -> {
                        it.copy(loading = false, subsection = initNotificationsState())
                    }
                    SettingsScreenSection.Theming -> {
                        it.copy(loading = false, subsection = initThemingState())
                    }
                }
            }
        }
    }

    fun setBackupEnabled(value: Boolean) {
        networkRepository.setBackupEnabled(value)
        check(viewModelState.value.subsection is SettingsViewModelState.Subsection.Backup)
        viewModelScope.launch {
            viewModelState.update {
                if (it.subsection is SettingsViewModelState.Subsection.Backup) {
                    it.copy(
                        subsection = it.subsection.copy(
                            backupEnabled = value,
                            lastBackupTime = networkRepository.getLastBackupTime()
                        )
                    )
                } else {
                    it.copy(subsection = initBackupState())
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
                            lastBackupTime = networkRepository.getLastBackupTime()
                        )
                    )
                } else {
                    it.copy(subsection = initBackupState())
                }
            }
        }
    }

    suspend fun backupDatabase() {
        val result = networkRepository.backupDatabase()
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
                        lastBackupTime = networkRepository.getLastBackupTime()
                    ) else initBackupState()
                )
            }
        }
    }

    suspend fun restoreDatabase() {
        val result = networkRepository.restoreDatabase()
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

    private suspend fun initBackupState(): SettingsViewModelState.Subsection.Backup =
        if (!networkRepository.isOnline()) {
            SettingsViewModelState.Subsection.Backup(dataAvailable = false, backupEnabled = false)
        } else {
            SettingsViewModelState.Subsection.Backup(
                dataAvailable = true,
                backupEnabled = networkRepository.isBackupEnabled(),
                signedInGoogleAccountEmail = networkRepository.getLoggedInAccountEmail(),
                lastBackupTime = networkRepository.getLastBackupTime()
            )
        }


    private fun initNotificationsState(): SettingsViewModelState.Subsection.Notifications =
        SettingsViewModelState.Subsection.Notifications("")

    private fun initThemingState(): SettingsViewModelState.Subsection.Theming =
        SettingsViewModelState.Subsection.Theming("")

    companion object {
        fun provideFactory(networkRepository: NetworkRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(networkRepository) as T
                }
            }
    }
}

enum class SettingsScreenSection {
    Home,
    Backup,
    Notifications,
    Theming
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
                    dataAvailable = subsection.dataAvailable,
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
            is Subsection.Theming -> {
                SettingsUiState.Theming(
                    loading = loading,
                    snackbarMessage = snackbarMessage
                )
            }
        }

    sealed interface Subsection {

        object Home : Subsection

        data class Backup(
            val dataAvailable: Boolean,
            val backupEnabled: Boolean,
            val signedInGoogleAccountEmail: String? = null,
            val lastBackupTime: LocalDateTime? = null
        ) : Subsection

        data class Notifications(
            val placeholder: Any
        ) : Subsection

        data class Theming(
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
        val dataAvailable: Boolean,
        val backupEnabled: Boolean,
        val signedInGoogleAccountEmail: String?,
        val lastBackupTime: LocalDateTime?
    ) : SettingsUiState

    data class Notifications(
        override val loading: Boolean,
        override val snackbarMessage: String?
    ) : SettingsUiState

    data class Theming(
        override val loading: Boolean,
        override val snackbarMessage: String?
    ) : SettingsUiState
}