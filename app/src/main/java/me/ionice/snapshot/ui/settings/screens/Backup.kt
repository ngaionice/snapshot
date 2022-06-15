package me.ionice.snapshot.ui.settings.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.ConfirmationDialog
import me.ionice.snapshot.ui.common.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.settings.*
import me.ionice.snapshot.utils.Utils
import java.time.LocalDateTime

// TODO:
//  - move isBackupInProgress and the suspend functions to ViewModel;
//    being able to cancel the backup if leaving the screen can be dangerous,
//    and whether backup is in progress should not be decided by the UI

@Composable
fun BackupScreen(
    uiState: SettingsUiState.Backup,
    onEnableBackup: (Boolean) -> Unit,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit,
) {

    if (!uiState.dataAvailable) {
        CannotBackupScreen()
    } else {
        CanBackupScreen(
            uiState = uiState,
            isBackupInProgress = uiState.isBackupInProgress,
            onEnableBackup = onEnableBackup,
            onStartBackup = onStartBackup,
            onStartRestore = onStartRestore,
            onSuccessfulLogin = onSuccessfulLogin
        )
    }
}

@Composable
private fun CannotBackupScreen() {
    FunctionalityNotAvailableScreen(reason = stringResource(R.string.settings_screen_backup_na_reason))
}

@Composable
private fun CanBackupScreen(
    uiState: SettingsUiState.Backup,
    isBackupInProgress: Boolean,
    onEnableBackup: (Boolean) -> Unit,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit
) {
    Column {
        BackupEnabledToggle(isEnabled = uiState.backupEnabled, onIsEnabledChange = onEnableBackup)
        AnimatedVisibility(
            visible = uiState.backupEnabled, enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (uiState.signedInGoogleAccountEmail != null) {
                BackupFunctionalities(
                    isBackupInProgress = isBackupInProgress,
                    email = uiState.signedInGoogleAccountEmail,
                    lastBackupTime = uiState.lastBackupTime,
                    onStartBackup = onStartBackup,
                    onStartRestore = onStartRestore
                )
            } else {
                SignInButton(onSuccessfulLogin = onSuccessfulLogin)
            }
        }
    }
}

@Composable
private fun BackupFunctionalities(
    isBackupInProgress: Boolean,
    email: String,
    lastBackupTime: LocalDateTime?,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit
) {

    Column {
        BackupInfo(email = email, lastBackupTime = lastBackupTime)
        if (isBackupInProgress) {
            BackupInProgress()
        } else {
            BackupActions(onStartBackup = onStartBackup, onStartRestore = onStartRestore)
        }
    }
}

@Composable
private fun BackupEnabledToggle(isEnabled: Boolean, onIsEnabledChange: (Boolean) -> Unit) {
    ProminentSwitchSetting(
        mainLabel = stringResource(R.string.settings_screen_backup_main_switch),
        checked = isEnabled,
        onCheckedChange = { onIsEnabledChange(!isEnabled) }
    )
}

@Composable
private fun BackupInfo(email: String, lastBackupTime: LocalDateTime?) {
    SettingsGroup(title = stringResource(R.string.settings_screen_backup_general_subsection_header)) {
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_backup_selected_account),
            secondaryLabel = email
        )
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_backup_last_backup),
            secondaryLabel = lastBackupTime?.format(Utils.dateTimeFormatter)
                ?: stringResource(R.string.settings_screen_backup_last_backup_never)
        )
    }
}

@Composable
private fun BackupActions(onStartBackup: () -> Unit, onStartRestore: () -> Unit) {
    var showBackupDialog by rememberSaveable { mutableStateOf(false) }
    var showRestoreDialog by rememberSaveable { mutableStateOf(false) }

    SettingsGroup(title = stringResource(R.string.settings_screen_backup_actions_subsection_header)) {
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_backup_start_backup),
            onClick = { showBackupDialog = true })
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_backup_start_restore),
            onClick = { showRestoreDialog = true })
    }
    ConfirmationDialog(
        isOpen = showBackupDialog,
        titleText = stringResource(R.string.settings_screen_backup_dialog_header),
        contentText = stringResource(R.string.settings_screen_backup_dialog_content),
        onConfirm = {
            onStartBackup()
            showBackupDialog = false
        }, onCancel = {
            showBackupDialog = false
        })
    ConfirmationDialog(
        isOpen = showRestoreDialog,
        titleText = stringResource(R.string.settings_screen_restore_dialog_header),
        contentText = stringResource(R.string.settings_screen_restore_dialog_content),
        onConfirm = {
            onStartRestore()
            showRestoreDialog = false
        }, onCancel = {
            showRestoreDialog = false
        })
}

@Composable
private fun BackupInProgress() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}