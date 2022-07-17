package me.ionice.snapshot.ui.settings.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.BackButton
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.FunctionalityNotYetAvailableScreen
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
    FunctionalityNotYetAvailableScreen()
}