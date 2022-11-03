package dev.ionice.snapshot.feature.settings.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.ionice.snapshot.core.ui.components.BackButton
import dev.ionice.snapshot.core.ui.screens.BaseScreen
import dev.ionice.snapshot.core.ui.screens.FunctionalityNotAvailableScreen
import dev.ionice.snapshot.feature.settings.R
import dev.ionice.snapshot.feature.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemingRoute(viewModel: SettingsViewModel = hiltViewModel(), onBack: () -> Unit) {
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