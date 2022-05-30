package me.ionice.snapshot.ui.days

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.day.Day
import me.ionice.snapshot.data.day.DayRepository
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.data.metric.MetricRepository
import java.time.LocalDate

class DaysViewModel(
    private val dayRepository: DayRepository,
    private val metricRepository: MetricRepository
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(
            DayViewModelState(
                loading = true,
                subscreen = DayViewModelState.Subscreen.DayList()
            )
        )

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        // observe the flow and update keys whenever they change
        viewModelScope.launch {
            metricRepository.observeKeys().collect { keys ->
                viewModelState.update { it.copy(metricKeys = keys) }
            }
        }

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
            dayRepository.observeDays(startDay, endDay)
                .takeWhile { viewModelState.value.listYear == year } // when the year changes, this flow gets cancelled automatically
                .collect { days ->
                    viewModelState.update {
                        it.copy(allEntries = days)
                    }
                }
        }
    }

    fun addDay(epochDay: Long) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            var newDay = dayRepository.getDay(epochDay)
            if (newDay == null) {
                newDay = DayWithMetrics(Day(id = epochDay), emptyList())
                dayRepository.upsertDay(newDay)
            }

            viewModelState.update {
                it.copy(
                    loading = false,
                    subscreen = DayViewModelState.Subscreen.DayEntry(epochDay, newDay)
                )
            }
        }
    }

    fun selectDay(epochDay: Long) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val data = dayRepository.getDay(epochDay)
            viewModelState.update {
                it.copy(
                    loading = false,
                    subscreen = DayViewModelState.Subscreen.DayEntry(epochDay, data)
                )
            }
        }
    }

    fun deselectDay() {
        // TODO: figure out how to preserve state if previously searching
        viewModelState.update { it.copy(subscreen = DayViewModelState.Subscreen.DayList(query = null)) }
    }

    fun saveDay() {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val subscreen = viewModelState.value.subscreen
            if (subscreen is DayViewModelState.Subscreen.DayEntry && subscreen.entry != null) {
                dayRepository.upsertDay(subscreen.entry)
            }
            viewModelState.update { it.copy(loading = false) }
        }
    }

    fun setSummary(value: String) {
        val subscreen = viewModelState.value.subscreen
        if (subscreen is DayViewModelState.Subscreen.DayEntry && subscreen.entry != null) {
            viewModelState.update {
                it.copy(
                    subscreen = subscreen.copy(
                        entry = subscreen.entry.copy(
                            day = subscreen.entry.day.copy(
                                summary = value
                            )
                        )
                    )
                )
            }
        }
    }

    fun setLocation(value: String) {
        val subscreen = viewModelState.value.subscreen
        if (subscreen is DayViewModelState.Subscreen.DayEntry && subscreen.entry != null) {
            viewModelState.update {
                it.copy(
                    subscreen = subscreen.copy(
                        entry = subscreen.entry.copy(
                            day = subscreen.entry.day.copy(
                                location = value
                            )
                        )
                    )
                )
            }
        }
    }

    fun addMetric(entry: MetricEntry) {
        val subscreen = viewModelState.value.subscreen
        if (subscreen is DayViewModelState.Subscreen.DayEntry && subscreen.entry != null) {
            viewModelState.update {
                it.copy(
                    subscreen = subscreen.copy(entry = subscreen.entry.copy(metrics = subscreen.entry.metrics + entry))
                )
            }
        }
    }

    fun removeMetric(entry: MetricEntry) {
        val subscreen = viewModelState.value.subscreen
        if (subscreen is DayViewModelState.Subscreen.DayEntry && subscreen.entry != null) {
            viewModelState.update {
                it.copy(
                    subscreen = subscreen.copy(entry = subscreen.entry.copy(metrics = subscreen.entry.metrics - entry))
                )
            }
        }
    }

    fun updateMetric(index: Int, newValue: String) {
        val subscreen = viewModelState.value.subscreen
        if (subscreen is DayViewModelState.Subscreen.DayEntry && subscreen.entry != null) {
            viewModelState.update {
                it.copy(
                    subscreen = subscreen.copy(entry = subscreen.entry.copy(metrics = subscreen.entry.metrics.mapIndexed { idx, entry ->
                        if (idx == index) entry.copy(value = newValue) else entry
                    }))
                )
            }
        }
    }

    fun search(query: DaySearchQuery) {
        val subscreen = viewModelState.value.subscreen
        if (subscreen is DayViewModelState.Subscreen.DayList) {
            viewModelState.update {
                it.copy(subscreen = (it.subscreen as DayViewModelState.Subscreen.DayList).copy(query = query))
            }
        }

    }

    fun clearSearch() {
        val subscreen = viewModelState.value.subscreen
        if (subscreen is DayViewModelState.Subscreen.DayList) {
            viewModelState.update {
                it.copy(subscreen = (it.subscreen as DayViewModelState.Subscreen.DayList).copy(query = null))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // TODO: use WorkManager here to save the current day to database if it exists
    }

    companion object {
        fun provideFactory(
            dayRepository: DayRepository,
            metricRepository: MetricRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DaysViewModel(dayRepository, metricRepository) as T
                }
            }
    }
}

/**
 * Internal representation of the [DaysViewModel] state.
 */
data class DayViewModelState(
    val loading: Boolean,
    val listYear: Int = LocalDate.now().year,
    val allEntries: List<DayWithMetrics> = emptyList(),
    val metricKeys: List<MetricKey> = emptyList(),
    val subscreen: Subscreen
) {

    fun toUiState(): DayUiState =
        when (subscreen) {
            is Subscreen.DayList -> {
                if (subscreen.query == null) {
                    DayUiState.DayList(loading = loading, year = listYear, entries = allEntries)
                } else {
                    DayUiState.DayList(
                        loading = loading,
                        year = listYear,
                        entries = allEntries.filter {
                            it.day.summary.contains(
                                subscreen.query.querySummaryString,
                                true
                            )
                        })
                }
            }
            is Subscreen.DayEntry -> {
                if (subscreen.entry == null) {
                    DayUiState.DayEntryNotFound(loading = loading, date = subscreen.date)
                } else {
                    DayUiState.DayEntryFound(
                        loading = loading,
                        date = subscreen.date,
                        location = subscreen.entry.day.location,
                        summary = subscreen.entry.day.summary,
                        metrics = subscreen.entry.metrics,
                        metricKeys = metricKeys
                    )
                }
            }
        }


    /**
     * An interface representing the data needed by currently showing screen in [DaysViewModel].
     *
     * This interface and its implementing classes exists such that
     * a balance can be obtained between memory usage and user experience,
     * where only the values relevant to the current screen are kept in memory,
     * minimizing the memory use by [DaysViewModel].
     */
    sealed interface Subscreen {

        data class DayList(
            val query: DaySearchQuery? = null
        ) : Subscreen

        data class DayEntry(
            val date: Long,
            val entry: DayWithMetrics?
        ) : Subscreen
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
 * Derived from [DayViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface DayUiState {

    val loading: Boolean

    /**
     * For displaying a list of entries.
     */
    data class DayList(
        override val loading: Boolean,
        val year: Int,
        val entries: List<DayWithMetrics>
    ) : DayUiState

    /**
     * When an entry is available for the specified date.
     */
    data class DayEntryFound(
        override val loading: Boolean,
        val date: Long,
        val location: String,
        val summary: String,
        val metrics: List<MetricEntry>,
        val metricKeys: List<MetricKey>
    ) : DayUiState

    /**
     * When an entry cannot be found for the specified date.
     */
    data class DayEntryNotFound(
        override val loading: Boolean,
        val date: Long
    ) : DayUiState
}