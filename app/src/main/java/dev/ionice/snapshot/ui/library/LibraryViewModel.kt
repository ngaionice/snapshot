package dev.ionice.snapshot.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ionice.snapshot.core.common.Result
import dev.ionice.snapshot.core.common.asResult
import dev.ionice.snapshot.core.data.repository.DayRepository
import dev.ionice.snapshot.core.data.repository.LocationRepository
import dev.ionice.snapshot.core.data.repository.TagRepository
import dev.ionice.snapshot.core.ui.DaysUiState
import dev.ionice.snapshot.core.ui.LocationsUiState
import dev.ionice.snapshot.core.ui.TagsUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState(
            DaysUiState.Loading, LocationsUiState.Loading, TagsUiState.Loading
        )
    )
}

data class LibraryUiState(
    val memoriesUiState: DaysUiState,
    val locationsUiState: LocationsUiState,
    val tagsUiState: TagsUiState
)