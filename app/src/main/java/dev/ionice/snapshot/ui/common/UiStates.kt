package dev.ionice.snapshot.ui.common

import dev.ionice.snapshot.core.database.model.Day
import dev.ionice.snapshot.core.database.model.LocationProperties
import dev.ionice.snapshot.core.database.model.TagProperties

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
    data class Success(val data: List<LocationProperties>) : LocationsUiState
}

sealed interface TagsUiState {
    object Loading : TagsUiState
    object Error : TagsUiState
    data class Success(val data: List<TagProperties>) : TagsUiState
}