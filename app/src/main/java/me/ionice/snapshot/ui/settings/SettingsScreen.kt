package me.ionice.snapshot.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, toggleBottomNav: (Boolean) -> Unit) {

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val onBack = { viewModel.switchScreens(SettingsScreenSection.Home) }

    LaunchedEffect(key1 = uiState.snackbarMessage) {
        if (uiState.snackbarMessage != null) {
            snackbarHostState.showSnackbar(
                uiState.snackbarMessage!!,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbarMessage()
        }
    }

    if (uiState.loading) {
        LoadingScreen()
    } else {
        // must keep: without it, the compiler is not able to cast the variable properly
        when (val currState = uiState) {
            is SettingsUiState.Home -> {
                BaseScreen(headerText = stringResource(R.string.settings_screen_header)) {
                    HomeScreen(
                        onBackupClick = { viewModel.switchScreens(SettingsScreenSection.Backup) },
                        onNotificationsClick = { viewModel.switchScreens(SettingsScreenSection.Notifications) },
                        onThemingClick = {viewModel.switchScreens(SettingsScreenSection.Theming)})
                }
            }
            is SettingsUiState.Backup -> {
                BaseScreen(
                    headerText = stringResource(R.string.settings_screen_backup_header),
                    snackbarHostState = snackbarHostState,
                    navigationIcon = { BackButton(onBack = onBack) })
                {
                    BackupScreen(
                        currState,
                        scope = scope,
                        onEnableBackup = { viewModel.setBackupEnabled(it) },
                        onSuccessfulLogin = { viewModel.loggedInToGoogle(it) },
                        onStartBackup = { viewModel.backupDatabase() },
                        onStartRestore = { viewModel.restoreDatabase() },
                        toggleBottomNav = toggleBottomNav
                    )

                    BackHandler(onBack = onBack)
                }
            }
            is SettingsUiState.Notifications -> {
                BaseScreen(
                    headerText = stringResource(R.string.settings_screen_notifs_header),
                    snackbarHostState = snackbarHostState,
                    navigationIcon = { BackButton(onBack = onBack) }) {
                    NotificationsScreen()
                }

                BackHandler(onBack = onBack)
            }
            is SettingsUiState.Theming -> {
                BaseScreen(
                    headerText = stringResource(R.string.settings_screen_theming_header),
                    snackbarHostState = snackbarHostState,
                    navigationIcon = { BackButton(onBack = onBack) }) {
                    ThemingScreen()
                }
            }
        }
    }


}

@Composable
fun HomeScreen(onBackupClick: () -> Unit, onNotificationsClick: () -> Unit, onThemingClick: () -> Unit) {
    Column {
        SettingsList(onBackupClick = onBackupClick, onNotificationsClick = onNotificationsClick, onThemingClick = onThemingClick)
    }
}


@Composable
fun BackupScreen(
    uiState: SettingsUiState.Backup,
    scope: CoroutineScope,
    onEnableBackup: (Boolean) -> Unit,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit,
    onStartBackup: suspend () -> Unit,
    onStartRestore: suspend () -> Unit,
    toggleBottomNav: (Boolean) -> Unit
) {

    var isBackupInProgress by remember {
        mutableStateOf(false)
    }

    if (!uiState.dataAvailable) {
        FunctionalityNotAvailableScreen(reason = stringResource(R.string.settings_screen_backup_na_reason))
    } else {
        Column {
            ProminentSwitchSetting(
                mainLabel = stringResource(R.string.settings_screen_backup_main_switch),
                checked = uiState.backupEnabled,
                onCheckedChange = { onEnableBackup(!uiState.backupEnabled) }
            )

            AnimatedVisibility(
                visible = uiState.backupEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    BackupScreenOptions(
                        accountEmail = uiState.signedInGoogleAccountEmail,
                        isBackupInProgress = isBackupInProgress,
                        onSuccessfulLogin = onSuccessfulLogin,
                        onStartBackup = {
                            isBackupInProgress = true
                            toggleBottomNav(false)
                            scope.launch {
                                onStartBackup()
                                isBackupInProgress = false
                                toggleBottomNav(true)
                            }
                        },
                        onStartRestore = {
                            isBackupInProgress = true
                            toggleBottomNav(false)
                            scope.launch {
                                onStartRestore()
                                isBackupInProgress = false
                                toggleBottomNav(true)
                            }
                        },
                        lastBackupTime = uiState.lastBackupTime
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen() {
    FunctionalityNotYetAvailableScreen()
}

@Composable
fun ThemingScreen() {
    FunctionalityNotYetAvailableScreen()
}