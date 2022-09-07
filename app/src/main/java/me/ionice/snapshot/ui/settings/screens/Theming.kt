package me.ionice.snapshot.ui.settings.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.components.BackButton
import me.ionice.snapshot.ui.common.screens.BaseScreen
import me.ionice.snapshot.ui.common.screens.FunctionalityNotAvailableScreen
import me.ionice.snapshot.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemingRoute(viewModel: SettingsViewModel, onBack: () -> Unit) {
    BaseScreen(
        headerText = stringResource(R.string.settings_screen_theming_header),
        navigationIcon = { BackButton(onBack = onBack) })
    {
        ThemingScreen()
    }
}

@Composable
private fun ThemingScreen() {
    FunctionalityNotAvailableScreen()
}