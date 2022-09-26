package me.ionice.snapshot.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
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
import me.ionice.snapshot.data.backup.GAuthResultContract
import me.ionice.snapshot.ui.common.components.PageSection
import java.time.LocalTime

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
fun SettingSwitch(
    mainLabel: String,
    secondaryLabel: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onCheckedChange(!checked) })
            .padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .weight(1f), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = mainLabel, style = MaterialTheme.typography.titleLarge)
                if (secondaryLabel != null) {
                    Text(text = secondaryLabel, style = MaterialTheme.typography.labelMedium)
                }
            }

        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }

}

/**
 * The base entry for each setting. Can be configured to provide button functionality.
 *
 * If button functionality is enabled, the button can be disabled by setting `loading` to `true`.
 */
@Composable
fun SettingsRow(
    mainLabel: String,
    secondaryLabel: String? = null,
    icon: ImageVector? = null,
    disabled: Boolean = false,
    onClick: (() -> Unit)? = null
) {

    // if a onClick is provided, enable button functionality
    val baseModifier =
        onClick?.let { Modifier.clickable(enabled = !disabled, onClick = onClick) } ?: Modifier

    Row(
        modifier = baseModifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                    if (onClick == null || disabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
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



@Preview
@Composable
private fun SettingsGroupTest() {
    Column {
        PageSection(title = "Title 1") {
            SettingsRow(mainLabel = "Text 1")
            SettingsRow(mainLabel = "Text 2")
            SettingsRow(mainLabel = "Text 3")
        }
        PageSection(title = "Title 2") {
            SettingsRow(mainLabel = "Text 4")
            SettingsRow(mainLabel = "Text 5")
            SettingsRow(mainLabel = "Text 6")
        }
    }
}

@Composable
fun SignInButton(onSuccessfulLogin: (GoogleSignInAccount) -> Unit) {
    var helperText by remember { mutableStateOf<String?>(null) }
    val signInRequestCode = 1
    var enabled by remember { mutableStateOf(true) }

    val failedText = stringResource(R.string.settings_screen_backup_login_failed)

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = GAuthResultContract()) { task ->
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
        disabled = !enabled,
        onClick = {
            enabled = false
            authResultLauncher.launch(signInRequestCode)
            enabled = true
        })
}

@Composable
fun TimePickerDialog(
    title: String? = null,
    isOpen: Boolean,
    initialTime: LocalTime = LocalTime.MIDNIGHT,
    onSelection: (LocalTime) -> Unit,
    onClose: () -> Unit
) {

    var selectedHour by rememberSaveable { mutableStateOf(initialTime.hour) }
    var selectedMinute by rememberSaveable { mutableStateOf((initialTime.minute / 5) * 5) }

    val hours = (0..23).toList()
    val minutes = (0..55 step 5).toList()

    if (isOpen) {
        AlertDialog(
            onDismissRequest = onClose,
            title = {
                title?.let { Text(text = it) }
            },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(1f)) {
                        NumberDropdown(
                            options = hours,
                            selected = selectedHour,
                            onSelection = { selectedHour = it })
                    }
                    Text(":")
                    Box(Modifier.weight(1f)) {
                        NumberDropdown(
                            options = minutes,
                            selected = selectedMinute,
                            onSelection = { selectedMinute = it })
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onSelection(LocalTime.of(selectedHour, selectedMinute)) }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onClose) {
                    Text(text = "Cancel")
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberDropdown(
    label: String? = null,
    options: List<Int>,
    selected: Int,
    onSelection: (Int) -> Unit
) {
    if (!options.contains(selected)) {
        throw IllegalArgumentException("options does not contain initialValue")
    }

    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }) {
        TextField(
            readOnly = true,
            value = selected.toString(),
            onValueChange = {},
            label = { label?.let { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption.toString()) },
                    onClick = {
                        onSelection(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}
