package me.ionice.snapshot.ui.settings.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.components.BackButton
import me.ionice.snapshot.ui.common.components.ConfirmationDialog
import me.ionice.snapshot.ui.common.screens.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.common.components.PageSection
import me.ionice.snapshot.ui.common.screens.BaseScreen
import me.ionice.snapshot.ui.common.screens.LoadingScreen
import me.ionice.snapshot.ui.settings.*
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
        when (uiState) {
            is SettingsUiState.Loading -> LoadingScreen()
            is SettingsUiState.Loaded -> {
                BackupScreen(
                    uiState = (uiState as SettingsUiState.Loaded).backupPreferences,
                    onEnableBackup = viewModel::setBackupEnabled,
                    onSuccessfulLogin = viewModel::loggedInToGoogle,
                    onStartBackup = viewModel::backupDatabase,
                    onStartRestore = viewModel::restoreDatabase,
                    onBackupTimeChange = viewModel::setBackupTime,
                    onBackupFreqChange = viewModel::setBackupFrequency
                )
            }
        }
    }
}

@Composable
private fun BackupScreen(
    uiState: SettingsUiState.Loaded.Backup,
    onEnableBackup: (Boolean) -> Unit,
    onSuccessfulLogin: (GoogleSignInAccount) -> Unit,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit,
    onBackupFreqChange: (Int) -> Unit,
    onBackupTimeChange: (LocalTime) -> Unit
) {
    when (uiState) {
        is SettingsUiState.Loaded.Backup.Available -> {
            CanBackupScreen(
                uiState = uiState,
                isBackupInProgress = uiState.isBackupInProgress,
                onEnableBackup = onEnableBackup,
                onStartBackup = onStartBackup,
                onStartRestore = onStartRestore,
                onSuccessfulLogin = onSuccessfulLogin,
                onBackupFreqChange = onBackupFreqChange,
                onBackupTimeChange = onBackupTimeChange
            )
        }
        is SettingsUiState.Loaded.Backup.NotAvailable -> CannotBackupScreen()
    }
}

@Composable
private fun CannotBackupScreen() {
    FunctionalityNotAvailableScreen(message = "Cannot access backups as ${stringResource(R.string.settings_screen_backup_na_reason)}.")
}

@Composable
private fun CanBackupScreen(
    uiState: SettingsUiState.Loaded.Backup.Available,
    isBackupInProgress: Boolean,
    onEnableBackup: (Boolean) -> Unit,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit,
    onBackupFreqChange: (Int) -> Unit,
    onBackupTimeChange: (LocalTime) -> Unit,
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
                    backupFreq = uiState.autoBackupFrequency,
                    backupTime = uiState.autoBackupTime,
                    onStartBackup = onStartBackup,
                    onStartRestore = onStartRestore,
                    onBackupFreqChange = onBackupFreqChange,
                    onBackupTimeChange = onBackupTimeChange
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
    backupFreq: Int,
    backupTime: LocalTime,
    onStartBackup: () -> Unit,
    onStartRestore: () -> Unit,
    onBackupFreqChange: (Int) -> Unit,
    onBackupTimeChange: (LocalTime) -> Unit
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
                backupFreq = backupFreq,
                backupTime = backupTime
            )
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
    PageSection(title = stringResource(R.string.settings_screen_backup_general_subsection_header)) {
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
private fun AutoBackupOptions(
    backupFreq: Int,
    backupTime: LocalTime,
    onBackupFreqChange: (Int) -> Unit,
    onBackupTimeChange: (LocalTime) -> Unit
) {
    var showFreqPickerDialog by rememberSaveable { mutableStateOf(false) }
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }

    val freqOptions = listOf(
        Pair(0, stringResource(R.string.settings_auto_backup_freq_never)),
        Pair(1, stringResource(R.string.settings_auto_backup_freq_daily)),
        Pair(7, stringResource(R.string.settings_auto_backup_freq_weekly)),
        Pair(14, stringResource(R.string.settings_auto_backup_freq_every_two_weeks)),
        Pair(28, stringResource(R.string.settings_auto_backup_freq_every_four_weeks))
    )

    val backupFreqText = when (backupFreq) {
        0 -> stringResource(R.string.settings_auto_backup_freq_never)
        1 -> stringResource(R.string.settings_auto_backup_freq_daily)
        7 -> stringResource(R.string.settings_auto_backup_freq_weekly)
        14 -> stringResource(R.string.settings_auto_backup_freq_every_two_weeks)
        28 -> stringResource(R.string.settings_auto_backup_freq_every_four_weeks)
        else -> throw IllegalArgumentException("backupFreq should be one of 0, 1, 7, 14, 28")
    }

    PageSection(title = stringResource(R.string.settings_screen_backup_auto_backup_subsection_header)) {
        SettingsRow(
            mainLabel = stringResource(R.string.settings_auto_backup_frequency),
            secondaryLabel = backupFreqText,
            onClick = { showFreqPickerDialog = true })
        SettingsRow(
            mainLabel = stringResource(R.string.settings_auto_backup_time),
            secondaryLabel = backupTime.format(Utils.timeFormatter),
            onClick = { showTimePickerDialog = true },
            disabled = backupFreq <= 0
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
    TimePickerDialog(isOpen = showTimePickerDialog,
        initialTime = backupTime,
        onSelection = {
            onBackupTimeChange(it)
            showTimePickerDialog = false
        },
        onClose = { showTimePickerDialog = false })
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