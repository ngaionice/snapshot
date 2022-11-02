package dev.ionice.snapshot.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ionice.snapshot.core.common.Result
import dev.ionice.snapshot.core.common.asResult
import kotlinx.coroutines.flow.*
import dev.ionice.snapshot.core.database.model.DayEntity
import dev.ionice.snapshot.core.database.model.LocationPropertiesEntity
import dev.ionice.snapshot.core.data.repository.DayRepository
import dev.ionice.snapshot.core.data.repository.LocationRepository
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    dayRepository: DayRepository,
    locationRepository: LocationRepository
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = getUiStateFlow(dayRepository, locationRepository)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState.Loading
        )

}

private fun getUiStateFlow(
    dayRepository: DayRepository,
    locationRepository: LocationRepository
): Flow<FavoritesUiState> = combine(
    dayRepository.getListFlowForFavorites().asResult(),
    locationRepository.getAllPropertiesFlow().asResult()
) { entriesResult, locationsResult ->
    when {
        entriesResult is Result.Error || locationsResult is Result.Error -> FavoritesUiState.Error
        entriesResult is Result.Loading || locationsResult is Result.Loading -> FavoritesUiState.Loading
        else -> FavoritesUiState.Success(
            (entriesResult as Result.Success).data, (locationsResult as Result.Success).data
        )
    }
}

sealed interface FavoritesUiState {
    object Loading : FavoritesUiState
    object Error : FavoritesUiState
    data class Success(
        val entries: List<DayEntity>,
        val locations: List<LocationPropertiesEntity>
    ) : FavoritesUiState
}