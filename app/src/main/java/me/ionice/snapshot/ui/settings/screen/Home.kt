package me.ionice.snapshot.ui.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.settings.SettingsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    onNavigateToBackup: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToTheming: () -> Unit
) {
    BaseScreen(headerText = stringResource(R.string.settings_screen_header)) {
        HomeScreen(
            onNavigateToBackup = onNavigateToBackup,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToTheming = onNavigateToTheming
        )
    }
}

@Composable
private fun HomeScreen(
    onNavigateToBackup: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToTheming: () -> Unit
) {
    Column {
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_backup_header),
            secondaryLabel = stringResource(R.string.settings_screen_backup_subtitle),
            icon = Icons.Outlined.CloudSync,
            onClick = onNavigateToBackup
        )
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_notifs_header),
            secondaryLabel = stringResource(R.string.settings_screen_notifs_subtitle),
            icon = Icons.Outlined.Notifications,
            onClick = onNavigateToNotifications
        )
        SettingsRow(
            mainLabel = stringResource(R.string.settings_screen_theming_header),
            secondaryLabel = stringResource(R.string.settings_screen_theming_subtitle),
            icon = Icons.Outlined.Palette,
            onClick = onNavigateToTheming
        )
    }
}