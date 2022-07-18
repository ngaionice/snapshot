package me.ionice.snapshot.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.metric.Metric
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.data.metric.MetricRepository

class MetricEntryViewModel(private val repository: MetricRepository) : ViewModel() {

    private val viewModelState = MutableStateFlow(MetricEntryViewModelState(loading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    fun selectMetric(metricId: Long) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val metric = repository.getMetric(metricId)
            viewModelState.update { it.copy(selectedMetric = metric, loading = false) }
        }
    }

    companion object {
        fun provideFactory(metricRepository: MetricRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MetricEntryViewModel(metricRepository) as T
                }
            }
    }
}

data class MetricEntryViewModelState(
    val selectedMetric: Metric? = null,
    val loading: Boolean
) {
    fun toUiState(): MetricEntryUiState = if (loading || selectedMetric == null) {
        MetricEntryUiState.Loading
    } else {
        MetricEntryUiState.Loaded(selectedMetric)
    }

}

sealed interface MetricEntryUiState {

    object Loading : MetricEntryUiState

    data class Loaded(
        val selectedMetric: Metric
    ) : MetricEntryUiState
}