package me.ionice.snapshot.ui.settings.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.components.BackButton
import me.ionice.snapshot.ui.common.screens.BaseScreen
import me.ionice.snapshot.ui.common.screens.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.common.screens.LoadingScreen
import me.ionice.snapshot.ui.settings.NotifsUiState
import me.ionice.snapshot.ui.settings.SettingSwitch
import me.ionice.snapshot.ui.settings.SettingsRow
import me.ionice.snapshot.ui.settings.SettingsViewModel
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
            onEnableReminders = viewModel::setRemindersEnabled,
            onReminderTimeChange = {},
            onEnableMemories = {})

        BackHandler(onBack = onBack)
    }
}

@Composable
private fun NotificationsScreen(
    uiStateProvider: () -> NotifsUiState,
    onEnableReminders: (Boolean) -> Unit,
    onReminderTimeChange: (LocalTime) -> Unit,
    onEnableMemories: (Boolean) -> Unit
) {
    when (val uiState = uiStateProvider()) {
        is NotifsUiState.Loading -> LoadingScreen()
        is NotifsUiState.Error -> FunctionalityNotAvailableScreen("Notifications settings not available due to an error.")
        is NotifsUiState.Success -> {
            Column {
                SettingSwitch(
                    mainLabel = "Use daily reminders",
                    checked = uiState.isRemindersEnabled,
                    onCheckedChange = { onEnableReminders(it) })
                if (uiState.isRemindersEnabled) {
                    SettingsRow(
                        mainLabel = "Remind at",
                        secondaryLabel = Utils.timeFormatter.format(uiState.reminderTime)
                    )
                }
            }
        }
    }

}