package me.ionice.snapshot.ui.entries.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.database.model.Coordinates
import me.ionice.snapshot.data.database.model.Day
import me.ionice.snapshot.data.database.repository.DayRepository
import me.ionice.snapshot.data.database.repository.LocationRepository
import me.ionice.snapshot.data.database.repository.TagRepository
import me.ionice.snapshot.ui.common.DayUiState
import me.ionice.snapshot.ui.common.LocationsUiState
import me.ionice.snapshot.ui.common.TagsUiState
import me.ionice.snapshot.utils.Result
import me.ionice.snapshot.utils.asResult

@OptIn(ExperimentalCoroutinesApi::class)
class EntriesSingleViewModel(
    private val dayRepository: DayRepository,
    private val locationRepository: LocationRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val dayId = MutableStateFlow<Long?>(null)
    private val mutableDayCopy = MutableStateFlow<Day?>(null)

    val uiState: StateFlow<EntriesSingleUiState> = combine(dayId,
        dayId.flatMapLatest { dayId -> dayId?.let { dayRepository.getFlow(it) } ?: emptyFlow() }
            .asResult(),
        locationRepository.getAllPropertiesFlow().asResult(),
        tagRepository.getAllPropertiesFlow().asResult(),
        mutableDayCopy) { dayId, dayResult, locationResult, tagResult, dayCopy ->
        val dayState = when (dayResult) {
            is Result.Loading -> DayUiState.Loading
            is Result.Error -> DayUiState.Error
            is Result.Success -> DayUiState.Success(dayResult.data)
        }
        val locationState = when (locationResult) {
            is Result.Loading -> LocationsUiState.Loading
            is Result.Error -> LocationsUiState.Error
            is Result.Success -> LocationsUiState.Success(locationResult.data)
        }
        val tagState = when (tagResult) {
            is Result.Loading -> TagsUiState.Loading
            is Result.Error -> TagsUiState.Error
            is Result.Success -> TagsUiState.Success(tagResult.data)
        }
        EntriesSingleUiState(dayId, dayState, locationState, tagState, dayCopy)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = EntriesSingleUiState(
            dayId.value, DayUiState.Loading, LocationsUiState.Loading, TagsUiState.Loading, null
        )
    )

    fun load(dayId: Long) {
        this.dayId.value = dayId
    }

    fun favorite(isFavorite: Boolean) {
        val dayUiState = uiState.value.dayUiState
        if (dayUiState is DayUiState.Success && dayUiState.data != null && dayUiState.data.properties.isFavorite != isFavorite) {
            val (properties, tags, location) = dayUiState.data
            val (id, summary) = properties
            viewModelScope.launch { dayRepository.update(id, summary, isFavorite, location, tags) }
        }
    }

    fun edit(day: Day?) {
        mutableDayCopy.value = day
    }

    fun save() {
        mutableDayCopy.value?.let {
            val (properties, tags, location) = it
            val (id, summary, _, _, isFavorite) = properties
            viewModelScope.launch { dayRepository.update(id, summary, isFavorite, location, tags) }
        }
    }

    fun addLocation(name: String, coordinates: Coordinates) {
        viewModelScope.launch { locationRepository.add(coordinates, name) }
    }

    fun addTag(name: String) {
        viewModelScope.launch { tagRepository.add(name) }
    }

    companion object {
        fun provideFactory(
            dayRepository: DayRepository,
            locationRepository: LocationRepository,
            tagRepository: TagRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EntriesSingleViewModel(
                    dayRepository, locationRepository, tagRepository
                ) as T
            }
        }
    }
}

data class EntriesSingleUiState(
    val dayId: Long?,
    val dayUiState: DayUiState,
    val locationsUiState: LocationsUiState,
    val tagsUiState: TagsUiState,
    val editingCopy: Day?
)