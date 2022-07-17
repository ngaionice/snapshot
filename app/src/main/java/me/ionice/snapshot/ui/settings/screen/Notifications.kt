package me.ionice.snapshot.ui.settings.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.BackButton
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.LoadingScreen
import me.ionice.snapshot.ui.settings.SettingsRow
import me.ionice.snapshot.ui.settings.SettingsUiState
import me.ionice.snapshot.ui.settings.SettingSwitch
import me.ionice.snapshot.ui.settings.SettingsViewModel
import me.ionice.snapshot.utils.Utils
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsRoute(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(
        headerText = stringResource(R.string.settings_screen_notifs_header),
        navigationIcon = { BackButton(onBack = onBack) }) {
        when (uiState) {
            is SettingsUiState.Loading -> LoadingScreen()
            is SettingsUiState.Loaded -> {
                NotificationsScreen(
                    uiState = (uiState as SettingsUiState.Loaded).notificationsPreferences,
                    onEnableReminders = viewModel::setRemindersEnabled,
                    onReminderTimeChange = {},
                    onEnableMemories = {})

                BackHandler(onBack = onBack)
            }
        }
    }
}

@Composable
private fun NotificationsScreen(
    uiState: SettingsUiState.Loaded.Notifications,
    onEnableReminders: (Boolean) -> Unit,
    onReminderTimeChange: (LocalTime) -> Unit,
    onEnableMemories: (Boolean) -> Unit
) {
    Column {
        SettingSwitch(mainLabel = "Use daily reminders", checked = uiState.isRemindersEnabled, onCheckedChange = {onEnableReminders(it)})
        if (uiState.isRemindersEnabled) {
            SettingsRow(mainLabel = "Remind at", secondaryLabel = Utils.timeFormatter.format(uiState.reminderTime))
        }
    }
}