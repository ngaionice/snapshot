package me.ionice.snapshot.ui.settings.screens

import androidx.compose.runtime.Composable
import me.ionice.snapshot.ui.common.FunctionalityNotYetAvailableScreen
import me.ionice.snapshot.ui.settings.SettingsUiState
import java.time.LocalTime

@Composable
fun NotificationsScreen(
    uiState: SettingsUiState.Notifications,
    onEnableReminders: (Boolean) -> Unit,
    onReminderTimeChange: (LocalTime) -> Unit,
    onEnableMemories: (Boolean) -> Unit
) {
    FunctionalityNotYetAvailableScreen()
}