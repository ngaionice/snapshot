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

class DayEntryViewModel(
    private val dayRepository: DayRepository,
    private val metricRepository: MetricRepository
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(DayEntryViewModelState(loading = false, dayId = 0))
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        viewModelScope.launch {
            // keep latest keys available so users can select them when adding metrics
            metricRepository.getKeysFlow().collect { keys ->
                viewModelState.update { it.copy(metricKeys = keys) }
            }
        }
    }

    fun loadDay(dayId: Long) {
        viewModelState.update { it.copy(loading = true, dayId = dayId) }
        viewModelScope.launch {
            val day = dayRepository.getDay(dayId)
            viewModelState.update { it.copy(loading = false, day = day) }
        }
    }

    fun insertDay(dayId: Long) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            var newDay = dayRepository.getDay(dayId)
            if (newDay == null) {
                newDay = DayWithMetrics(Day(id = dayId), emptyList())
                dayRepository.upsertDay(newDay)
            }

            viewModelState.update {
                it.copy(loading = false, day = newDay)
            }
        }
    }

    /**
     * Saves the current state of the Day entry in viewModelState to the database.
     */
    fun saveDay() {
        viewModelState.update { it.copy(loading = true) }
        viewModelScope.launch {
            viewModelState.value.day?.let { dayRepository.upsertDay(it) }
            viewModelState.update { it.copy(loading = false) }
        }
    }

    fun setSummary(summary: String) {
        viewModelState.update {
            it.copy(
                day = it.day?.copy(day = it.day.day.copy(summary = summary))
            )
        }
    }

    fun setLocation(location: String) {
        viewModelState.update {
            it.copy(
                day = it.day?.copy(day = it.day.day.copy(location = location))
            )
        }
    }

    fun addMetric(entry: MetricEntry) {
        viewModelState.update {
            it.copy(day = it.day?.copy(metrics = it.day.metrics + entry))
        }
    }

    fun removeMetric(entry: MetricEntry) {
        viewModelState.update {
            it.copy(day = it.day?.copy(metrics = it.day.metrics - entry))
        }
    }

    fun updateMetric(index: Int, newValue: String) {
        viewModelState.update {
            it.copy(day = it.day?.copy(metrics = it.day.metrics.mapIndexed { idx, entry ->
                if (idx == index) entry.copy(value = newValue) else entry
            }))
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
                    return DayEntryViewModel(dayRepository, metricRepository) as T
                }
            }
    }
}

/**
 * Internal representation of the [DayEntryViewModel] state.
 */
data class DayEntryViewModelState(
    val loading: Boolean,
    val dayId: Long,
    val day: DayWithMetrics? = null,
    val metricKeys: List<MetricKey> = emptyList()
) {
    fun toUiState(): DayEntryUiState = if (loading) {
        DayEntryUiState.Loading(dayId = dayId)
    } else if (day == null) {
        DayEntryUiState.EntryNotFound(dayId = dayId)
    } else {
        DayEntryUiState.EntryFound(
            dayId = dayId,
            summary = day.day.summary,
            location = day.day.location,
            metrics = day.metrics,
            metricKeys = metricKeys
        )
    }
}

/**
 * UI state for the Day screen.
 *
 * Derived from [DayEntryViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface DayEntryUiState {

    val dayId: Long

    data class Loading(
        override val dayId: Long
    ) : DayEntryUiState

    /**
     * When an entry is available for the specified date.
     */
    data class EntryFound(
        override val dayId: Long,
        val summary: String,
        val location: String,
        val metrics: List<MetricEntry>,
        val metricKeys: List<MetricKey>
    ) : DayEntryUiState

    /**
     * When an entry is not available for the specified date.
     */
    data class EntryNotFound(
        override val dayId: Long
    ) : DayEntryUiState
}