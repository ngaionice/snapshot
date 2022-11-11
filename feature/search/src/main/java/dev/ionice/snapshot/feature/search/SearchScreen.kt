package dev.ionice.snapshot.feature.search

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.navigation.Navigator
import dev.ionice.snapshot.core.ui.components.DateRangePickerDialog
import dev.ionice.snapshot.core.ui.screens.ErrorScreen
import dev.ionice.snapshot.core.ui.screens.LoadingScreen
import dev.ionice.snapshot.feature.search.components.*
import kotlinx.coroutines.launch

@Composable
internal fun SearchRoute(viewModel: SearchViewModel = hiltViewModel(), navigator: Navigator) {

    val uiState by viewModel.uiState.collectAsState()

    SearchScreen(
        uiStateProvider = { uiState },
        setSearchString = viewModel::setSearchString,
        setFilters = viewModel::setFilters,
        onSearch = viewModel::search,
        onSelectEntry = navigator::navigateToEntry,
        onBack = navigator::navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@VisibleForTesting
@Composable
internal fun SearchScreen(
    uiStateProvider: () -> SearchUiState,
    setSearchString: (String) -> Unit,
    setFilters: (Filters) -> Unit,
    onSearch: () -> Unit,
    onSelectEntry: (Long) -> Unit,
    onBack: () -> Unit
) {
    val (hasSearched, setHasSearched) = remember { mutableStateOf(false) }
    val uiState = uiStateProvider()
    val (searchString, searchHistory, filters) = uiState
    val (isSearchBarActive, setIsSearchBarActive) = remember { mutableStateOf(true) }

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val (modalSheetFilterType, setModalSheetFilterType) = remember { mutableStateOf(FilterType.DATE) }
    val (showDateRangePicker, setShowDateRangePicker) = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    Scaffold(topBar = {
        SearchBar(
            searchString = searchString,
            setSearchString = setSearchString,
            isActive = isSearchBarActive,
            setIsActive = setIsSearchBarActive,
            onBack = onBack
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            SearchFilters(
                filters = filters,
                onTriggerBottomSheet = { type ->
                    setModalSheetFilterType(type)
                    focusRequester.requestFocus()
                    coroutineScope.launch {
                        modalSheetState.show()
                    }
                }
            )

            SearchContent(
                searchString = searchString,
                recentSearches = searchHistory.reversed(),
                quickResultsProvider = { uiState.quickResults },
                fullResultsProvider = { uiState.fullResultsUiState },
                state = if (isSearchBarActive) SearchContentState.INPUT else SearchContentState.RESULTS,
                onSearch = { string ->
                    setSearchString(string)
                    onSearch()
                    setIsSearchBarActive(false)
                    setHasSearched(true)
                },
                onSelectEntry = onSelectEntry
            )
        }
    }

    FiltersBottomSheet(
        filters = filters,
        setFilters = setFilters,
        onShowDateRangePicker = { setShowDateRangePicker(true) },
        onCloseSheet = { coroutineScope.launch { modalSheetState.hide() } },
        sheetState = modalSheetState,
        focusRequester = focusRequester,
        contentType = modalSheetFilterType,
        locationsUiStateProvider = { uiState.locationsUiState },
        tagsUiStateProvider = { uiState.tagsUiState }
    )

    if (showDateRangePicker) {
        DateRangePickerDialog(
            onCancel = { setShowDateRangePicker(false) },
            onConfirm = { start, end ->
                setFilters(filters.copy(dateFilter = DateFilter.Custom(start, end)))
                setShowDateRangePicker(false)
            }
        )
    }

    BackHandler(enabled = hasSearched) {
        if (isSearchBarActive) setIsSearchBarActive(false)
        else onBack()
    }
}

@Composable
private fun SearchContent(
    searchString: String,
    recentSearches: List<String>,
    quickResultsProvider: () -> List<Day>,
    fullResultsProvider: () -> ResultsUiState,
    state: SearchContentState,
    onSearch: (String) -> Unit,
    onSelectEntry: (Long) -> Unit
) {
    Column {
        when (state) {
            SearchContentState.INPUT -> {
                if (searchString.length > 1) {
                    SearchButton(searchString = searchString, onSearch = { onSearch(searchString) })
                } else {
                    SearchHistory(recentSearches = recentSearches, onSearch = onSearch)
                }

                QuickResults(
                    searchString = searchString,
                    resultsProvider = quickResultsProvider,
                    onSelectEntry = onSelectEntry
                )
            }
            SearchContentState.RESULTS -> {
                when (val fullResults = fullResultsProvider()) {
                    is ResultsUiState.Loading -> {
                        LoadingScreen(message = stringResource(R.string.search_loading_msg))
                    }
                    is ResultsUiState.Error -> {
                        ErrorScreen(message = stringResource(R.string.search_error_msg))
                    }
                    is ResultsUiState.Success -> {
                        FullResults(
                            searchString = searchString,
                            results = fullResults.data,
                            onSelectEntry = onSelectEntry
                        )
                    }
                }
            }
        }
    }
}

private enum class SearchContentState {
    INPUT,
    RESULTS
}