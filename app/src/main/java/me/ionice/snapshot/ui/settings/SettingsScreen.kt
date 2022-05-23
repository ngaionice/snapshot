package me.ionice.snapshot.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import me.ionice.snapshot.ui.common.BaseScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    BaseScreen(headerText = "Settings") {
        Column {
            Text(text = "Functionality not implemented yet.")
        }
    }
}