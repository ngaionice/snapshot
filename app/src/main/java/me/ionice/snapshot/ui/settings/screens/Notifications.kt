package me.ionice.snapshot.ui.settings.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.components.BackButton
import me.ionice.snapshot.ui.common.components.PageSection
import me.ionice.snapshot.ui.common.screens.BaseScreen
import me.ionice.snapshot.ui.common.screens.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.common.screens.LoadingScreen
import me.ionice.snapshot.ui.settings.NotifsUiState
import me.ionice.snapshot.ui.settings.SettingsViewModel
import me.ionice.snapshot.ui.settings.components.FilledSettingSwitch
import me.ionice.snapshot.ui.settings.components.SettingRow
import me.ionice.snapshot.ui.settings.components.SettingSwitch
import me.ionice.snapshot.ui.settings.components.TimePickerDialog
import me.ionice.snapshot.utils.Utils
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsRoute(viewModel: SettingsViewModel = hiltViewModel(), onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(
        headerText = stringResource(R.string.settings_screen_notifs_header),
        navigationIcon = { BackButton(onBack = onBack) }
    ) {
        NotificationsScreen(
            uiStateProvider = { uiState.notifsUiState },
            onNotifsEnabledChange = viewModel::setNotifsEnabled,
            onRemindersChange = viewModel::setDailyReminders
        )
        BackHandler(onBack = onBack)
    }
}

@Composable
private fun NotificationsScreen(
    uiStateProvider: () -> NotifsUiState,
    onNotifsEnabledChange: (Boolean) -> Unit,
    onRemindersChange: (Boolean, LocalTime) -> Unit
) {
    when (val uiState = uiStateProvider()) {
        is NotifsUiState.Loading -> LoadingScreen()
        is NotifsUiState.Error -> FunctionalityNotAvailableScreen("Notifications settings not available due to an error.")
        is NotifsUiState.Success -> {
            SuccessScreen(
                uiState = uiState,
                onNotifsEnabledChange = onNotifsEnabledChange,
                onRemindersChange = onRemindersChange
            )
        }
    }
}

@Composable
fun SuccessScreen(
    uiState: NotifsUiState.Success,
    onNotifsEnabledChange: (Boolean) -> Unit,
    onRemindersChange: (Boolean, LocalTime) -> Unit
) {
    Column {
        FilledSettingSwitch(
            mainLabel = "Use notifications",
            checked = uiState.areNotifsEnabled,
            onCheckedChange = onNotifsEnabledChange
        )
        AnimatedVisibility(
            visible = uiState.areNotifsEnabled, enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                RemindersSection(
                    isEnabled = uiState.isRemindersEnabled,
                    time = uiState.reminderTime,
                    onEnabledChange = { onRemindersChange(it, uiState.reminderTime) },
                    onTimeChange = { onRemindersChange(uiState.isRemindersEnabled, it) }
                )
                MemoriesSection()
            }
        }
    }
}

@Composable
private fun RemindersSection(
    isEnabled: Boolean,
    time: LocalTime,
    onEnabledChange: (Boolean) -> Unit,
    onTimeChange: (LocalTime) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    PageSection(title = "Reminders") {
        SettingSwitch(
            mainLabel = "Enable reminders",
            secondaryLabel = "Send me daily reminders to write the day's entry",
            checked = isEnabled,
            onCheckedChange = { onEnabledChange(it) })
        SettingRow(
            mainLabel = "Notify me at",
            secondaryLabel = Utils.timeFormatter.format(time),
            enabled = isEnabled,
            onClick = { showTimePicker = true }
        )
    }
    if (showTimePicker) {
        TimePickerDialog(
            initialTime = time,
            onSelection = {
                onTimeChange(it)
                showTimePicker = false
            },
            onClose = { showTimePicker = false }
        )
    }
}

@Composable
private fun MemoriesSection() {
    PageSection(title = "Memories") {
        FunctionalityNotAvailableScreen("Feature coming soon!")
    }
}