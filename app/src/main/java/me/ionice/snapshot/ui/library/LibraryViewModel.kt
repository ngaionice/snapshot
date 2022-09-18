package me.ionice.snapshot.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import me.ionice.snapshot.data.database.repository.DayRepository
import me.ionice.snapshot.data.database.repository.LocationRepository
import me.ionice.snapshot.data.database.repository.TagRepository
import me.ionice.snapshot.ui.common.DaysUiState
import me.ionice.snapshot.ui.common.LocationsUiState
import me.ionice.snapshot.ui.common.TagsUiState
import me.ionice.snapshot.utils.Result
import me.ionice.snapshot.utils.asResult
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModel @Inject constructor(
    private val dayRepository: DayRepository,
    locationRepository: LocationRepository,
    tagRepository: TagRepository
) : ViewModel() {

    private val today = MutableStateFlow(LocalDate.now())
    private val locationFlow = locationRepository.getAllPropertiesFlow().asResult()
    private val tagsFlow = tagRepository.getRecentlyUsedFlow().asResult()

    val uiState: StateFlow<LibraryUiState> = combine(
        today.flatMapLatest {
            dayRepository.getListFlowByDayOfYear(it.monthValue, it.dayOfMonth).asResult()
        }, locationFlow, tagsFlow
    ) { memoriesResult, locationResult, tagsResult ->
        val memoriesState = when (memoriesResult) {
            is Result.Loading -> DaysUiState.Loading
            is Result.Error -> DaysUiState.Error
            is Result.Success -> DaysUiState.Success(memoriesResult.data)
        }
        val locationState = when (locationResult) {
            is Result.Loading -> LocationsUiState.Loading
            is Result.Error -> LocationsUiState.Error
            is Result.Success -> LocationsUiState.Success(locationResult.data)
        }
        val metricsState = when (tagsResult) {
            is Result.Loading -> TagsUiState.Loading
            is Result.Error -> TagsUiState.Error
            is Result.Success -> TagsUiState.Success(tagsResult.data)
        }
        LibraryUiState(memoriesState, locationState, metricsState)
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = LibraryUiState(
            DaysUiState.Loading, LocationsUiState.Loading, TagsUiState.Loading
        )
    )
}

data class LibraryUiState(
    val memoriesUiState: DaysUiState,
    val locationsUiState: LocationsUiState,
    val tagsUiState: TagsUiState
)