package me.ionice.snapshot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val icon: ImageVector) {

    Days(icon = Icons.Filled.Today),
    Metrics(icon = Icons.Filled.DataUsage),
    Settings(icon = Icons.Filled.Settings);

    companion object {
        fun fromRoute(route: String?): Screen = when (route?.substringBefore("/")) {
            Days.name -> Days
            Metrics.name -> Metrics
            Settings.name -> Settings
            null -> Days
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}