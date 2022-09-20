package me.ionice.snapshot.ui.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ionice.snapshot.data.database.model.Coordinates
import me.ionice.snapshot.data.database.model.Day
import me.ionice.snapshot.data.database.repository.DayRepository
import me.ionice.snapshot.data.database.repository.LocationRepository
import me.ionice.snapshot.data.database.repository.TagRepository
import me.ionice.snapshot.ui.common.DayUiState
import me.ionice.snapshot.ui.common.DaysUiState
import me.ionice.snapshot.ui.common.LocationsUiState
import me.ionice.snapshot.ui.common.TagsUiState
import me.ionice.snapshot.utils.Result
import me.ionice.snapshot.utils.asResult
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class EntriesViewModel @Inject constructor(
    private val dayRepository: DayRepository,
    private val locationRepository: LocationRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val dayId = MutableStateFlow<Long?>(null)
    private val mutableDayCopy = MutableStateFlow<Day?>(null)

    private val today = MutableStateFlow(LocalDate.now().toEpochDay())
    private val year = MutableStateFlow(LocalDate.now().year)

    private val dayFlow =
        dayId.flatMapLatest { dayId -> dayId?.let { dayRepository.getFlow(it) } ?: emptyFlow() }
            .asResult()
    private val locationsFlow = locationRepository.getAllPropertiesFlow().asResult()
    private val tagsFlow = tagRepository.getAllPropertiesFlow().asResult()

    private val weekFlow = today.flatMapLatest {
        dayRepository.getListFlowInIdRange(today.value - 6, today.value).asResult()
    }
    private val yearFlow = year.flatMapLatest { dayRepository.getListFlowByYear(it).asResult() }

    private val entryFlow: Flow<EntryState> = combine(
        dayId, dayFlow, locationsFlow, tagsFlow, mutableDayCopy
    ) { dayId, dayResult, locationResult, tagResult, dayCopy ->
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
        EntryState(dayId, dayState, locationState, tagState, dayCopy)
    }

    private val listFlow: Flow<ListState> = combine(
        year, weekFlow, yearFlow
    ) { year, weekResult, yearResult ->
        val weekEntries = when (weekResult) {
            is Result.Loading -> DaysUiState.Loading
            is Result.Success -> DaysUiState.Success(weekResult.data)
            is Result.Error -> DaysUiState.Error
        }
        val yearEntries = when (yearResult) {
            is Result.Loading -> DaysUiState.Loading
            is Result.Success -> DaysUiState.Success(yearResult.data)
            is Result.Error -> DaysUiState.Error
        }
        ListState(year, weekEntries, yearEntries)
    }

    val uiState: StateFlow<EntriesUiState> = combine(
        entryFlow, listFlow.debounce(100)
    ) { entryResult, listResult ->
        val (dayId, dayState, locationsState, tagsState, dayCopy) = entryResult
        val (year, weekState, yearState) = listResult
        EntriesUiState(
            dayId = dayId,
            year = year,
            editingCopy = dayCopy,
            dayUiState = dayState,
            weekUiState = weekState,
            yearUiState = yearState,
            locationsUiState = locationsState,
            tagsUiState = tagsState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EntriesUiState(
            dayId = dayId.value,
            year = year.value,
            editingCopy = null,
            dayUiState = DayUiState.Loading,
            weekUiState = DaysUiState.Loading,
            yearUiState = DaysUiState.Loading,
            locationsUiState = LocationsUiState.Loading,
            tagsUiState = TagsUiState.Loading
        )
    )

    fun changeListYear(year: Int) {
        this.year.update { year }
    }

    fun add(dayId: Long) {
        viewModelScope.launch { dayRepository.create(dayId) }
    }

    fun load(dayId: Long) {
        this.dayId.update { dayId }
    }

    fun edit(day: Day?) {
        mutableDayCopy.update { day }
    }

    fun save() {
        mutableDayCopy.value?.let {
            val (properties, tags, location) = it
            val (id, summary, _, _, isFavorite) = properties
            viewModelScope.launch { dayRepository.update(id, summary, isFavorite, location, tags) }
        }
    }

    fun favorite(isFavorite: Boolean) {
        val dayUiState = uiState.value.dayUiState
        if (dayUiState is DayUiState.Success && dayUiState.data != null && dayUiState.data.properties.isFavorite != isFavorite) {
            val (properties, tags, location) = dayUiState.data
            val (id, summary) = properties
            viewModelScope.launch { dayRepository.update(id, summary, isFavorite, location, tags) }
        }
    }

    fun addLocation(name: String, coordinates: Coordinates) {
        viewModelScope.launch { locationRepository.add(coordinates, name) }
    }

    fun addTag(name: String) {
        viewModelScope.launch { tagRepository.add(name) }
    }
}

private data class ListState(
    val year: Int, val weekUiState: DaysUiState, val yearUiState: DaysUiState
)

private data class EntryState(
    val dayId: Long?,
    val dayUiState: DayUiState,
    val locationsUiState: LocationsUiState,
    val tagsUiState: TagsUiState,
    val editingCopy: Day?
)

data class EntriesUiState(
    val dayId: Long?,
    val year: Int,
    val editingCopy: Day?,
    val dayUiState: DayUiState,
    val weekUiState: DaysUiState,
    val yearUiState: DaysUiState,
    val locationsUiState: LocationsUiState,
    val tagsUiState: TagsUiState
)