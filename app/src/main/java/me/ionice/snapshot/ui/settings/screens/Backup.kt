package me.ionice.snapshot.ui.settings.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import me.ionice.snapshot.R
import me.ionice.snapshot.data.backup.GAuthResultContract
import me.ionice.snapshot.ui.common.components.BackButton
import me.ionice.snapshot.ui.common.components.ConfirmationDialog
import me.ionice.snapshot.ui.common.components.PageSection
import me.ionice.snapshot.ui.common.components.TimePicker
import me.ionice.snapshot.ui.common.screens.BaseScreen
import me.ionice.snapshot.ui.common.screens.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.settings.BackupUiState
import me.ionice.snapshot.ui.settings.SettingsViewModel
import me.ionice.snapshot.ui.settings.components.*
import me.ionice.snapshot.utils.Utils
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRoute(viewModel: SettingsViewModel = hiltViewModel(), onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(
        headerText = stringResource(R.string.settings_screen_backup_header),
        navigationIcon = { BackButton(onBack = onBack) })
    {
        BackupScreen(
            uiStateProvider = { uiState.backupUiState },
            onEnableBackup = viewModel::setBackupEnabled,
            onSuccessfulLogin = viewModel::loggedInToGoogle,
            onStartBackup = viewModel::backupDatabase,
            onStartRestore = viewModel::restoreDatabase,
            onAutoBackupConfigChange = viewModel::setAutoBackups
        )
    }
}

@Composable
private fun BackupScreen(
    uiStateProvider: () -> BackupUiState,
    onEnableBackup: (Boolean) -> Unit,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit,
    onAutoBackupConfigChange: (Int, LocalTime, Boolean) -> Unit
) {
    when (val uiState = uiStateProvider()) {
        is BackupUiState.Loading -> LoadingScreen()
        is BackupUiState.Error ->
            FunctionalityNotAvailableScreen(message = "Cannot access backups due to an error.")
        is BackupUiState.Success ->
            CanBackupScreen(
                uiState = uiState,
                onEnableBackup = onEnableBackup,
                onStartBackup = onStartBackup,
                onStartRestore = onStartRestore,
                onAutoBackupConfigChange = onAutoBackupConfigChange,
                onSuccessfulLogin = onSuccessfulLogin
            )
    }
}

@VisibleForTesting
@Composable
fun LoadingScreen() {
    Column {
        FilledSettingSwitchPlaceholder()
        PageSection(title = stringResource(R.string.settings_screen_backup_general_subsection_header)) {
            SettingRowPlaceholder(hasSecondary = true)
            SettingRowPlaceholder(hasSecondary = true)
        }
        PageSection(title = stringResource(R.string.settings_screen_backup_auto_backup_subsection_header)) {
            SettingRowPlaceholder(hasSecondary = true)
            SettingRowPlaceholder(hasSecondary = true)
            SettingRowPlaceholder()
        }
        PageSection(title = stringResource(R.string.settings_screen_backup_manual_actions_subsection_header)) {
            SettingRowPlaceholder()
            SettingRowPlaceholder()
        }
    }
}


@Composable
private fun CanBackupScreen(
    uiState: BackupUiState.Success,
    onEnableBackup: (Boolean) -> Unit,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit,
    onAutoBackupConfigChange: (Int, LocalTime, Boolean) -> Unit,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit
) {
    Column {
        BackupEnabledToggle(isEnabled = uiState.isEnabled, onIsEnabledChange = onEnableBackup)
        AnimatedVisibility(
            visible = uiState.isEnabled, enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (uiState.signedInGoogleAccountEmail != null) {
                BackupFunctionalities(
                    isBackupInProgress = uiState.isBackupInProgress,
                    email = uiState.signedInGoogleAccountEmail,
                    lastBackupTime = uiState.lastBackupTime,
                    backupFreq = uiState.autoBackupFrequency,
                    backupTime = uiState.autoBackupTime,
                    backupOnCellular = uiState.autoBackupOnCellular,
                    onStartBackup = onStartBackup,
                    onStartRestore = onStartRestore,
                    onBackupFreqChange = {
                        onAutoBackupConfigChange(
                            it,
                            uiState.autoBackupTime,
                            uiState.autoBackupOnCellular
                        )
                    },
                    onBackupTimeChange = {
                        onAutoBackupConfigChange(
                            uiState.autoBackupFrequency,
                            it,
                            uiState.autoBackupOnCellular
                        )
                    },
                    onBackupUseCellularChange = {
                        onAutoBackupConfigChange(
                            uiState.autoBackupFrequency,
                            uiState.autoBackupTime,
                            it
                        )
                    }
                )
            } else {
                AccountManagement(onSuccessfulLogin = onSuccessfulLogin)
            }
        }
    }
}

@Composable
private fun BackupFunctionalities(
    isBackupInProgress: Boolean,
    email: String,
    lastBackupTime: LocalDateTime?,
    backupFreq: Int,
    backupTime: LocalTime,
    backupOnCellular: Boolean,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit,
    onBackupFreqChange: (Int) -> Unit,
    onBackupTimeChange: (LocalTime) -> Unit,
    onBackupUseCellularChange: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        BackupInfo(email = email, lastBackupTime = lastBackupTime)
        if (isBackupInProgress) {
            BackupInProgress()
        } else {
            AutoBackupOptions(
                onBackupFreqChange = onBackupFreqChange,
                onBackupTimeChange = onBackupTimeChange,
                onBackupOnCellularChange = onBackupUseCellularChange,
                backupFreq = backupFreq,
                backupTime = backupTime,
                backupOnCellular = backupOnCellular
            )
            BackupActions(onStartBackup = onStartBackup, onStartRestore = onStartRestore)
        }
    }
}

