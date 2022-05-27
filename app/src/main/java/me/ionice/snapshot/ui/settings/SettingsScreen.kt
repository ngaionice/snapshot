package me.ionice.snapshot.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, toggleBottomNav: (Boolean) -> Unit) {

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    BaseScreen(headerText = "Settings", snackbarHostState = snackbarHostState) {
        if (uiState.loading) {
            LoadingScreen()
        } else {
            Column {
                BackupScreen(
                    uiState as SettingsUiState.TempStateClass,
                    scope = scope,
                    onEnableBackup = { viewModel.setBackupEnabled(it) },
                    onSuccessfulLogin = { viewModel.loggedInToGoogle(it) },
                    onStartBackup = { viewModel.backupDatabase() },
                    onStartRestore = { viewModel.restoreDatabase() },
                    toggleBottomNav = toggleBottomNav
                )
            }
            LaunchedEffect(key1 = uiState.snackbarMessage) {
                if (uiState.snackbarMessage != null) {
                    snackbarHostState.showSnackbar(uiState.snackbarMessage!!, duration = SnackbarDuration.Short)
                    viewModel.clearErrorMessage()
                }
            }
        }
    }
}

@Composable
fun SettingsListScreen() {

}


@Composable
fun BackupScreen(
    uiState: SettingsUiState.TempStateClass,
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