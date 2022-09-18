package me.ionice.snapshot.ui.library

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import me.ionice.snapshot.data.database.repository.DayRepository
import me.ionice.snapshot.data.database.repository.LocationRepository
import me.ionice.snapshot.data.database.repository.TagRepository
import me.ionice.snapshot.ui.common.animationDurationMs
import me.ionice.snapshot.ui.navigation.NavigationDestination
import me.ionice.snapshot.ui.navigation.NavigatorImpl

const val LIBRARY_ROUTE = "library"

object LibraryHomeDestination : NavigationDestination {
    override val route = LIBRARY_ROUTE
    override val destination = "home"
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.libraryGraph(
    navController: NavHostController,
    dayRepository: DayRepository,
    locationRepository: LocationRepository,
    tagRepository: TagRepository
) {
    navigation(route = LibraryHomeDestination.route,
        startDestination = "${LibraryHomeDestination.route}/${LibraryHomeDestination.destination}",
        enterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Left, tween(
                    animationDurationMs
                )
            )
        },
        exitTransition = { fadeOut(tween(animationDurationMs)) },
        popEnterTransition = { fadeIn(tween(animationDurationMs)) },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Right, tween(
                    animationDurationMs
                )
            )
        }
    ) {
        val navigator = NavigatorImpl(navController)
        composable(
            route = "${LibraryHomeDestination.route}/${LibraryHomeDestination.destination}",
            enterTransition = { fadeIn(tween(animationDurationMs)) },
            popExitTransition = { fadeOut(tween(animationDurationMs)) }) {
            LibraryRoute(
                viewModel = viewModel(
                    factory = LibraryViewModel.provideFactory(
                        dayRepository,
                        locationRepository,
                        tagRepository
                    )
                ), navigator = navigator
            )
        }
    }
}