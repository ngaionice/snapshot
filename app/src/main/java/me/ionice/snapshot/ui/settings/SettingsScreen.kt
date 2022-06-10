package me.ionice.snapshot.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.BackButton
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen
import me.ionice.snapshot.ui.settings.screens.BackupScreen
import me.ionice.snapshot.ui.settings.screens.HomeScreen
import me.ionice.snapshot.ui.settings.screens.NotificationsScreen
import me.ionice.snapshot.ui.settings.screens.ThemingScreen

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
                        onThemingClick = { viewModel.switchScreens(SettingsScreenSection.Theming) })
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
                    NotificationsScreen(
                        uiState = currState,
                        onEnableReminders = { },
                        onReminderTimeChange = {},
                        onEnableMemories = {})

                    BackHandler(onBack = onBack)
                }
            }
            is SettingsUiState.Theming -> {
                BaseScreen(
                    headerText = stringResource(R.string.settings_screen_theming_header),
                    snackbarHostState = snackbarHostState,
                    navigationIcon = { BackButton(onBack = onBack) }) {
                    ThemingScreen()
                }

                BackHandler(onBack = onBack)
            }
        }
    }
}
