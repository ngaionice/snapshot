package dev.ionice.snapshot.core.ui

import dev.ionice.snapshot.core.database.model.DayEntity
import dev.ionice.snapshot.core.database.model.LocationPropertiesEntity
import dev.ionice.snapshot.core.database.model.TagPropertiesEntity

sealed interface DayUiState {
    object Loading : DayUiState
    object Error : DayUiState
    data class Success(val data: DayEntity?) : DayUiState
}

sealed interface DaysUiState {
    object Loading : DaysUiState
    object Error : DaysUiState
    data class Success(val data: List<DayEntity>) : DaysUiState
}

sealed interface LocationsUiState {
    object Loading : LocationsUiState
    object Error : LocationsUiState
    data class Success(val data: List<LocationPropertiesEntity>) : LocationsUiState
}

sealed interface TagsUiState {
    object Loading : TagsUiState
    object Error : TagsUiState
    data class Success(val data: List<TagPropertiesEntity>) : TagsUiState
}