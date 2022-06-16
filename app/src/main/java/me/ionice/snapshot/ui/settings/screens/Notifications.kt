package me.ionice.snapshot.ui.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import me.ionice.snapshot.ui.settings.SettingsRow
import me.ionice.snapshot.ui.settings.SettingsUiState
import me.ionice.snapshot.ui.settings.SettingSwitch
import me.ionice.snapshot.utils.Utils
import java.time.LocalTime

@Composable
fun NotificationsScreen(
    uiState: SettingsUiState.Notifications,
    onEnableReminders: (Boolean) -> Unit,
    onReminderTimeChange: (LocalTime) -> Unit,
    onEnableMemories: (Boolean) -> Unit
) {
    Column {
        SettingSwitch(mainLabel = "Use daily reminders", secondaryLabel = "test", checked = uiState.isRemindersEnabled, onCheckedChange = {onEnableReminders(it)})
        if (uiState.isRemindersEnabled) {
            SettingsRow(mainLabel = "Remind at", secondaryLabel = Utils.timeFormatter.format(uiState.reminderTime))
        }
    }
}