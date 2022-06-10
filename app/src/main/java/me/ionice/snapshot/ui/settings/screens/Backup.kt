package me.ionice.snapshot.ui.settings.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.ConfirmationDialog
import me.ionice.snapshot.ui.common.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.settings.*
import me.ionice.snapshot.utils.Utils

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

    var showBackupConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var showRestoreConfirmDialog by rememberSaveable { mutableStateOf(false) }

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
                    if (uiState.signedInGoogleAccountEmail != null) {
                        // Show email or something
                        SettingsRow(
                            mainLabel = stringResource(R.string.settings_screen_backup_selected_account),
                            secondaryLabel = uiState.signedInGoogleAccountEmail
                        )
                        SettingsRow(
                            mainLabel = stringResource(R.string.settings_screen_backup_last_backup),
                            secondaryLabel = uiState.lastBackupTime?.format(Utils.dateTimeFormatter)
                                ?: stringResource(R.string.settings_screen_backup_last_backup_never)
                        )

                        Divider()

                        if (!isBackupInProgress) {
                            SettingsRow(
                                mainLabel = stringResource(R.string.settings_screen_backup_start_backup),
                                onClick = { showBackupConfirmDialog = true })
                            SettingsRow(
                                mainLabel = stringResource(R.string.settings_screen_backup_start_restore),
                                onClick = { showRestoreConfirmDialog = true })
                            ConfirmationDialog(
                                isOpen = showBackupConfirmDialog,
                                titleText = stringResource(R.string.settings_screen_backup_dialog_header),
                                contentText = stringResource(R.string.settings_screen_backup_dialog_content),
                                onConfirm = {
                                    isBackupInProgress = true
                                    toggleBottomNav(false)
                                    scope.launch {
                                        onStartBackup()
                                        isBackupInProgress = false
                                        toggleBottomNav(true)
                                    }
                                    showBackupConfirmDialog = false
                                }, onCancel = {
                                    showBackupConfirmDialog = false
                                })
                            ConfirmationDialog(
                                isOpen = showRestoreConfirmDialog,
                                titleText = stringResource(R.string.settings_screen_restore_dialog_header),
                                contentText = stringResource(R.string.settings_screen_restore_dialog_content),
                                onConfirm = {
                                    isBackupInProgress = true
                                    toggleBottomNav(false)
                                    scope.launch {
                                        onStartRestore()
                                        isBackupInProgress = false
                                        toggleBottomNav(true)
                                    }
                                    showRestoreConfirmDialog = false
                                }, onCancel = {
                                    showRestoreConfirmDialog = false
                                })

                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp), horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        SignInButton(onSuccessfulLogin = onSuccessfulLogin)
                    }
                }
            }
        }
    }
}