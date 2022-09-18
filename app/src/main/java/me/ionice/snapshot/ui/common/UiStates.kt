package me.ionice.snapshot.ui.common

import me.ionice.snapshot.data.database.model.Day
import me.ionice.snapshot.data.database.model.LocationProperties
import me.ionice.snapshot.data.database.model.TagProperties

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