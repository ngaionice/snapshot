package dev.ionice.snapshot.ui.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ionice.snapshot.core.common.Result
import dev.ionice.snapshot.core.common.asResult
import dev.ionice.snapshot.core.database.model.CoordinatesEntity
import dev.ionice.snapshot.core.database.model.DayEntity
import dev.ionice.snapshot.core.data.repository.DayRepository
import dev.ionice.snapshot.core.data.repository.LocationRepository
import dev.ionice.snapshot.core.data.repository.TagRepository
import dev.ionice.snapshot.core.ui.DayUiState
import dev.ionice.snapshot.core.ui.DaysUiState
import dev.ionice.snapshot.core.ui.LocationsUiState
import dev.ionice.snapshot.core.ui.TagsUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    private val mutableDayCopy = MutableStateFlow<DayEntity?>(null)

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

    val singleUiState: StateFlow<EntriesSingleUiState> = combine(
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
        EntriesSingleUiState(dayId, dayState, locationState, tagState, dayCopy)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EntriesSingleUiState(
            dayId = dayId.value,
            editingCopy = null,
            dayUiState = DayUiState.Loading,
            locationsUiState = LocationsUiState.Loading,
            tagsUiState = TagsUiState.Loading
        )
    )

    val listUiState: StateFlow<EntriesListUiState> = combine(
        year, weekFlow, yearFlow.debounce(100)
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
        EntriesListUiState(year, weekEntries, yearEntries)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EntriesListUiState(
            year = year.value,
            weekUiState = DaysUiState.Loading,
            yearUiState = DaysUiState.Loading,
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

    fun edit(day: DayEntity?) {
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
        val dayUiState = singleUiState.value.dayUiState
        if (dayUiState is DayUiState.Success && dayUiState.data != null && dayUiState.data!!.properties.isFavorite != isFavorite) {
            val (properties, tags, location) = dayUiState.data!!
            val (id, summary) = properties
            viewModelScope.launch { dayRepository.update(id, summary, isFavorite, location, tags) }
        }
    }

    fun addLocation(name: String, coordinates: CoordinatesEntity) {
        viewModelScope.launch { locationRepository.add(coordinates, name) }
    }

    fun addTag(name: String) {
        viewModelScope.launch { tagRepository.add(name) }
    }
}

data class EntriesListUiState(
    val year: Int, val weekUiState: DaysUiState, val yearUiState: DaysUiState
)

data class EntriesSingleUiState(
    val dayId: Long?,
    val dayUiState: DayUiState,
    val locationsUiState: LocationsUiState,
    val tagsUiState: TagsUiState,
    val editingCopy: DayEntity?
)