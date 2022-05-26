package me.ionice.snapshot.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    BaseScreen(headerText = "Settings") {
        if (uiState.loading) {
            LoadingScreen()
        } else {
            Column {
                MainScreen(
                    uiState as SettingsUiState.TempStateClass,
                    onEnableBackup = { viewModel.setBackupEnabled(it) },
                    onSuccessfulLogin = { viewModel.loggedInToGoogle(it) },
                    onStartBackup = { scope.launch { viewModel.backupDatabase() } },
                    onStartRestore = { scope.launch { viewModel.restoreDatabase() } })
            }
        }
    }
}


@Composable
fun MainScreen(
    uiState: SettingsUiState.TempStateClass,
    onEnableBackup: (Boolean) -> Unit,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit
) {
    BackupSection(
        isEnabled = uiState.backupEnabled,
        accountEmail = uiState.signedInGoogleAccountEmail,
        setIsEnabled = { onEnableBackup(!uiState.backupEnabled) },
        onSuccessfulLogin = onSuccessfulLogin,
        onStartBackup = onStartBackup,
        onStartRestore = onStartRestore,
        lastBackupTime = uiState.lastBackupTime
    )
}