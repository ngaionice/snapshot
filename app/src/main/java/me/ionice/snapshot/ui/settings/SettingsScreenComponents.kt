package me.ionice.snapshot.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import me.ionice.snapshot.data.backup.AuthResultContract

@Composable
fun BackupSection(isEnabled: Boolean, accountEmail: String?, setIsEnabled: (Boolean) -> Unit, onSuccessfulLogin: (GoogleSignInAccount) -> Unit, onStartBackup: () -> Unit) {

    var backupInProgress by remember {
        mutableStateOf(false)
    }

    Section(headerText = "Backup & Restore") {
        SwitchSetting(mainLabel = "Use backups", checked = isEnabled, onCheckedChange = setIsEnabled)
        if (isEnabled) {
            if (accountEmail != null) {
                // Show email or something
                SettingsRow(mainLabel = accountEmail, secondaryLabel = "Current selected account")
                BackupButton(enabled = !backupInProgress, onClick = {
                    backupInProgress = true
                    onStartBackup()
                    backupInProgress = false
                })
            } else {
                SignInButton(onSuccessfulLogin = onSuccessfulLogin)
            }
        }
    }
}

@Composable
private fun SwitchSetting(mainLabel: String, secondaryLabel: String? = null, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier
        .clickable(onClick = { onCheckedChange(!checked) })
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
private fun SettingsRow(mainLabel: String, secondaryLabel: String? = null, loading: Boolean = false, onClick: (() -> Unit)? = null) {

    // if a onClick is provided, enable button functionality
    val baseModifier = if (onClick != null) Modifier.clickable(enabled = !loading, onClick = onClick) else Modifier

    Row(modifier = baseModifier
        .fillMaxWidth()
        .padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = mainLabel, style = MaterialTheme.typography.titleLarge)
                if (secondaryLabel != null) {
                    Text(text = secondaryLabel, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun Section(headerText: String, content: @Composable () -> Unit) {
    Column {
        Text(text = headerText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 16.dp))
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
                    helperText = "Google sign in failed"
                } else {
                    onSuccessfulLogin(account)
                }
            } catch (e: ApiException) {
                helperText = "Google sign in failed"
            }
        }

    SettingsRow(mainLabel = "Login to Google", secondaryLabel = helperText, loading = !enabled, onClick = {
        enabled = false
        authResultLauncher.launch(signInRequestCode)
        enabled = true
    })
}

@Composable
private fun BackupButton(enabled: Boolean, onClick: () -> Unit) {
    SettingsRow(mainLabel = "Backup now", loading = !enabled, onClick = onClick)
}