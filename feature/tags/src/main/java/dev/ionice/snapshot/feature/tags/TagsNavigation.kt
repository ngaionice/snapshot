package dev.ionice.snapshot.feature.tags

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.ionice.snapshot.core.navigation.NavigatorImpl
import dev.ionice.snapshot.core.navigation.TAGS_ROUTE
import dev.ionice.snapshot.core.navigation.TagsListDestination
import dev.ionice.snapshot.core.navigation.TagsSingleDestination
import dev.ionice.snapshot.core.ui.animationDurationMs
import dev.ionice.snapshot.feature.tags.list.TagsListRoute
import dev.ionice.snapshot.feature.tags.single.TagsSingleRoute

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.tagsGraph(navController: NavHostController) {
    navigation(route = TAGS_ROUTE,
        startDestination = "${TagsListDestination.route}/${TagsListDestination.destination}",
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
        composable(route = "${TagsListDestination.route}/${TagsListDestination.destination}") {
            TagsListRoute(navigator = navigator)
        }
        composable(route = "${TagsSingleDestination.route}/${TagsSingleDestination.destination}/{${TagsSingleDestination.tagIdArg}}",
            arguments = listOf(
                navArgument(TagsSingleDestination.tagIdArg) { type = NavType.LongType }
            )) {
            TagsSingleRoute(
                tagId = it.arguments!!.getLong(TagsSingleDestination.tagIdArg),
                navigator = navigator
            )
        }
    }
}