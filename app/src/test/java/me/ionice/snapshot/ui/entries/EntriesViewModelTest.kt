package me.ionice.snapshot.ui.entries

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import me.ionice.snapshot.MainCoroutineRule
import me.ionice.snapshot.data.database.model.LocationEntry
import me.ionice.snapshot.data.database.model.TagEntry
import me.ionice.snapshot.data.database.repository.FRD
import me.ionice.snapshot.data.database.repository.FakeDayRepository
import me.ionice.snapshot.data.database.repository.FakeLocationRepository
import me.ionice.snapshot.data.database.repository.FakeTagRepository
import me.ionice.snapshot.ui.common.DayUiState
import me.ionice.snapshot.ui.common.DaysUiState
import me.ionice.snapshot.ui.common.LocationsUiState
import me.ionice.snapshot.ui.common.TagsUiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EntriesViewModelTest {

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var viewModel: EntriesViewModel

    // VM dependencies
    private val dayRepository = FakeDayRepository()
    private val locationRepository = FakeLocationRepository()
    private val tagRepository = FakeTagRepository()

    @Before
    fun setupViewModel() {
        viewModel = EntriesViewModel(dayRepository, locationRepository, tagRepository)
    }

    @Test
    fun uiStateDay_whenInitialized_showsLoading() = runTest {
        assertThat(viewModel.uiState.value.dayUiState).isInstanceOf(DayUiState.Loading::class.java)
    }

    @Test
    fun uiStateWeek_whenInitialized_showsLoading() = runTest {
        assertThat(viewModel.uiState.value.weekUiState).isInstanceOf(DaysUiState.Loading::class.java)
    }

    @Test
    fun uiStateYear_whenInitialized_showsLoading() = runTest {
        assertThat(viewModel.uiState.value.yearUiState).isInstanceOf(DaysUiState.Loading::class.java)
    }

    @Test
    fun uiStateLocations_whenInitialized_showsLoading() = runTest {
        assertThat(viewModel.uiState.value.locationsUiState).isInstanceOf(LocationsUiState.Loading::class.java)
    }

    @Test
    fun uiStateTags_whenInitialized_showsLoading() = runTest {
        assertThat(viewModel.uiState.value.tagsUiState).isInstanceOf(TagsUiState.Loading::class.java)
    }

    @Test
    fun entriesViewModel_whenAdd_insertsDayIntoRepo() = runTest {
        viewModel.add(0)
        assertThat(dayRepository.get(0)).isNotNull()
    }

    @Test
    fun entriesViewModel_whenEditAndSave_updatesDayInRepo() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect{} }

        val dayToEdit = dayRepository.get(FRD.dayIds[1])!!
        val (properties) = dayToEdit
        val newSummary = "Fake summary edited"

        viewModel.edit(
            dayToEdit.copy(
                properties = properties.copy(summary = newSummary),
                tags = listOf(TagEntry(dayId = FRD.dayIds[1], tagId = FRD.tagId)),
                location = LocationEntry(dayId = FRD.dayIds[1], locationId = FRD.locationId)
            )
        )
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.editingCopy).isNotNull()
        viewModel.save()
        advanceUntilIdle()

        val updated = dayRepository.get(FRD.dayIds[1])!!
        val (nProperties, nTags, nLocation) = updated

        assertThat(nProperties.summary).isEqualTo(newSummary)
        assertThat(nTags).contains(TagEntry(dayId = FRD.dayIds[1], tagId = FRD.tagId))
        assertThat(nLocation).isEqualTo(
            LocationEntry(
                dayId = FRD.dayIds[1],
                locationId = FRD.locationId
            )
        )

        collectJob.cancel()
    }

    @Test
    fun entriesViewModel_whenFavorite_updatesDayInRepo() {
        TODO()
    }

    @Test
    fun entriesViewModel_whenAddLocation_insertsLocationIntoRepo() {
        TODO()
    }

    @Test
    fun entriesViewModel_whenAddTag_insertsTagIntoRepo() {
        TODO()
    }

    @Test
    fun entriesViewModel_whenGet_fetchesDayFromRepo() {
        TODO()
    }
}