package me.ionice.snapshot.ui.days

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.day.Day
import me.ionice.snapshot.data.day.DayRepository
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.data.metric.MetricRepository
import java.time.LocalDate

class DayListViewModel(
    private val dayRepository: DayRepository,
    private val metricRepository: MetricRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(DayListViewModelState(loading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        switchYear(LocalDate.now().year)
    }

    fun switchYear(year: Int) {
        viewModelState.update { it.copy(listYear = year, loading = true) }

        viewModelScope.launch {
            val startDay = LocalDate.of(year, 1, 1).toEpochDay()
            val endDay = LocalDate.of(year, 12, 31).toEpochDay()
            val data = dayRepository.getDays(startDay, endDay)
            viewModelState.update { it.copy(allEntries = data, loading = false) }

            // observe for changes
            dayRepository.getDaysFlow(startDay, endDay)
                .takeWhile { viewModelState.value.listYear == year } // when the year changes, this flow gets cancelled automatically
                .collect { days ->
                    viewModelState.update {
                        it.copy(allEntries = days)
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
            dayRepository: DayRepository,
            metricRepository: MetricRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DayListViewModel(dayRepository, metricRepository) as T
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
    val allEntries: List<DayWithMetrics> = emptyList(),
    val metricKeys: List<MetricKey> = emptyList(),
    val query: DaySearchQuery? = null
) {

    fun toUiState(): DayListUiState = if (query == null) {
        DayListUiState(loading = loading, year = listYear, entries = allEntries)
    } else {
        DayListUiState(
            loading = loading,
            year = listYear,
            entries = allEntries.filter {
                it.day.summary.contains(query.querySummaryString, true)
            })
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
 * UI state for the Day screen.
 *
 * Derived from [DayListViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
data class DayListUiState(
    val loading: Boolean,
    val year: Int,
    val entries: List<DayWithMetrics>
)