package dev.ionice.snapshot.core.navigation

import androidx.navigation.NavHostController

interface Navigator {
    fun navigateBack()

    fun navigateToEntry(entryId: Long)

    fun navigateToTag(tagId: Long)

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

    override fun navigateToTag(tagId: Long) {
        navController.navigate("${TagsSingleDestination.route}/${TagsSingleDestination.destination}/$tagId")
    }

    override fun navigateToLocation() {
        TODO("Not yet implemented")
    }

    override fun navigateToDestination(destination: NavigationDestination) {
        navController.navigate("${destination.route}/${destination.destination}")
    }
}