@Composable
private fun BackupEnabledToggle(isEnabled: Boolean, onIsEnabledChange: (Boolean) -> Unit) {
    FilledSettingSwitch(
        mainLabel = stringResource(R.string.settings_screen_backup_main_switch),
        checked = isEnabled,
        onCheckedChange = { onIsEnabledChange(!isEnabled) }
    )
}

@Composable
private fun BackupInfo(email: String, lastBackupTime: LocalDateTime?) {
    PageSection(title = stringResource(R.string.settings_screen_backup_general_subsection_header)) {
        SettingRow(
            mainLabel = stringResource(R.string.settings_screen_backup_selected_account),
            secondaryLabel = email
        )
        SettingRow(
            mainLabel = stringResource(R.string.settings_screen_backup_last_backup),
            secondaryLabel = lastBackupTime?.format(Utils.dateTimeFormatter)
                ?: stringResource(R.string.settings_screen_backup_last_backup_never)
        )
    }
}

@Composable
private fun AutoBackupOptions(
    backupFreq: Int,
    backupTime: LocalTime,
    backupOnCellular: Boolean,
    onBackupFreqChange: (Int) -> Unit,
    onBackupTimeChange: (LocalTime) -> Unit,
    onBackupOnCellularChange: (Boolean) -> Unit
) {
    var showFreqPickerDialog by rememberSaveable { mutableStateOf(false) }
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }

    val freqOptions = listOf(
        Pair(0, stringResource(R.string.settings_auto_backup_freq_never)),
        Pair(1, stringResource(R.string.settings_auto_backup_freq_daily)),
        Pair(7, stringResource(R.string.settings_auto_backup_freq_weekly)),
        Pair(30, stringResource(R.string.settings_auto_backup_freq_monthly))
    )

    val backupFreqText = when (backupFreq) {
        0 -> stringResource(R.string.settings_auto_backup_freq_never)
        1 -> stringResource(R.string.settings_auto_backup_freq_daily)
        7 -> stringResource(R.string.settings_auto_backup_freq_weekly)
        30 -> stringResource(R.string.settings_auto_backup_freq_monthly)
        else -> throw IllegalArgumentException("backupFreq should be one of 0, 1, 7, 30")
    }

    PageSection(title = stringResource(R.string.settings_screen_backup_auto_backup_subsection_header)) {
        SettingRow(
            mainLabel = stringResource(R.string.settings_auto_backup_frequency),
            secondaryLabel = backupFreqText,
            onClick = { showFreqPickerDialog = true })
        SettingRow(
            mainLabel = stringResource(R.string.settings_auto_backup_time),
            secondaryLabel = backupTime.format(Utils.timeFormatter),
            onClick = { showTimePickerDialog = true },
            disabled = backupFreq <= 0
        )
        SettingSwitch(
            mainLabel = stringResource(R.string.settings_auto_backup_use_cellular),
            checked = backupOnCellular,
            onCheckedChange = onBackupOnCellularChange
        )
    }

    if (showFreqPickerDialog) {
        BackupFreqPickerDialog(
            selected = freqOptions.find { (f, _) -> f == backupFreq }
                ?: throw IllegalArgumentException(
                    "Illegal backupFreq value"
                ),
            options = freqOptions,
            onSelection = {
                onBackupFreqChange(it.first)
                showFreqPickerDialog = false
            },
            onClose = { showFreqPickerDialog = false }
        )
    }
    if (showTimePickerDialog) {
        TimePickerDialog(
            initialTime = backupTime,
            onSelection = {
                onBackupTimeChange(it)
                showTimePickerDialog = false
            },
            onClose = { showTimePickerDialog = false }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackupFreqPickerDialog(
    selected: Pair<Int, String>,
    options: List<Pair<Int, String>>,
    onSelection: (Pair<Int, String>) -> Unit,
    onClose: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(selected) }

    AlertDialog(
        onDismissRequest = onClose,
        text = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    value = current.second,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.settings_auto_backup_frequency)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.second) },
                            onClick = {
                                current = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onSelection(current) }) { Text(stringResource(R.string.common_dialog_ok)) } },
        dismissButton = { TextButton(onClick = onClose) { Text(stringResource(R.string.common_dialog_cancel)) } })

}

@Composable
private fun BackupActions(onStartBackup: () -> Unit, onStartRestore: () -> Unit) {
    var showBackupDialog by rememberSaveable { mutableStateOf(false) }
    var showRestoreDialog by rememberSaveable { mutableStateOf(false) }

    PageSection(title = stringResource(R.string.settings_screen_backup_manual_actions_subsection_header)) {
        SettingRow(
            mainLabel = stringResource(R.string.settings_screen_backup_start_backup),
            onClick = { showBackupDialog = true })
        SettingRow(
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(stringResource(R.string.settings_screen_sync_in_progress))
    }
}

@Composable
private fun AccountManagement(onSuccessfulLogin: (GoogleSignInAccount) -> Unit) {
    PageSection(title = stringResource(R.string.settings_screen_backup_drive_settings)) {
        SignInButton(onSuccessfulLogin = onSuccessfulLogin)
    }
}

@Composable
private fun SignInButton(onSuccessfulLogin: (GoogleSignInAccount) -> Unit) {
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

    SettingRow(
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
private fun TimePickerDialog(
    initialTime: LocalTime = LocalTime.MIDNIGHT,
    onSelection: (LocalTime) -> Unit,
    onClose: () -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(text = "Select time", style = MaterialTheme.typography.labelSmall)
        },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimePicker(initialTime = selectedTime, onSelectTime = { selectedTime = it })
            }
        },
        confirmButton = {
            TextButton(onClick = { onSelection(selectedTime) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text(text = "Cancel")
            }
        }
    )
}