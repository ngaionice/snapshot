//package me.ionice.snapshot.ui.metrics
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import me.ionice.snapshot.data.database.v1.metric.MetricKey
//import me.ionice.snapshot.data.database.v1.metric.MetricRepository
//
//class MetricListViewModel(private val repository: MetricRepository) : ViewModel() {
//
//    private val viewModelState = MutableStateFlow(MetricListViewModelState(loading = true))
//
//    val uiState = viewModelState
//        .map { it.toUiState() }
//        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())
//
//    init {
//        viewModelScope.launch {
//            // fetch keys once
//            val initialKeys = repository.getKeys()
//            viewModelState.update {
//                it.copy(keys = initialKeys, loading = false)
//            }
//
//            // observe keys
//            repository.getKeysFlow().collect { keys ->
//                viewModelState.update {
//                    it.copy(keys = keys)
//                }
//            }
//        }
//    }
//
//    fun addKey(name: String) {
//        viewModelState.update { it.copy(loading = true) }
//
//        viewModelScope.launch {
//            repository.insertKey(name)
//            viewModelState.update { it.copy(loading = false) }
//        }
//    }
//
//    companion object {
//        fun provideFactory(metricRepository: MetricRepository): ViewModelProvider.Factory =
//            object : ViewModelProvider.Factory {
//                @Suppress("UNCHECKED_CAST")
//                override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                    return MetricListViewModel(metricRepository) as T
//                }
//            }
//    }
//}
//
//data class MetricListViewModelState(
//    val keys: List<MetricKey> = emptyList(),
//    val loading: Boolean
//) {
//    fun toUiState(): MetricListUiState = if (loading) {
//        MetricListUiState.Loading
//    } else {
//        MetricListUiState.Loaded(keys)
//    }
//
//}
//
//sealed interface MetricListUiState {
//
//    object Loading : MetricListUiState
//
//    data class Loaded(
//        val keys: List<MetricKey>
//    ) : MetricListUiState
//}