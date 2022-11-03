package dev.ionice.snapshot.feature.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.ionice.snapshot.core.ui.components.BackButton
import dev.ionice.snapshot.core.ui.screens.BaseScreen
import dev.ionice.snapshot.feature.settings.R
import dev.ionice.snapshot.feature.settings.components.SettingRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    onNavigateToBackup: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToTheming: () -> Unit,
    onBack: () -> Unit
) {
    BaseScreen(headerText = stringResource(R.string.settings_screen_header), navigationIcon = {
        BackButton(onBack = onBack)
    }) {
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
        SettingRow(
            mainLabel = stringResource(R.string.settings_screen_backup_header),
            secondaryLabel = stringResource(R.string.settings_screen_backup_subtitle),
            icon = Icons.Outlined.CloudSync,
            onClick = onNavigateToBackup
        )
        SettingRow(
            mainLabel = stringResource(R.string.settings_screen_notifs_header),
            secondaryLabel = stringResource(R.string.settings_screen_notifs_subtitle),
            icon = Icons.Outlined.Notifications,
            onClick = onNavigateToNotifications
        )
        SettingRow(
            mainLabel = stringResource(R.string.settings_screen_theming_header),
            secondaryLabel = stringResource(R.string.settings_screen_theming_subtitle),
            icon = Icons.Outlined.Palette,
            onClick = onNavigateToTheming
        )
    }
}