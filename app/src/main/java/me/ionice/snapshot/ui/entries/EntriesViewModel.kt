package me.ionice.snapshot.ui.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.day.Day
import me.ionice.snapshot.data.day.DayRepository
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.utils.Result
import me.ionice.snapshot.utils.asResult
import java.time.LocalDate

class EntriesViewModel(private val dayRepository: DayRepository) : ViewModel() {

    private val today = LocalDate.now()
    private val todayId = today.toEpochDay()
    private var year = MutableStateFlow(today.year)

    private val weekEntriesStream =
        dayRepository.getDaysFlow(todayId - 6, todayId).asResult()
    private val yearEntriesStream =
        dayRepository.getDaysFlow(todayId + 1 - today.dayOfYear, todayId)
            .asResult()

    val uiState: StateFlow<EntriesUiState> =
        combine(
            year,
            weekEntriesStream,
            yearEntriesStream
        ) { year, weekEntriesResult, yearEntriesResult ->
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
            EntriesUiState(year, weekEntries, yearEntries)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = EntriesUiState(year.value, WeekUiState.Loading, YearUiState.Loading)
        )

    fun changeViewingYear(year: Int) {
        this.year.update { year }
    }

    fun addEntry(epochDay: Long) {
        viewModelScope.launch {
            var newDay = dayRepository.getDay(epochDay)
            if (newDay == null) {
                newDay = DayWithMetrics(Day(id = epochDay), emptyList())
                dayRepository.upsertDay(newDay)
            }
        }
    }
}

sealed interface WeekUiState {
    object Loading : WeekUiState
    object Error : WeekUiState
    data class Success(val entries: List<DayWithMetrics>) : WeekUiState
}

sealed interface YearUiState {
    object Loading : YearUiState
    object Error : YearUiState
    data class Success(val entries: List<DayWithMetrics>) : YearUiState
}

data class EntriesUiState(
    val year: Int,
    val weekUiState: WeekUiState,
    val yearUiState: YearUiState
)



