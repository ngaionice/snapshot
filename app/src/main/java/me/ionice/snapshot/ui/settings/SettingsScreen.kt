package me.ionice.snapshot.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.ionice.snapshot.ui.common.BackButton
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, toggleBottomNav: (Boolean) -> Unit) {

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val onBack = { viewModel.switchScreens(SettingsViewModelState.Subsection.Home::class) }

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
        val currState = uiState
        when (currState) {
            is SettingsUiState.Home -> {
                BaseScreen(headerText = "Settings") {
                    HomeScreen(
                        onBackupClick = { viewModel.switchScreens(SettingsViewModelState.Subsection.Backup::class) },
                        onNotificationsClick = { viewModel.switchScreens(SettingsViewModelState.Subsection.Notifications::class) })
                }
            }
            is SettingsUiState.Backup -> {
                BaseScreen(
                    headerText = "Backup & Restore",
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
                    headerText = "Notifications",
                    snackbarHostState = snackbarHostState,
                    navigationIcon = { BackButton(onBack = onBack) }) {
                    NotificationsScreen()
                }

                BackHandler(onBack = onBack)
            }
        }
    }


}

@Composable
fun HomeScreen(onBackupClick: () -> Unit, onNotificationsClick: () -> Unit) {
    Column {
        SettingsList(onBackupClick = onBackupClick, onNotificationsClick = onNotificationsClick)
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

    Column {
        ProminentSwitchSetting(
            mainLabel = "Use cloud backups",
            checked = uiState.backupEnabled,
            onCheckedChange = { onEnableBackup(!uiState.backupEnabled) }
        )

        AnimatedVisibility(visible = uiState.backupEnabled, enter = fadeIn(), exit = fadeOut()) {
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

@Composable
fun NotificationsScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Functionality not yet implemented!",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}