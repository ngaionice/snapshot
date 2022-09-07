//package me.ionice.snapshot.ui.days
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import me.ionice.snapshot.data.database.model.DayProperties
//import me.ionice.snapshot.data.database.DayRepository
//import java.time.LocalDate
//
//class DayListViewModel(
//    private val dayRepository: DayRepository
//) : ViewModel() {
//
//    private val viewModelState = MutableStateFlow(DayListViewModelState(loading = true))
//
//    val uiState = viewModelState
//        .map { it.toUiState() }
//        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())
//
//    init {
//        val today = LocalDate.now()
//        switchYear(today.year)
//
//        // load weekly entries
//        viewModelScope.launch {
//            val currentDayId = today.toEpochDay()
//            val weekStartDayId = currentDayId - today.dayOfWeek.value
//            dayRepository.getListFlowInIdRange(weekStartDayId, currentDayId).collect { days ->
//                viewModelState.update {
//                    it.copy(weekEntries = days)
//                }
//            }
//        }
//    }
//
//    fun switchYear(year: Int) {
//        viewModelState.update { it.copy(listYear = year, loading = true) }
//
//        viewModelScope.launch {
//            val data = dayRepository.getListFlowByYear(year).first()
//            viewModelState.update { it.copy(yearEntries = data, loading = false) }
//
//            // observe for changes
//            dayRepository.getListFlowByYear(year)
//                .takeWhile { viewModelState.value.listYear == year } // when the year changes, this flow gets cancelled automatically
//                .collect { days ->
//                    viewModelState.update {
//                        it.copy(yearEntries = days)
//                    }
//                }
//        }
//    }
//
//
//    fun insertDay(epochDay: Long) {
//        viewModelState.update { it.copy(loading = true) }
//
//        viewModelScope.launch {
//            if (dayRepository.get(epochDay) == null) {
//                dayRepository.create(epochDay)
//            }
//            viewModelState.update {
//                it.copy(loading = false)
//            }
//        }
//    }
//
//    fun setScreenMode(mode: DayListViewModelState.ScreenMode) {
//        viewModelState.update {
//            when (mode) {
//                DayListViewModelState.ScreenMode.OVERVIEW -> {
//                    it.copy(screenMode = mode, searchQuery = null)
//                }
//                DayListViewModelState.ScreenMode.SEARCH_OPTIONS -> {
//                    it.copy(screenMode = mode, searchQuery = DaySearchQuery())
//                }
//                DayListViewModelState.ScreenMode.SEARCH_RESULTS -> {
//                    if (it.searchQuery == null) throw IllegalArgumentException("Screen mode set to SEARCH_RESULTS when query is null.")
//                    // TODO: initiate search in coroutine scope
//                    it.copy(screenMode = mode)
//                }
//            }
//        }
//    }
//
//    fun setQuery(query: DaySearchQuery) {
//        viewModelState.update {
//            it.copy(searchQuery = query)
//        }
//    }
//
//    companion object {
//        fun provideFactory(
//            dayRepository: DayRepository
//        ): ViewModelProvider.Factory =
//            object : ViewModelProvider.Factory {
//                @Suppress("UNCHECKED_CAST")
//                override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                    return DayListViewModel(dayRepository) as T
//                }
//            }
//    }
//}
//
///**
// * Internal representation of the [DayListViewModel] state.
// */
//data class DayListViewModelState(
//    val loading: Boolean,
//    val screenMode: ScreenMode = ScreenMode.OVERVIEW,
//    val listYear: Int = LocalDate.now().year,
//    val weekEntries: List<DayProperties> = emptyList(),
//    val yearEntries: List<DayProperties> = emptyList(),
//    val searchQuery: DaySearchQuery? = null,
//    val searchResults: List<DayProperties> = emptyList()
//) {
//
//    enum class ScreenMode {
//        OVERVIEW,
//        SEARCH_OPTIONS,
//        SEARCH_RESULTS
//    }
//
//    fun toUiState(): DayListUiState {
//        if (loading) return DayListUiState.Loading
//        return when (screenMode) {
//            ScreenMode.OVERVIEW -> {
//                DayListUiState.Overview(
//                    year = listYear,
//                    weekEntries = weekEntries,
//                    yearEntries = yearEntries,
//                )
//            }
//            ScreenMode.SEARCH_OPTIONS -> {
//                if (searchQuery == null) throw IllegalArgumentException("ScreenMode set to SEARCH_OPTIONS but query is null.")
//                val quickResults = yearEntries.filter { entry ->
//                    entry.summary.contains(
//                        searchQuery.searchTerm,
//                        true
//                    )
//                }
//                DayListUiState.Search.Options(
//                    query = searchQuery,
//                    quickResults = quickResults.slice(0..minOf(4, quickResults.size - 1)),
//                )
//            }
//            ScreenMode.SEARCH_RESULTS -> {
//                if (searchQuery == null) throw IllegalArgumentException("ScreenMode set to SEARCH_RESULTS but query is null.")
//                DayListUiState.Search.Results(
//                    query = searchQuery,
//                    results = searchResults
//                )
//            }
//        }
//    }
//}
//
///**
// * An object representing the properties of a search against Day entries.
// */
//data class DaySearchQuery(
//    val searchTerm: String = "",
//    val dateRange: DateRange = DateRange.Any,
//    val locations: List<String> = emptyList()
//) {
//    sealed interface DateRange {
//
//        fun getDateRange(): Pair<LocalDate, LocalDate>
//
//        object Any : DateRange {
//            override fun getDateRange() = Pair(LocalDate.MIN, LocalDate.MAX)
//        }
//
//        object OneMonthPlus : DateRange {
//            override fun getDateRange() = Pair(LocalDate.MIN, LocalDate.now().minusMonths(1))
//        }
//
//        object ThreeMonthsPlus : DateRange {
//            override fun getDateRange() = Pair(LocalDate.MIN, LocalDate.now().minusMonths(3))
//        }
//
//        object SixMonthsPlus : DateRange {
//            override fun getDateRange() = Pair(LocalDate.MIN, LocalDate.now().minusMonths(6))
//        }
//
//        object OneYearPlus : DateRange {
//            override fun getDateRange() = Pair(LocalDate.MIN, LocalDate.now().minusYears(1))
//        }
//
//        data class Custom(val startDate: LocalDate, val endDate: LocalDate) : DateRange {
//            override fun getDateRange() = Pair(startDate, endDate)
//        }
//    }
//}
//
///**
// * UI state for the List screen.
// *
// * Derived from [DayListViewModelState], but separated into different classes to more accurately reflect the state of the screen
// */
//sealed interface DayListUiState {
//
//    object Loading : DayListUiState
//
//    data class Overview(
//        val year: Int,
//        val weekEntries: List<DayProperties>,
//        val yearEntries: List<DayProperties>
//    ) : DayListUiState
//
//    sealed interface Search : DayListUiState {
//
//        val query: DaySearchQuery
//
//        data class Options(
//            override val query: DaySearchQuery,
//            val quickResults: List<DayProperties>
//        ) : Search
//
//        data class Results(
//            override val query: DaySearchQuery,
//            val results: List<DayProperties>
//        ) : Search
//    }
//}
