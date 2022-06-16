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

    private val viewModelState = MutableStateFlow(SettingsViewModelState(loading = false))
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    private val backupPreferencesState = preferencesRepository.backupPreferencesFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PreferencesRepository.BackupPreferences.DEFAULT
    )

    private val notificationsPreferencesState =
        preferencesRepository.notificationsPreferencesFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            PreferencesRepository.NotificationsPreferences.DEFAULT
        )

    init {
        viewModelScope.launch {
            networkRepository.backupStatus.collect {
                val snackbarMessage = if (it.isInProgress || it.action == null) {
                    null
                } else {
                    "${it.action} ${if (it.isSuccess == true) "successful" else "failed"}"
                }

                if (viewModelState.value.subsection is SettingsViewModelState.Subsection.Backup) {
                    val subsection =
                        viewModelState.value.subsection as SettingsViewModelState.Subsection.Backup
                    viewModelState.value = viewModelState.value.copy(
                        subsection = subsection.copy(
                            isBackupInProgress = it.isInProgress,
                            lastBackupTime = if (!it.isInProgress) networkRepository.getLastBackupTime() else subsection.lastBackupTime
                        ),
                        snackbarMessage = snackbarMessage
                    )
                }
            }
        }
    }

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

    fun setBackupEnabled(enable: Boolean) {
        check(viewModelState.value.subsection is SettingsViewModelState.Subsection.Backup)
        viewModelScope.launch {
            preferencesRepository.setIsBackupEnabled(enable)
            viewModelState.update {
                if (it.subsection is SettingsViewModelState.Subsection.Backup) {
                    it.copy(
                        subsection = it.subsection.copy(
                            backupEnabled = enable,
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
                if (it.subsection is SettingsViewModelState.Subsection.Notifications) {
                    it.copy(
                        subsection = it.subsection.copy(
                            isRemindersEnabled = enable,
                            reminderTime = notificationsPreferencesState.value.reminderTime
                        )
                    )
                } else {
                    it.copy(subsection = initNotificationsState())
                }
            }
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
                backupEnabled = backupPreferencesState.value.isEnabled,
                signedInGoogleAccountEmail = networkRepository.getLoggedInAccountEmail(),
                lastBackupTime = networkRepository.getLastBackupTime(),
                isBackupInProgress = networkRepository.backupStatus.value.isInProgress
            )
        }

    private fun initNotificationsState(): SettingsViewModelState.Subsection.Notifications =
        SettingsViewModelState.Subsection.Notifications(
            isRemindersEnabled = notificationsPreferencesState.value.isRemindersEnabled,
            reminderTime = notificationsPreferencesState.value.reminderTime
        )

    private fun initThemingState(): SettingsViewModelState.Subsection.Theming =
        SettingsViewModelState.Subsection.Theming("")

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
                    snackbarMessage = snackbarMessage,
                    isBackupInProgress = subsection.isBackupInProgress
                )
            }
            is Subsection.Notifications -> {
                SettingsUiState.Notifications(
                    loading = loading,
                    snackbarMessage = snackbarMessage,
                    isRemindersEnabled = subsection.isRemindersEnabled,
                    reminderTime = subsection.reminderTime
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
            val lastBackupTime: LocalDateTime? = null,
            val autoBackupFrequency: Int = 0,
            val autoBackupTime: LocalTime = LocalTime.MIDNIGHT,
            val isBackupInProgress: Boolean = true
        ) : Subsection

        data class Notifications(
            val isRemindersEnabled: Boolean,
            val reminderTime: LocalTime
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
        val lastBackupTime: LocalDateTime?,
        val isBackupInProgress: Boolean
    ) : SettingsUiState

    data class Notifications(
        override val loading: Boolean,
        override val snackbarMessage: String?,
        val isRemindersEnabled: Boolean,
        val reminderTime: LocalTime
    ) : SettingsUiState

    data class Theming(
        override val loading: Boolean,
        override val snackbarMessage: String?
    ) : SettingsUiState
}