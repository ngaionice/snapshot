package me.ionice.snapshot.ui.entries.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.database.repository.DayRepository
import me.ionice.snapshot.ui.common.DaysUiState
import me.ionice.snapshot.utils.Result
import me.ionice.snapshot.utils.asResult
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class EntriesListViewModel @Inject constructor(
    private val dayRepository: DayRepository
) : ViewModel() {

    private val today = MutableStateFlow(LocalDate.now().toEpochDay())
    private val year = MutableStateFlow(LocalDate.now().year)

    val uiState: StateFlow<EntriesListUiState> = combine(year, today.flatMapLatest {
        dayRepository.getListFlowInIdRange(today.value - 6, today.value).asResult()
    }, year.flatMapLatest {
        dayRepository.getListFlowByYear(it).asResult()
    }) { year, weekEntriesResult, yearEntriesResult ->
        val weekEntries = when (weekEntriesResult) {
            is Result.Loading -> DaysUiState.Loading
            is Result.Success -> DaysUiState.Success(weekEntriesResult.data)
            is Result.Error -> DaysUiState.Error
        }
        val yearEntries = when (yearEntriesResult) {
            is Result.Loading -> DaysUiState.Loading
            is Result.Success -> DaysUiState.Success(yearEntriesResult.data)
            is Result.Error -> DaysUiState.Error
        }
        EntriesListUiState(year, weekEntries, yearEntries)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = EntriesListUiState(year.value, DaysUiState.Loading, DaysUiState.Loading)
    )

    fun changeViewingYear(year: Int) {
        this.year.update { year }
    }

    fun addEntry(dayId: Long) {
        viewModelScope.launch { dayRepository.create(dayId) }
    }
}

data class EntriesListUiState(
    val year: Int, val weekUiState: DaysUiState, val yearUiState: DaysUiState
)



