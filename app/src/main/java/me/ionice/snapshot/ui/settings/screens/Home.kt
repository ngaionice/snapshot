package me.ionice.snapshot.ui.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.settings.SettingsRow

@Composable
fun HomeScreen(
    onBackupClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onThemingClick: () -> Unit
) {
    Column {
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_backup_header),
            secondaryLabel = stringResource(R.string.settings_screen_backup_subtitle),
            icon = Icons.Outlined.CloudSync,
            onClick = onBackupClick
        )
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_notifs_header),
            secondaryLabel = stringResource(R.string.settings_screen_notifs_subtitle),
            icon = Icons.Outlined.Notifications,
            onClick = onNotificationsClick
        )
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_theming_header),
            secondaryLabel = stringResource(R.string.settings_screen_theming_subtitle),
            icon = Icons.Outlined.Palette,
            onClick = onThemingClick
        )
    }
}