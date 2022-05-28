package me.ionice.snapshot.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import me.ionice.snapshot.R
import me.ionice.snapshot.data.network.AuthResultContract
import me.ionice.snapshot.ui.common.ConfirmationDialog
import me.ionice.snapshot.ui.common.SectionHeader
import me.ionice.snapshot.utils.Utils
import java.time.LocalDateTime

@Composable
fun SettingsList(onBackupClick: () -> Unit, onNotificationsClick: () -> Unit, onThemingClick: () -> Unit) {
    SettingsRow(
        mainLabel = stringResource(R.string.settings_screen_backup_header),
        secondaryLabel = stringResource(R.string.settings_screen_backup_subtitle),
        icon = Icons.Outlined.CloudSync,
        onClick = onBackupClick
    )
    SettingsRow(
        mainLabel = stringResource(R.string.settings_screen_notifs_header),
        secondaryLabel = stringResource(R.string.settings_screen_notifs_subtitle),
        icon = Icons.Outlined.Notifications,
        onClick = onNotificationsClick
    )
    SettingsRow(
        mainLabel = stringResource(R.string.settings_screen_theming_header),
        secondaryLabel = stringResource(R.string.settings_screen_theming_subtitle),
        icon = Icons.Outlined.Palette,
        onClick = onThemingClick
    )
}

@Composable
fun BackupScreenOptions(
    isBackupInProgress: Boolean,
    accountEmail: String?,
    lastBackupTime: LocalDateTime?,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit
) {

    var showBackupConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var showRestoreConfirmDialog by rememberSaveable { mutableStateOf(false) }

    if (accountEmail != null) {
        // Show email or something
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_backup_selected_account),
            secondaryLabel = accountEmail
        )
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_backup_last_backup),
            secondaryLabel = lastBackupTime?.format(Utils.dateTimeFormatter)
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
                    onStartBackup()
                    showBackupConfirmDialog = false
                }, onCancel = {
                    showBackupConfirmDialog = false
                })
            ConfirmationDialog(
                isOpen = showRestoreConfirmDialog,
                titleText = stringResource(R.string.settings_screen_restore_dialog_header),
                contentText = stringResource(R.string.settings_screen_restore_dialog_content),
                onConfirm = {
                    onStartRestore()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProminentSwitchSetting(
    mainLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mainLabel,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Preview
@Composable
private fun ProminentSwitchPreview() {
    ProminentSwitchSetting(mainLabel = "Use location", checked = true, onCheckedChange = { })
}

@Composable
private fun SwitchSetting(
    mainLabel: String,
    secondaryLabel: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onCheckedChange(!checked) })
            .fillMaxWidth()
            .padding(horizontal = 24.dp, 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = mainLabel, style = MaterialTheme.typography.titleLarge)
            if (secondaryLabel != null) {
                Text(text = secondaryLabel, style = MaterialTheme.typography.labelMedium)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/**
 * The base entry for each setting. Can be configured to provide button functionality.
 *
 * If button functionality is enabled, the button can be disabled by setting `loading` to `true`.
 */
@Composable
private fun SettingsRow(
    mainLabel: String,
    secondaryLabel: String? = null,
    icon: ImageVector? = null,
    loading: Boolean = false,
    onClick: (() -> Unit)? = null
) {

    // if a onClick is provided, enable button functionality
    val baseModifier =
        onClick?.let { Modifier.clickable(enabled = !loading, onClick = onClick) } ?: Modifier

    Row(
        modifier = baseModifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = mainLabel,
                        modifier = Modifier.padding(end = 24.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    val textColor =
                        if (onClick == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    Text(
                        text = mainLabel,
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor
                    )
                    if (secondaryLabel != null) {
                        Text(
                            text = secondaryLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SwitchRowPreview() {
    Column {
        SettingsRow(
            mainLabel = "Backup & Restore",
            secondaryLabel = "100%",
            icon = Icons.Outlined.CloudSync,
            onClick = { })
        SettingsRow(mainLabel = "Backup & Restore", icon = Icons.Outlined.CloudSync)
        SettingsRow(mainLabel = "Backup & Restore")
    }
}

@Composable
private fun ScreenSubsection(headerText: String?, content: @Composable () -> Unit) {
    headerText?.let { SectionHeader(displayText = headerText) }
    content()
}

@Composable
private fun SignInButton(onSuccessfulLogin: (GoogleSignInAccount) -> Unit) {
    var helperText by remember { mutableStateOf<String?>(null) }
    val signInRequestCode = 1
    var enabled by remember { mutableStateOf(true) }

    val failedText = stringResource(R.string.settings_screen_backup_login_failed)

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account == null) {
                    helperText = failedText
                } else {
                    onSuccessfulLogin(account)
                }
            } catch (e: ApiException) {
                helperText = failedText
            }
        }

    SettingsRow(
        mainLabel = stringResource(R.string.settings_screen_backup_login),
        secondaryLabel = helperText,
        loading = !enabled,
        onClick = {
            enabled = false
            authResultLauncher.launch(signInRequestCode)
            enabled = true
        })
}
