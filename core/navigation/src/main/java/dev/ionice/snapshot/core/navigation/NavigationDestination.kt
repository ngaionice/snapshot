package dev.ionice.snapshot.core.navigation

interface NavigationDestination {
    /**
     * Defines a specific route this destination belongs to.
     * Route is a String that defines the path to your composable.
     * You can think of it as an implicit deep link that leads to a specific destination.
     * Each destination should have a unique route.
     */
    val route: String

    /**
     * Defines a specific destination ID.
     * This is needed when using nested graphs via the navigation DLS, to differentiate a specific
     * destination's route from the route of the entire nested graph it belongs to.
     */
    val destination: String
}

const val ENTRIES_ROUTE = "entries"
const val FAVORITES_ROUTE = "favorites"
const val LIBRARY_ROUTE = "library"
const val SEARCH_ROUTE = "search"
const val SETTINGS_ROUTE = "settings"

object EntriesListDestination : NavigationDestination {
    override val route = ENTRIES_ROUTE
    override val destination = "list"
}

object EntriesSingleDestination : NavigationDestination {
    override val route = ENTRIES_ROUTE
    override val destination = "single"
    const val dayIdArg = "dayId"
}

object FavoritesDestination : NavigationDestination {
    override val route = FAVORITES_ROUTE
    override val destination = "home"
}

object LibraryHomeDestination : NavigationDestination {
    override val route = LIBRARY_ROUTE
    override val destination = "home"
}

object SearchDestination : NavigationDestination {
    override val route = SEARCH_ROUTE
    override val destination = "home"
}

object SettingsHomeDestination : NavigationDestination {
    override val route = SETTINGS_ROUTE
    override val destination = "home"
}

object SettingsBackupDestination : NavigationDestination {
    override val route = SETTINGS_ROUTE
    override val destination = "backup"
}

object SettingsNotificationsDestination :
    NavigationDestination {
    override val route = SETTINGS_ROUTE
    override val destination = "notifications"
}

object SettingsThemingDestination : NavigationDestination {
    override val route = SETTINGS_ROUTE
    override val destination = "theming"
}