package me.ionice.snapshot.ui.entries.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.database.model.Day
import me.ionice.snapshot.data.database.repository.DayRepository
import me.ionice.snapshot.utils.Result
import me.ionice.snapshot.utils.asResult
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class EntriesListViewModel(private val dayRepository: DayRepository) : ViewModel() {

    private val today = MutableStateFlow(LocalDate.now().toEpochDay())
    private val year = MutableStateFlow(LocalDate.now().year)

    val uiState: StateFlow<EntriesListUiState> = combine(year, today.flatMapLatest {
        dayRepository.getListFlowInIdRange(today.value - 6, today.value).asResult()
    }, year.flatMapLatest {
        dayRepository.getListFlowByYear(it).asResult()
    }) { year, weekEntriesResult, yearEntriesResult ->
        val weekEntries = when (weekEntriesResult) {
            is Result.Loading -> WeekUiState.Loading
            is Result.Success -> WeekUiState.Success(weekEntriesResult.data)
            is Result.Error -> WeekUiState.Error
        }
        val yearEntries = when (yearEntriesResult) {
            is Result.Loading -> YearUiState.Loading
            is Result.Success -> YearUiState.Success(yearEntriesResult.data)
            is Result.Error -> YearUiState.Error
        }
        EntriesListUiState(year, weekEntries, yearEntries)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = EntriesListUiState(year.value, WeekUiState.Loading, YearUiState.Loading)
    )

    fun changeViewingYear(year: Int) {
        this.year.update { year }
    }

    fun addEntry(dayId: Long) {
        viewModelScope.launch { dayRepository.create(dayId) }
    }

    companion object {
        fun provideFactory(dayRepository: DayRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EntriesListViewModel(dayRepository) as T
                }
            }
    }
}

sealed interface WeekUiState {
    object Loading : WeekUiState
    object Error : WeekUiState
    data class Success(val entries: List<Day>) : WeekUiState
}

sealed interface YearUiState {
    object Loading : YearUiState
    object Error : YearUiState
    data class Success(val entries: List<Day>) : YearUiState
}

data class EntriesListUiState(
    val year: Int, val weekUiState: WeekUiState, val yearUiState: YearUiState
)



