package dev.ionice.snapshot.core.ui

import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.model.Location
import dev.ionice.snapshot.core.model.Tag

sealed interface DayUiState {
    object Loading : DayUiState
    object Error : DayUiState
    data class Success(val data: Day?) : DayUiState
}

sealed interface DaysUiState {
    object Loading : DaysUiState
    object Error : DaysUiState
    data class Success(val data: List<Day>) : DaysUiState
}

sealed interface LocationsUiState {
    object Loading : LocationsUiState
    object Error : LocationsUiState
    data class Success(val data: List<Location>) : LocationsUiState
}

sealed interface TagsUiState {
    object Loading : TagsUiState
    object Error : TagsUiState
    data class Success(val data: List<Tag>) : TagsUiState
}