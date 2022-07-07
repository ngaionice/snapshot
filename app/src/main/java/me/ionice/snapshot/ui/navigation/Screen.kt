package me.ionice.snapshot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val icon: ImageVector) {

    day_route(icon = Icons.Filled.Today),
    Metrics(icon = Icons.Filled.DataUsage),
    Settings(icon = Icons.Filled.Settings);
}