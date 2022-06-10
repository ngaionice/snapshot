package me.ionice.snapshot.ui.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import me.ionice.snapshot.ui.settings.SettingsList

@Composable
fun HomeScreen(
    onBackupClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onThemingClick: () -> Unit
) {
    Column {
        SettingsList(
            onBackupClick = onBackupClick,
            onNotificationsClick = onNotificationsClick,
            onThemingClick = onThemingClick
        )
    }
}