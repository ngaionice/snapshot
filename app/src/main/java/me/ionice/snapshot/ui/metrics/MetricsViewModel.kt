package me.ionice.snapshot.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.metric.Metric
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.data.metric.MetricRepository

class MetricsViewModel(private val repository: MetricRepository) : ViewModel() {

    private val viewModelState = MutableStateFlow(MetricsViewModelState(loading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        viewModelScope.launch {
            // fetch keys once
            val initialKeys = repository.getKeys()
            viewModelState.update {
                it.copy(keys = initialKeys, loading = false)
            }

            // observe keys
            repository.observeKeys().collect { keys ->
                viewModelState.update {
                    it.copy(keys = keys)
                }
            }
        }
    }

    fun addKey(name: String) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            repository.insertKey(name)
            viewModelState.update { it.copy(loading = false) }
        }
    }

    fun selectMetric(key: MetricKey) {
        viewModelState.update { it.copy(loading = true) }

        viewModelScope.launch {
            val metric = repository.getMetric(key)
            viewModelState.update { it.copy(selectedMetric = metric, loading = false) }
        }
    }

    fun deselectMetric() {
        viewModelState.update { it.copy(selectedMetric = null) }
    }

    companion object {
        fun provideFactory(metricRepository: MetricRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MetricsViewModel(metricRepository) as T
                }
            }
    }
}

data class MetricsViewModelState(
    val keys: List<MetricKey> = emptyList(),
    val selectedMetric: Metric? = null,
    val loading: Boolean
) {
    fun toUiState(): MetricsUiState =
        if (selectedMetric == null) {
            MetricsUiState.MetricList(loading, keys)
        } else {
            MetricsUiState.MetricDetails(loading, keys, selectedMetric)
        }
}

sealed interface MetricsUiState {

    val loading: Boolean
    val keys: List<MetricKey>

    data class MetricList(
        override val loading: Boolean,
        override val keys: List<MetricKey>
    ) : MetricsUiState

    data class MetricDetails(
        override val loading: Boolean,
        override val keys: List<MetricKey>,
        val selectedMetric: Metric
    ) : MetricsUiState
}