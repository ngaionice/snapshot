package dev.ionice.snapshot.feature.settings.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.ui.components.BackButton
import dev.ionice.snapshot.core.ui.components.PageSection
import dev.ionice.snapshot.core.ui.screens.BaseScreen
import dev.ionice.snapshot.core.ui.screens.LoadingScreen
import dev.ionice.snapshot.core.ui.screens.FunctionalityNotAvailableScreen
import dev.ionice.snapshot.feature.settings.NotifsUiState
import dev.ionice.snapshot.feature.settings.R
import dev.ionice.snapshot.feature.settings.SettingsViewModel
import dev.ionice.snapshot.feature.settings.components.FilledSettingSwitch
import dev.ionice.snapshot.feature.settings.components.SettingRow
import dev.ionice.snapshot.feature.settings.components.SettingSwitch
import dev.ionice.snapshot.feature.settings.components.TimePickerDialog
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

@VisibleForTesting
@Composable
internal fun NotificationsScreen(
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
private fun SuccessScreen(
    uiState: NotifsUiState.Success,
    onNotifsEnabledChange: (Boolean) -> Unit,
    onRemindersChange: (Boolean, LocalTime) -> Unit
) {
    Column {
        FilledSettingSwitch(
            mainLabel = "Use notifications",
            checked = uiState.areNotifsEnabled,
            onCheckedChange = onNotifsEnabledChange,
            testTag = stringResource(R.string.tt_settings_notifs_main_toggle)
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
            onCheckedChange = { onEnabledChange(it) },
            testTag = stringResource(R.string.tt_settings_notifs_reminders_toggle)
        )
        SettingRow(
            mainLabel = "Notify me at",
            secondaryLabel = Utils.timeFormatter.format(time),
            enabled = isEnabled,
            onClick = { showTimePicker = true },
            testTag = stringResource(R.string.tt_settings_notifs_reminders_time_btn)
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