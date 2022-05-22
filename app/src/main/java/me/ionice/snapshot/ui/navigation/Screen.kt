package me.ionice.snapshot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val icon: ImageVector) {

    Day(icon = Icons.Filled.Today),
    History(icon = Icons.Filled.History),
    Metrics(icon = Icons.Filled.DataUsage),
    Settings(icon = Icons.Filled.Settings);

    companion object {
        fun fromRoute(route: String?): Screen = when (route?.substringBefore("/")) {
            Day.name -> Day
            History.name -> History
            Metrics.name -> Metrics
            Settings.name -> Settings
            null -> Day
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}