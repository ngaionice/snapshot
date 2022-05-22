package me.ionice.snapshot.ui.day

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

class DayViewModel(
    private val dayRepository: DayRepository,
    private val metricRepository: MetricRepository
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(DayViewModelState(loading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        switchDay(LocalDate.now().toEpochDay())

        // observe the flow and update keys whenever they change
        viewModelScope.launch {
            metricRepository.observeKeys().collect { keys ->
                viewModelState.update { it.copy(metricKeys = keys) }
            }
        }
    }

    fun addDay(epochDay: Long) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val newDay = DayWithMetrics(Day(id = epochDay), emptyList())
            dayRepository.upsertDay(newDay)

            viewModelState.update {
                DayViewModelState(epochDay = epochDay, mDay = newDay, loading = false)
            }
        }
    }

    fun switchDay(epochDay: Long) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            // save the current data if it exists
            if (viewModelState.value.mDay != null) {
                dayRepository.upsertDay(viewModelState.value.mDay!!)
            }

            val data = dayRepository.getDay(epochDay)
            viewModelState.update {
                if (data == null) {
                    DayViewModelState(epochDay = epochDay, loading = false)
                } else {
                    DayViewModelState(epochDay = epochDay, mDay = data, loading = false)
                }
            }
        }
    }

    fun setSummary(value: String) {
        if (viewModelState.value.mDay != null) {
            viewModelState.update {
                it.copy(mDay = it.mDay!!.copy(day = it.mDay.day.copy(summary = value)))
            }
        }
    }

    fun setLocation(value: String) {
        if (viewModelState.value.mDay != null) {
            viewModelState.update {
                it.copy(mDay = it.mDay!!.copy(day = it.mDay.day.copy(location = value)))
            }
        }
    }

    fun addMetric(entry: MetricEntry) {
        if (viewModelState.value.mDay != null) {
            viewModelState.update {
                it.copy(mDay = it.mDay!!.copy(metrics = it.mDay.metrics + entry))
            }
        }
    }

    fun removeMetric(entry: MetricEntry) {
        if (viewModelState.value.mDay != null) {
            viewModelState.update {
                it.copy(mDay = it.mDay!!.copy(metrics = it.mDay.metrics - entry))
            }
        }
    }

    fun updateMetric(index: Int, newValue: String) {
        if (viewModelState.value.mDay != null) {
            viewModelState.update {
                it.copy(mDay = it.mDay!!.copy(metrics = it.mDay.metrics.mapIndexed { idx, entry ->
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
                    return DayViewModel(dayRepository, metricRepository) as T
                }
            }
    }
}

/**
 * Internal representation of the [DayViewModel] state.
 */
data class DayViewModelState(
    val epochDay: Long? = null,
    val mDay: DayWithMetrics? = null,
    val loading: Boolean,
    val metricKeys: List<MetricKey> = emptyList()
) {

    fun toUiState(): DayUiState =
        if (epochDay == null || mDay == null) {
            DayUiState.NotAvailable(epochDay = epochDay, loading = loading)
        } else {
            DayUiState.Available(
                loading = loading,
                epochDay = epochDay,
                location = mDay.day.location,
                summary = mDay.day.summary,
                metrics = mDay.metrics,
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
    val epochDay: Long?

    /**
     * No data is available.
     */
    data class NotAvailable(
        override val loading: Boolean,
        override val epochDay: Long?,
    ) : DayUiState

    /**
     * Data is available.
     */
    data class Available(
        override val loading: Boolean,
        override val epochDay: Long,
        val location: String,
        val summary: String,
        val metrics: List<MetricEntry>,
        val metricKeys: List<MetricKey>
    ) : DayUiState
}