package dev.ionice.snapshot.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ionice.snapshot.core.common.Result
import dev.ionice.snapshot.core.common.asResult
import dev.ionice.snapshot.core.data.repository.DayRepository
import dev.ionice.snapshot.core.data.repository.LocationRepository
import dev.ionice.snapshot.core.data.repository.PreferencesRepository
import dev.ionice.snapshot.core.data.repository.TagRepository
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.model.Location
import dev.ionice.snapshot.core.model.Tag
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dayRepository: DayRepository,
    locationRepository: LocationRepository,
    tagRepository: TagRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val searchStringFlow = MutableStateFlow("")
    private val filtersFlow = MutableStateFlow(Filters())
    private val currentYearEntries = MutableStateFlow(emptyList<Day>())
    private val fullResultsFlow = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)

    private val locationsFlow = locationRepository.getAllFlow()
    private val tagsFlow = tagRepository.getAllFlow()

    private val filterSourcesFlow = combine(locationsFlow, tagsFlow, ::Pair).asResult()
    private val searchHistoryFlow = preferencesRepository.getRecentSearchesFlow().asResult()

    init {
        viewModelScope.launch {
            currentYearEntries.update {
                dayRepository.getListFlowByYear(LocalDate.now().year).first()
            }
        }
    }

    val uiState: StateFlow<SearchUiState> = combine(
        searchStringFlow, filtersFlow, fullResultsFlow, filterSourcesFlow, searchHistoryFlow
    ) { searchString, filters, fullResults, filterSourcesResult, historyResult ->
        val quickResults = if (searchString.isEmpty()) {
            emptyList()
        } else {
            currentYearEntries.value.filter {
                it.summary.contains(searchString, ignoreCase = true) &&
                        quickResultFilter(filters, it)
            }
        }
        val (locations, tags) = when (filterSourcesResult) {
            is Result.Loading -> Pair(LocationsUiState.Loading, TagsUiState.Loading)
            is Result.Error -> Pair(LocationsUiState.Error, TagsUiState.Error)
            is Result.Success -> Pair(
                LocationsUiState.Success(filterSourcesResult.data.first),
                TagsUiState.Success(filterSourcesResult.data.second)
            )
        }
        val history = when (historyResult) {
            is Result.Loading -> emptyList()
            is Result.Error -> emptyList()
            is Result.Success -> historyResult.data.searches
        }

        SearchUiState(
            searchString = searchString,
            searchHistory = history,
            filters = filters,
            quickResults = quickResults,
            fullResultsUiState = fullResults,
            locationsUiState = locations,
            tagsUiState = tags
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState(
            searchString = searchStringFlow.value,
            searchHistory = emptyList(),
            filters = filtersFlow.value,
            quickResults = emptyList(),
            fullResultsUiState = fullResultsFlow.value,
            locationsUiState = LocationsUiState.Loading,
            tagsUiState = TagsUiState.Loading
        )
    )

    fun search() {
        val currFilters = filtersFlow.value
        val dateFilter = currFilters.dateFilter.getDates()
        fullResultsFlow.update { ResultsUiState.Loading }
        viewModelScope.launch {
            preferencesRepository.insertRecentSearch(searchStringFlow.value)
            val data = dayRepository.search(
                queryString = searchStringFlow.value,
                startDayId = dateFilter.first,
                endDayId = dateFilter.second,
                includedLocations = filtersFlow.value.locationFilters.ifEmpty { null },
                includedTags = filtersFlow.value.tagFilters.ifEmpty { null }
            )
            fullResultsFlow.update { ResultsUiState.Success(data) }
        }
    }

    fun setSearchString(searchString: String) {
        this.searchStringFlow.update { searchString }
    }

    fun setFilters(filters: Filters) {
        this.filtersFlow.update { filters }
        if (searchStringFlow.value.isNotEmpty()) search()
    }
}

data class SearchUiState(
    val searchString: String,
    val searchHistory: List<String>,
    val filters: Filters,
    val quickResults: List<Day>,
    val fullResultsUiState: ResultsUiState,
    val locationsUiState: LocationsUiState,
    val tagsUiState: TagsUiState
)

data class Filters(
    val dateFilter: DateFilter = DateFilter.Any,
    val locationFilters: Set<Location> = emptySet(),
    val tagFilters: Set<Tag> = emptySet()
)

sealed interface DateFilter {
    object Any : DateFilter {
        override fun getDates() = Pair(null, null)
    }

    enum class OlderThan : DateFilter {
        ONE_WEEK {
            override fun getDates() = Pair(null, LocalDate.now().minusWeeks(1).toEpochDay())
        },
        ONE_MONTH {
            override fun getDates() = Pair(null, LocalDate.now().minusMonths(1).toEpochDay())
        },
        SIX_MONTHS {
            override fun getDates() = Pair(null, LocalDate.now().minusMonths(6).toEpochDay())
        },
        ONE_YEAR {
            override fun getDates() = Pair(null, LocalDate.now().minusYears(1).toEpochDay())
        }
    }

    data class Custom(val startDate: LocalDate, val endDate: LocalDate) : DateFilter {
        override fun getDates() = Pair(startDate.toEpochDay(), endDate.toEpochDay())
    }

    /**
     * @return Pair(start, endInclusive)
     */
    fun getDates(): Pair<Long?, Long?>
}

sealed interface ResultsUiState {
    object Error : ResultsUiState
    object Loading : ResultsUiState
    data class Success(val data: List<Day>) : ResultsUiState
}

sealed interface LocationsUiState {
    object Error : LocationsUiState
    object Loading : LocationsUiState
    data class Success(val data: List<Location>) : LocationsUiState
}

sealed interface TagsUiState {
    object Error : TagsUiState
    object Loading : TagsUiState
    data class Success(val data: List<Tag>) : TagsUiState
}

private fun quickResultFilter(filters: Filters, input: Day): Boolean {
    val locationIds = filters.locationFilters.map { it.id }
    val tagIds = filters.tagFilters.map { it.id }
    val dateFilter: (Day) -> Boolean = { day ->
        val dateRange = filters.dateFilter.getDates()
        if (dateRange.first == null && dateRange.second == null) true
        else if (dateRange.first == null) day.id <= dateRange.second!!
        else if (dateRange.second == null) dateRange.first!! <= day.id
        else dateRange.first!! <= day.id && day.id <= dateRange.second!!
    }
    val locationFilter: (Day) -> Boolean = { day ->
        locationIds.isEmpty() || locationIds.contains(day.location?.id)
    }
    val tagFilter: (Day) -> Boolean = { day ->
        tagIds.isEmpty() || day.tags.any { tagIds.contains(it.tag.id) }
    }
    return dateFilter(input) && locationFilter(input) && tagFilter(input)
}