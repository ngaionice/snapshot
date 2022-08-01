package me.ionice.snapshot.ui.days

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.day.Day
import me.ionice.snapshot.data.day.DayRepository
import me.ionice.snapshot.data.day.DayWithMetrics
import java.time.LocalDate

class DayListViewModel(
    private val dayRepository: DayRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(DayListViewModelState(loading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        val today = LocalDate.now()
        switchYear(today.year)

        // load weekly entries
        viewModelScope.launch {
            val currentDayId = today.toEpochDay()
            val weekStartDayId = currentDayId - today.dayOfWeek.value
            dayRepository.getDaysFlow(weekStartDayId, currentDayId).collect { days ->
                viewModelState.update {
                    it.copy(weekEntries = days)
                }
            }
        }

        // load memories
        viewModelScope.launch {
            val memories = dayRepository.getDaysOfDate(today.month.value, today.dayOfMonth)
            viewModelState.update {
                it.copy(memories = memories)
            }
        }
    }

    fun switchYear(year: Int) {
        viewModelState.update { it.copy(listYear = year, loading = true) }

        viewModelScope.launch {
            val startDay = LocalDate.of(year, 1, 1).toEpochDay()
            val endDay = LocalDate.of(year, 12, 31).toEpochDay()
            val data = dayRepository.getDays(startDay, endDay)
            viewModelState.update { it.copy(yearEntries = data, loading = false) }

            // observe for changes
            dayRepository.getDaysFlow(startDay, endDay)
                .takeWhile { viewModelState.value.listYear == year } // when the year changes, this flow gets cancelled automatically
                .collect { days ->
                    viewModelState.update {
                        it.copy(yearEntries = days)
                    }
                }
        }
    }


    fun insertDay(epochDay: Long) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            var newDay = dayRepository.getDay(epochDay)
            if (newDay == null) {
                newDay = DayWithMetrics(Day(id = epochDay), emptyList())
                dayRepository.upsertDay(newDay)
            }

            viewModelState.update {
                it.copy(loading = false)
            }
        }
    }

    fun setQuery(query: DaySearchQuery) {
        viewModelState.update {
            it.copy(searchQuery = query)
        }
    }

    fun search(query: DaySearchQuery) {
        viewModelState.update {
            it.copy(
                searchQuery = query,
                // TODO: make it suspend + run query method when available
                searchResults = emptyList())
        }
    }

    fun clearSearch() {
        viewModelState.update {
            it.copy(searchQuery = null)
        }
    }

    companion object {
        fun provideFactory(
            dayRepository: DayRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DayListViewModel(dayRepository) as T
                }
            }
    }
}

/**
 * Internal representation of the [DayListViewModel] state.
 */
data class DayListViewModelState(
    val loading: Boolean,
    val listYear: Int = LocalDate.now().year,
    val weekEntries: List<DayWithMetrics> = emptyList(),
    val yearEntries: List<DayWithMetrics> = emptyList(),
    val memories: List<DayWithMetrics> = emptyList(),
    val searchQuery: DaySearchQuery? = null,
    val searchResults: List<DayWithMetrics> = emptyList()
) {

    fun toUiState(): DayListUiState {
        if (loading) return DayListUiState.Loading
        if (searchQuery == null) {
            return DayListUiState.Overview(
                year = listYear,
                weekEntries = weekEntries,
                yearEntries = yearEntries,
                memories = memories
            )
        }

        val quickResults = yearEntries.filter { entry ->
            entry.core.summary.contains(
                searchQuery.searchTerm,
                true
            )
        }
        return DayListUiState.Search(
            query = searchQuery,
            quickResults = quickResults.slice(0..minOf(4, quickResults.size - 1)),
            // TODO: update when results searching become available
            fullResults = searchResults
        )
    }
}

/**
 * An object representing the properties of a search against Day entries.
 */
data class DaySearchQuery(
    val yearRange: Int?, // TODO: remove when ListOld is deleted
    val searchTerm: String = "",
    val dateRange: Pair<LocalDate, LocalDate> = Pair(LocalDate.MIN, LocalDate.MAX),
    val locations: List<String> = emptyList()
) {
    companion object {
        fun initialize(): DaySearchQuery = DaySearchQuery(yearRange = null)
    }
}

/**
 * UI state for the List screen.
 *
 * Derived from [DayListViewModelState], but separated into different classes to more accurately reflect the state of the screen
 */
sealed interface DayListUiState {

    object Loading : DayListUiState

    data class Overview(
        val year: Int,
        val weekEntries: List<DayWithMetrics>,
        val yearEntries: List<DayWithMetrics>,
        val memories: List<DayWithMetrics>
    ) : DayListUiState

    data class Search(
        val query: DaySearchQuery,
        val quickResults: List<DayWithMetrics>,
        val fullResults: List<DayWithMetrics>
    ) : DayListUiState
}
