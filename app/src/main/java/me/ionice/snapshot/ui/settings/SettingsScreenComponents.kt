package me.ionice.snapshot.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery4Bar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import me.ionice.snapshot.data.backup.AuthResultContract
import me.ionice.snapshot.ui.common.SectionHeader
import java.time.LocalDateTime

@Composable
fun BackupScreenOptions(
    isBackupInProgress: Boolean,
    accountEmail: String?,
    lastBackupTime: LocalDateTime?,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit
) {
    if (accountEmail != null) {
        // Show email or something
        SettingsRow(mainLabel = "Current selected account", secondaryLabel = accountEmail)
        SettingsRow(mainLabel = "Last backup", secondaryLabel = lastBackupTime?.toString() ?: "Never")

        Divider()

        if (!isBackupInProgress) {
            SettingsRow(mainLabel = "Backup now", onClick = { onStartBackup() })
            SettingsRow(mainLabel = "Restore cloud backup", onClick = { onStartRestore() })
        } else {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        }
    } else {
        SignInButton(onSuccessfulLogin = onSuccessfulLogin)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProminentSwitchSetting(mainLabel: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(onClick = { onCheckedChange(!checked) }, modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)) {
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = mainLabel, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
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
        if (onClick != null) Modifier.clickable(enabled = !loading, onClick = onClick) else Modifier

    Row(
        modifier = baseModifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = mainLabel, modifier = Modifier.padding(end = 24.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    val textColor = if (onClick == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    Text(text = mainLabel, style = MaterialTheme.typography.titleLarge, color = textColor)
                    if (secondaryLabel != null) {
                        Text(text = secondaryLabel, style = MaterialTheme.typography.labelMedium, color = textColor)
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
        SettingsRow(mainLabel = "Battery", secondaryLabel = "100%", icon = Icons.Filled.Battery4Bar)
        SettingsRow(mainLabel = "Battery", icon = Icons.Filled.Battery4Bar)
        SettingsRow(mainLabel = "Battery")
    }
}

@Composable
private fun Section(headerText: String, content: @Composable () -> Unit) {
    Column {
        SectionHeader(displayText = headerText)
        content()
    }
}

@Composable
private fun SignInButton(onSuccessfulLogin: (GoogleSignInAccount) -> Unit) {
    var helperText by remember { mutableStateOf<String?>(null) }
    val signInRequestCode = 1
    var enabled by remember { mutableStateOf(true) }

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account == null) {
                    helperText = "Google sign in failed, tap to retry"
                } else {
                    onSuccessfulLogin(account)
                }
            } catch (e: ApiException) {
                helperText = "Google sign in failed, tap to retry"
            }
        }

    SettingsRow(
        mainLabel = "Login to Google",
        secondaryLabel = helperText,
        loading = !enabled,
        onClick = {
            enabled = false
            authResultLauncher.launch(signInRequestCode)
            enabled = true
        })
}
