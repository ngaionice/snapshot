package me.ionice.snapshot.ui.navigation

import androidx.navigation.NavHostController
import me.ionice.snapshot.ui.entries.EntriesSingleDestination

interface Navigator {
    fun navigateBack()

    fun navigateToEntry(entryId: Long)

    fun navigateToLocation()

    fun navigateToDestination(destination: NavigationDestination)
}

class NavigatorImpl(private val navController: NavHostController) : Navigator {

    override fun navigateBack() {
        navController.popBackStack()
    }

    override fun navigateToEntry(entryId: Long) {
        navController.navigate("${EntriesSingleDestination.route}/${EntriesSingleDestination.destination}/$entryId")
    }

    override fun navigateToLocation() {
        TODO("Not yet implemented")
    }

    override fun navigateToDestination(destination: NavigationDestination) {
        navController.navigate("${destination.route}/${destination.destination}")
    }
}