package dev.ionice.snapshot.feature.favorites

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.ionice.snapshot.testtools.data.database.repository.FRD
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun screen_whenUiStateIsLoading_showsLoading() {
        // Arrange
        composeTestRule.setContent {
            FavoritesScreen(
                uiStateProvider = { FavoritesUiState.Loading },
                onSelectEntry = {},
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(getString(R.string.tt_favorites_loading)).assertIsDisplayed()
    }

    @Test
    fun screen_whenUiStateIsError_showsError() {
        // Arrange
        composeTestRule.setContent {
            FavoritesScreen(
                uiStateProvider = { FavoritesUiState.Error },
                onSelectEntry = {},
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText(getString(R.string.favorites_load_error_message))
            .assertIsDisplayed()
    }

    @Test
    fun list_whenNoFavorites_showsPlaceholder() {
        // Arrange
        composeTestRule.setContent {
            FavoritesScreen(
                uiStateProvider = {
                    FavoritesUiState.Success(
                        entries = emptyList()
                    )
                },
                onSelectEntry = {},
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText(getString(R.string.tt_favorites_none)).assertIsDisplayed()
    }

    @Test
    fun list_whenHasFavorites_showsEntries() {
        // Arrange
        composeTestRule.setContent {
            FavoritesScreen(
                uiStateProvider = {
                    FavoritesUiState.Success(
                        entries = FRD.daySourceData.filter { it.isFavorite }
                    )
                },
                onSelectEntry = {},
                onBack = {}
            )
        }

        // Assert
        composeTestRule.onAllNodesWithTag(getString(R.string.tt_favorites_entry))
            .assertCountEquals(FRD.daySourceData.filter { it.isFavorite }.size)
    }

    private fun getString(@StringRes resId: Int) =
        composeTestRule.activity.resources.getString(resId)
}