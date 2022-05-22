package me.ionice.snapshot.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.day.DayRepository
import me.ionice.snapshot.data.day.DayWithMetrics
import java.time.LocalDate

class HistoryViewModel(private val dayRepository: DayRepository) : ViewModel() {

    private val viewModelState = MutableStateFlow(HistoryViewModelState(loading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        changeYear(LocalDate.now().year)
    }

    fun changeYear(year: Int) {
        viewModelState.update { it.copy(year = year, loading = true) }

        viewModelScope.launch {
            dayRepository.observeDays(
                LocalDate.of(year, 1, 1).toEpochDay(),
                LocalDate.of(year, 12, 31).toEpochDay()
            )
                .takeWhile { viewModelState.value.year == year } // when the year changes, this flow gets cancelled automatically
                .collect { days ->
                viewModelState.update {
                    it.copy(days = days, loading = false)
                }
            }
        }
    }

    companion object {
        fun provideFactory(dayRepository: DayRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HistoryViewModel(dayRepository) as T
                }
            }
    }
}

data class HistoryViewModelState(
    val year: Int = LocalDate.now().year, // unused for now, but will be used later
    val days: List<DayWithMetrics> = emptyList(),
    val loading: Boolean
) {

    fun toUiState(): HistoryScreenState = HistoryScreenState(year, days, loading)
}

data class HistoryScreenState(val year: Int, val days: List<DayWithMetrics>, val loading: Boolean)