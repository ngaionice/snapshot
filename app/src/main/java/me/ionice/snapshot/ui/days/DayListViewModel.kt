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
            val weekStartDayId = currentDayId - (today.dayOfWeek.value - 1)
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

    fun search(query: DaySearchQuery) {
        viewModelState.update {
            it.copy(query = query)
        }
    }

    fun clearSearch() {
        viewModelState.update {
            it.copy(query = null)
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
    val query: DaySearchQuery? = null
) {

    fun toUiState(): DayListUiState = if (query == null) {
        DayListUiState(
            loading = loading,
            year = listYear,
            weekEntries = weekEntries,
            yearEntries = yearEntries,
            memories = memories
        )
    } else {
        DayListUiState(
            loading = loading,
            year = listYear,
            weekEntries = weekEntries,
            yearEntries = yearEntries.filter {
                it.core.summary.contains(query.querySummaryString, true)
            },
            memories = memories
        )
    }
}

/**
 * An object representing the properties of a search against Day entries.
 */
data class DaySearchQuery(
    val yearRange: Int?, // null if searching all
    val querySummaryString: String
    // potentially add filters down the road?
)

/**
 * UI state for the List screen.
 */
data class DayListUiState(
    val loading: Boolean,
    val year: Int,
    val weekEntries: List<DayWithMetrics>,
    val yearEntries: List<DayWithMetrics>,
    val memories: List<DayWithMetrics>
)