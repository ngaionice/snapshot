//package me.ionice.snapshot.ui.days
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import me.ionice.snapshot.data.database.v2.model.*
//import me.ionice.snapshot.data.database.DayRepository
//import me.ionice.snapshot.data.database.LocationRepository
//import me.ionice.snapshot.data.database.TagRepository
//
//class DayEntryViewModel(
//    private val dayRepository: DayRepository,
//    private val locationRepository: LocationRepository,
//    private val tagRepository: TagRepository
//) : ViewModel() {
//
//    private val viewModelState =
//        MutableStateFlow(DayEntryViewModelState(loading = false, dayId = 0))
//    val uiState = viewModelState
//        .map { it.toUiState() }
//        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())
//
//    init {
//        viewModelScope.launch {
//            // keep latest keys available so users can select them when selecting tags
//            tagRepository.getAllPropertiesFlow().collect { keys ->
//                viewModelState.update { it.copy(availableTags = keys) }
//            }
//        }
//
//        viewModelScope.launch {
//            // keep latest keys available so users can select them when selecting locations
//            locationRepository.getAllPropertiesFlow().collect { keys ->
//                viewModelState.update { it.copy(availableLocations = keys) }
//            }
//        }
//    }
//
//    fun loadDay(dayId: Long) {
//        viewModelState.update { it.copy(loading = true, dayId = dayId) }
//        viewModelScope.launch {
//            var day = dayRepository.get(dayId)
//            if (day == null) {
//                dayRepository.create(dayId)
//                day = dayRepository.get(dayId)
//            }
//            viewModelState.update { it.copy(loading = false, day = day) }
//        }
//    }
//
//    fun insertDay(dayId: Long) {
//        viewModelState.update { it.copy(loading = true) }
//
//        viewModelScope.launch {
//            dayRepository.create(dayId)
//            viewModelState.update {
//                it.copy(loading = false, day = dayRepository.get(dayId))
//            }
//        }
//    }
//
//    /**
//     * Saves the current state of the Day entry in viewModelState to the database.
//     */
//    fun saveDay() {
//        viewModelState.update { it.copy(loading = true) }
//        viewModelScope.launch {
//            viewModelState.value.day?.let {
//                val (properties, tags, location) = it
//                val (dayId, summary, _, _, isFavorite) = properties
//                dayRepository.update(dayId, summary, isFavorite, location, tags)
//            }
//            viewModelState.update { it.copy(loading = false) }
//        }
//    }
//
//    fun setSummary(summary: String) {
//        viewModelState.update {
//            it.copy(
//                day = it.day?.copy(properties = it.day.properties.copy(summary = summary))
//            )
//        }
//    }
//
//    fun setLocation(location: LocationEntry?) {
//        viewModelState.update {
//            it.copy(
//                day = it.day?.copy(location = location)
//            )
//        }
//    }
//
//    fun addTag(entry: TagEntry) {
//        viewModelState.update {
//            it.copy(day = it.day?.copy(tags = it.day.tags + entry))
//        }
//    }
//
//    fun removeTag(entry: TagEntry) {
//        viewModelState.update {
//            it.copy(day = it.day?.copy(tags = it.day.tags - entry))
//        }
//    }
//
//    fun updateTag(index: Int, newValue: String) {
//        viewModelState.update {
//            it.copy(day = it.day?.copy(tags = it.day.tags.mapIndexed { idx, entry ->
//                if (idx == index) entry.copy(content = newValue) else entry
//            }))
//        }
//    }
//
//    companion object {
//        fun provideFactory(
//            dayRepository: DayRepository,
//            locationRepository: LocationRepository,
//            tagRepository: TagRepository
//        ): ViewModelProvider.Factory =
//            object : ViewModelProvider.Factory {
//                @Suppress("UNCHECKED_CAST")
//                override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                    return DayEntryViewModel(dayRepository, locationRepository, tagRepository) as T
//                }
//            }
//    }
//}
//
///**
// * Internal representation of the [DayEntryViewModel] state.
// */
//data class DayEntryViewModelState(
//    val loading: Boolean,
//    val dayId: Long,
//    val day: Day? = null,
//    val availableLocations: List<LocationProperties> = emptyList(),
//    val availableTags: List<TagProperties> = emptyList()
//) {
//    fun toUiState(): DayEntryUiState = if (loading) {
//        DayEntryUiState.Loading(dayId = dayId)
//    } else if (day == null) {
//        DayEntryUiState.EntryNotFound(dayId = dayId)
//    } else {
//        DayEntryUiState.EntryFound(
//            dayId = dayId,
//            day = day,
//            availableLocations = availableLocations,
//            availableTags = availableTags
//        )
//    }
//}
//
///**
// * UI state for the Day screen.
// *
// * Derived from [DayEntryViewModelState], but split into two possible subclasses to more
// * precisely represent the state available to render the UI.
// */
//sealed interface DayEntryUiState {
//
//    val dayId: Long
//
//    data class Loading(
//        override val dayId: Long
//    ) : DayEntryUiState
//
//    /**
//     * When an entry is available for the specified date.
//     */
//    data class EntryFound(
//        override val dayId: Long,
//        val day: Day,
//        val availableLocations: List<LocationProperties>,
//        val availableTags: List<TagProperties>
//    ) : DayEntryUiState
//
//    /**
//     * When an entry is not available for the specified date.
//     */
//    data class EntryNotFound(
//        override val dayId: Long
//    ) : DayEntryUiState
//}