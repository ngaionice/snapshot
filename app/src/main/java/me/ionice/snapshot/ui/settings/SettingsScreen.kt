package me.ionice.snapshot.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(headerText = "Settings") {
        if (uiState.loading) {
            LoadingScreen()
        } else {
            Column {
                MainScreen(
                    uiState as SettingsUiState.TempStateClass,
                    onEnableBackup = { viewModel.setBackupEnabled(it) },
                    onSuccessfulLogin = { viewModel.loggedInToGoogle(it) },
                    onStartBackup = { viewModel.backup() })
            }
        }
    }
}


@Composable
fun MainScreen(
    uiState: SettingsUiState.TempStateClass,
    onEnableBackup: (Boolean) -> Unit,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit,
    onStartBackup: () -> Unit
) {
    BackupSection(
        isEnabled = uiState.backupEnabled,
        accountEmail = uiState.signedInGoogleAccountEmail,
        setIsEnabled = { onEnableBackup(!uiState.backupEnabled) },
        onSuccessfulLogin = onSuccessfulLogin,
        onStartBackup = onStartBackup
    )
}