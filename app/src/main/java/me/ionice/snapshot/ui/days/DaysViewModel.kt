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
        MutableStateFlow(DayViewModelState(loading = true))

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

        changeYear(LocalDate.now().year)
    }

    fun changeYear(year: Int) {
        viewModelState.update { it.copy(listYear = year, loading = true) }

        viewModelScope.launch {
            val startDay = LocalDate.of(year, 1, 1).toEpochDay()
            val endDay = LocalDate.of(year, 12, 31).toEpochDay()
            val data = dayRepository.getDays(startDay, endDay)
            viewModelState.update { it.copy(entries = data, loading = false) }

            // observe for changes
            dayRepository.observeDays(startDay, endDay)
                .takeWhile { viewModelState.value.listYear == year } // when the year changes, this flow gets cancelled automatically
                .collect { days ->
                    viewModelState.update {
                        it.copy(entries = days)
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
                it.copy(selectedDate = epochDay, selectedEntry = newDay, loading = false)
            }
        }
    }

    fun selectDay(epochDay: Long) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val data = dayRepository.getDay(epochDay)
            viewModelState.update {
                it.copy(selectedDate = epochDay, selectedEntry = data, loading = false)
            }
        }
    }

    fun deselectDay() {
        viewModelState.update { it.copy(selectedDate = null, selectedEntry = null) }
    }

    fun saveDay() {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            if (viewModelState.value.selectedEntry != null) {
                dayRepository.upsertDay(viewModelState.value.selectedEntry!!)
            }
            viewModelState.update { it.copy(loading = false) }
        }
    }

    fun setSummary(value: String) {
        if (viewModelState.value.selectedEntry != null) {
            viewModelState.update {
                it.copy(
                    selectedEntry = it.selectedEntry!!.copy(
                        day = it.selectedEntry.day.copy(
                            summary = value
                        )
                    )
                )
            }
        }
    }

    fun setLocation(value: String) {
        if (viewModelState.value.selectedEntry != null) {
            viewModelState.update {
                it.copy(
                    selectedEntry = it.selectedEntry!!.copy(
                        day = it.selectedEntry.day.copy(
                            location = value
                        )
                    )
                )
            }
        }
    }

    fun addMetric(entry: MetricEntry) {
        if (viewModelState.value.selectedEntry != null) {
            viewModelState.update {
                it.copy(selectedEntry = it.selectedEntry!!.copy(metrics = it.selectedEntry.metrics + entry))
            }
        }
    }

    fun removeMetric(entry: MetricEntry) {
        if (viewModelState.value.selectedEntry != null) {
            viewModelState.update {
                it.copy(selectedEntry = it.selectedEntry!!.copy(metrics = it.selectedEntry.metrics - entry))
            }
        }
    }

    fun updateMetric(index: Int, newValue: String) {
        if (viewModelState.value.selectedEntry != null) {
            viewModelState.update {
                it.copy(selectedEntry = it.selectedEntry!!.copy(metrics = it.selectedEntry.metrics.mapIndexed { idx, entry ->
                    if (idx == index) entry.copy(value = newValue) else entry
                }))
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
    val selectedDate: Long? = null,
    val selectedEntry: DayWithMetrics? = null,
    val entries: List<DayWithMetrics> = emptyList(),
    val metricKeys: List<MetricKey> = emptyList()
) {

    fun toUiState(): DayUiState =
        if (selectedDate == null) {
            DayUiState.DayList(loading = loading, year = listYear, entries = entries)
        } else if (selectedEntry == null) {
            DayUiState.DayEntryNotFound(loading = loading, date = selectedDate)
        } else {
            DayUiState.DayEntryFound(
                loading = loading,
                date = selectedDate,
                location = selectedEntry.day.location,
                summary = selectedEntry.day.summary,
                metrics = selectedEntry.metrics,
                metricKeys = metricKeys
            )
        }
}

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