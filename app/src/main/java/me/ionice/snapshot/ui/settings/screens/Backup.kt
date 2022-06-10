package me.ionice.snapshot.ui.settings.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.settings.BackupScreenOptions
import me.ionice.snapshot.ui.settings.ProminentSwitchSetting
import me.ionice.snapshot.ui.settings.SettingsUiState

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