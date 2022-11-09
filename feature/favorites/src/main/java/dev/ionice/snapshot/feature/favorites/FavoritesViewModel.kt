package dev.ionice.snapshot.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ionice.snapshot.core.common.Result
import dev.ionice.snapshot.core.common.asResult
import dev.ionice.snapshot.core.data.repository.DayRepository
import dev.ionice.snapshot.core.model.Day
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    dayRepository: DayRepository
) : ViewModel() {
    val uiState: StateFlow<FavoritesUiState> = getUiStateFlow(dayRepository)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState.Loading
        )
}

private fun getUiStateFlow(
    dayRepository: DayRepository
): Flow<FavoritesUiState> = dayRepository.getListFlowForFavorites().asResult().map {
    when (it) {
        is Result.Error -> FavoritesUiState.Error
        is Result.Loading -> FavoritesUiState.Loading
        else -> FavoritesUiState.Success(
            (it as Result.Success).data
        )
    }
}

sealed interface FavoritesUiState {
    object Loading : FavoritesUiState
    object Error : FavoritesUiState
    data class Success(
        val entries: List<Day>
    ) : FavoritesUiState
}