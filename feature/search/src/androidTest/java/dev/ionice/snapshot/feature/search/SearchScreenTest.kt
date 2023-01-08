package dev.ionice.snapshot.feature.search

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import com.google.common.truth.Truth.assertThat
import dev.ionice.snapshot.testtools.data.database.repository.FRD
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class SearchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun searchBar_onClick_activatesTextField() {
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase },
                setSearchString = { },
                setFilters = { },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getTagNode(R.string.tt_search_bar_base).performClick()
        getTagNode(R.string.tt_search_bar_text_field).assertIsFocused()
    }

    @Test
    fun searchBar_onClearBtnClick_clearsSearchString() {
        var searchString = nonEmptySearchString
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase.copy(searchString = searchString) },
                setSearchString = { searchString = it },
                setFilters = { },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getCdNode(R.string.cd_search_bar_clear).performClick()
        assertThat(searchString).isEmpty()
    }

    @Test
    fun searchButton_onEmptySearchString_getsHidden() {
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase },
                setSearchString = { },
                setFilters = { },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getCdNode(R.string.cd_search_action).assertDoesNotExist()
    }

    @Test
    fun searchBar_onSearchBtnClick_getsDeactivated() {
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase.copy(searchString = nonEmptySearchString) },
                setSearchString = { },
                setFilters = { },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getTagNode(R.string.tt_search_bar_base).performClick()
        getTagNode(R.string.tt_search_button).performClick()
        getTagNode(R.string.tt_search_bar_text_field).assertDoesNotExist()
    }

    @Test
    fun searchHistory_onSelectSuggestion_setsSearchStringAndSearches() {
        var searchString = ""
        var executedSearch: String? = null
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase },
                setSearchString = { searchString = it },
                setFilters = { },
                onSearch = { executedSearch = searchString },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getCdNode(R.string.tt_recent_search_button, history[0]).performClick()
        assertThat(searchString).isEqualTo(history[0])
        assertThat(executedSearch).isEqualTo(history[0])
    }

    @Test
    fun searchHistory_onNonEmptySearchString_getsHidden() {
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase.copy(searchString = nonEmptySearchString) },
                setSearchString = { },
                setFilters = { },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getTextNode(R.string.recent_searches_header).assertDoesNotExist()
    }

    @Test
    fun dateBottomSheet_onSelectPresetFilter_setsFilter() {
        var filters = Filters()
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase },
                setSearchString = { },
                setFilters = { filters = it },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getCdNode(R.string.cd_filter_date).performClick()
        getTextNode(R.string.date_at_least_one_week).performClick()

        assertThat(filters.dateFilter).isEqualTo(DateFilter.OlderThan.ONE_WEEK)
    }

    @Test
    fun dateBottomSheet_onSelectCustomFilterThenSelectRange_setsFilter() {
        var filters = Filters()
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase },
                setSearchString = { },
                setFilters = { filters = it },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getCdNode(R.string.cd_filter_date).performClick()
        getTextNode(R.string.date_custom_range).performClick()

        getCdNode(dev.ionice.snapshot.core.ui.R.string.cd_date_range_start_text_field).performTextInput(
            "20011010"
        )
        getCdNode(dev.ionice.snapshot.core.ui.R.string.cd_date_range_end_text_field).performTextInput(
            "20011011"
        )
        getTextNode(dev.ionice.snapshot.core.ui.R.string.button_ok).performClick()

        assertThat(filters.dateFilter).isEqualTo(
            DateFilter.Custom(
                LocalDate.of(2001, 10, 10),
                LocalDate.of(2001, 10, 11)
            )
        )
    }

    @Test
    fun locationBottomSheet_onNonEmptySearchText_filtersLocations() {
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase.copy(locationsUiState = locationUiStateSuccess) },
                setSearchString = { },
                setFilters = { },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getCdNode(R.string.cd_filter_location).performClick()
        getCdNode(R.string.cd_bottom_sheet_locations_filter_text_field).performTextInput(locations[2].name)

        composeTestRule.onAllNodes(isToggleable()).assertCountEquals(1)
    }

    @Test
    fun locationBottomSheet_onSelectFilter_setsFilter() {
        var filters = Filters()
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase.copy(locationsUiState = locationUiStateSuccess) },
                setSearchString = { },
                setFilters = { filters = it },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getCdNode(R.string.cd_filter_location).performClick()
        composeTestRule.onNodeWithText(locations[2].name).performClick()

        assertThat(filters.locationFilters).contains(locations[2])
    }

    @Test
    fun tagBottomSheet_onSelectFilter_setsFilter() {
        var filters = Filters()
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = { uiStateBase.copy(tagsUiState = tagsUiStateSuccess) },
                setSearchString = { },
                setFilters = { filters = it },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getCdNode(R.string.cd_filter_tag).performClick()
        composeTestRule.onNodeWithText(tags[2].name).performClick()

        assertThat(filters.tagFilters).contains(tags[2])
    }

    @Test
    fun quickResults_onSelectEntry_callsOnSelectEntry() {
        var selectedEntry: Long? = null
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = {
                    uiStateBase.copy(
                        searchString = nonEmptySearchString,
                        quickResults = results
                    )
                },
                setSearchString = { },
                setFilters = { },
                onSearch = { },
                onSelectEntry = { selectedEntry = it },
                onBack = { }
            )
        }
        val entry = results[0]
        composeTestRule.onNodeWithText(entry.summary).performClick()
        assertThat(selectedEntry).isEqualTo(entry.id)
    }

    @Test
    fun fullResults_onSelectEntry_callsOnSelectEntry() {
        var selectedEntry: Long? = null
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = {
                    uiStateBase.copy(
                        searchString = nonEmptySearchString,
                        fullResultsUiState = ResultsUiState.Success(results)
                    )
                },
                setSearchString = { },
                setFilters = { },
                onSearch = { },
                onSelectEntry = { selectedEntry = it },
                onBack = { }
            )
        }
        getTagNode(R.string.tt_search_bar_base).performClick()
        getTagNode(R.string.tt_search_button).performClick()

        val entry = results[0]
        composeTestRule.onNodeWithText(entry.summary).performClick()
        assertThat(selectedEntry).isEqualTo(entry.id)
    }

    @Test
    fun searchScreen_onBackAfterSearchBarActivationAfterPreviousSearch_showsPreviousResults() {
        composeTestRule.setContent {
            SearchScreen(
                uiStateProvider = {
                    uiStateBase.copy(
                        searchString = nonEmptySearchString,
                        fullResultsUiState = ResultsUiState.Success(results)
                    )
                },
                setSearchString = { },
                setFilters = { },
                onSearch = { },
                onSelectEntry = { },
                onBack = { }
            )
        }
        getTagNode(R.string.tt_search_bar_base).performClick()
        getTagNode(R.string.tt_search_button).performClick()

        getTagNode(R.string.tt_search_bar_base).performClick()

        Espresso.pressBack()
        composeTestRule.onNodeWithText(results[0].summary).assertIsDisplayed()
    }

    private fun getString(@StringRes resId: Int, vararg args: String) =
        composeTestRule.activity.resources.getString(resId, *args)

    private fun getTextNode(@StringRes resId: Int, vararg args: String) =
        composeTestRule.onNodeWithText(getString(resId, *args))

    private fun getCdNode(@StringRes resId: Int, vararg args: String) =
        composeTestRule.onNodeWithContentDescription(getString(resId, *args))

    private fun getTagNode(@StringRes resId: Int, vararg args: String) =
        composeTestRule.onNodeWithTag(getString(resId, *args))

    private val emptySearchString = ""
    private val nonEmptySearchString = "new"

    private val history = listOf("one", "two", "new")

    private val locations = FRD.locationSourceData
    private val tags = FRD.tagSourceData

    private val locationUiStateSuccess = LocationsUiState.Success(locations)
    private val tagsUiStateSuccess = TagsUiState.Success(tags)

    private val results =
        FRD.daySourceData.filter { it.summary.contains(nonEmptySearchString, ignoreCase = true) }

    private val uiStateBase = SearchUiState(
        emptySearchString,
        history,
        Filters(),
        emptyList(),
        ResultsUiState.Loading,
        LocationsUiState.Loading,
        TagsUiState.Loading
    )
}