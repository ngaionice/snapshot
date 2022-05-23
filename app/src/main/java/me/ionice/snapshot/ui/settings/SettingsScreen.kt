package me.ionice.snapshot.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import me.ionice.snapshot.ui.common.BaseScreen

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    BaseScreen(headerText = "Settings") {
        Column {
            Text(text = "Functionality not implemented yet.")
        }
    }
}