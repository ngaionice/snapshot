package dev.ionice.snapshot.feature.entries

import com.google.common.truth.Truth.assertThat
import dev.ionice.snapshot.core.model.*
import dev.ionice.snapshot.core.ui.DayUiState
import dev.ionice.snapshot.core.ui.DaysUiState
import dev.ionice.snapshot.core.ui.LocationsUiState
import dev.ionice.snapshot.core.ui.TagsUiState
import dev.ionice.snapshot.testtools.MainCoroutineRule
import dev.ionice.snapshot.testtools.data.database.repository.FRD
import dev.ionice.snapshot.testtools.data.database.repository.FakeDayRepository
import dev.ionice.snapshot.testtools.data.database.repository.FakeLocationRepository
import dev.ionice.snapshot.testtools.data.database.repository.FakeTagRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

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
    fun uiStateDay_whenInitialized_thenShowLoading() = runTest {
        assertThat(viewModel.singleUiState.value.dayUiState).isInstanceOf(DayUiState.Loading::class.java)
    }

    @Test
    fun uiStateWeek_whenInitialized_thenShowLoading() = runTest {
        assertThat(viewModel.listUiState.value.weekUiState).isInstanceOf(DaysUiState.Loading::class.java)
    }

    @Test
    fun uiStateYear_whenInitialized_thenShowLoading() = runTest {
        assertThat(viewModel.listUiState.value.yearUiState).isInstanceOf(DaysUiState.Loading::class.java)
    }

    @Test
    fun uiStateLocations_whenInitialized_thenShowLoading() = runTest {
        assertThat(viewModel.singleUiState.value.locationsUiState).isInstanceOf(LocationsUiState.Loading::class.java)
    }

    @Test
    fun uiStateTags_whenInitialized_thenShowLoading() = runTest {
        assertThat(viewModel.singleUiState.value.tagsUiState).isInstanceOf(TagsUiState.Loading::class.java)
    }

    @Test
    fun uiStateYear_whenChangingListYear_thenShowNewYearAndNewEntries() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.listUiState.collect { } }

        val newYear = 2021
        val yearEntries = dayRepository.getListFlowByYear(newYear).first()

        viewModel.changeListYear(newYear)
        advanceUntilIdle()

        assertThat(viewModel.listUiState.value.year).isEqualTo(newYear)
        assertThat(viewModel.listUiState.value.yearUiState).isEqualTo(DaysUiState.Success(data = yearEntries))

        collectJob.cancel()
    }

    // more tests to assess UI state success behavior on actions
    @Test
    fun uiStateDays_whenWeekFlowSuccessAndYearFlowSuccess_weekUiStateSuccessAndYearUiStateSuccess() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.listUiState.collect {} }

            // use current date because EntriesViewModel defaults to using current date as initial value
            val today = LocalDate.now()
            val data = Day(
                id = today.toEpochDay(),
                summary = "",
                createdAt = 0L,
                lastModifiedAt = 0L,
                isFavorite = false,
                tags = emptyList(),
                location = null
            )
            dayRepository.sendDays(listOf(data))
            advanceUntilIdle()

            assertThat(viewModel.listUiState.value.weekUiState).isEqualTo(
                DaysUiState.Success(
                    data = listOf(
                        data
                    )
                )
            )
            assertThat(viewModel.listUiState.value.yearUiState).isEqualTo(
                DaysUiState.Success(
                    data = listOf(
                        data
                    )
                )
            )

            collectJob.cancel()
        }

    @Test
    fun uiStateLocation_whenLocationSuccess_thenShowSuccess() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.singleUiState.collect {} }

        val data = Location(
            id = 0,
            coordinates = Coordinates(0.0, 0.0),
            name = "",
            lastUsedAt = 0
        )
        locationRepository.sendLocations(listOf(data))
        advanceUntilIdle()

        assertThat(viewModel.singleUiState.value.locationsUiState).isEqualTo(
            LocationsUiState.Success(data = listOf(data))
        )

        collectJob.cancel()
    }

    @Test
    fun uiStateTags_whenTagsSuccess_thenShowSuccess() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.singleUiState.collect {} }

        val data = Tag(
            id = 0, name = "", lastUsedAt = 0
        )
        tagRepository.sendTags(listOf(data))
        advanceUntilIdle()

        assertThat(viewModel.singleUiState.value.tagsUiState).isEqualTo(
            TagsUiState.Success(data = listOf(data))
        )

        collectJob.cancel()
    }

    @Test
    fun entriesViewModel_whenAdd_insertsDayIntoRepo() = runTest {
        viewModel.add(0)
        advanceUntilIdle()
        assertThat(dayRepository.getFlow(0).first()).isNotNull()
    }

    @Test
    fun entriesViewModel_whenEditAndSave_updatesDayInRepo() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.singleUiState.collect {} }

        val dayToEdit = dayRepository.getFlow(FRD.dayIds[1]).first()!!
        val newSummary = "Fake summary edited"

        viewModel.edit(
            dayToEdit.copy(
                summary = newSummary,
                tags = listOf(ContentTag(tag = FRD.tagSourceData[0])),
                location = FRD.locationSourceData[0]
            )
        )
        advanceUntilIdle()

        assertThat(viewModel.singleUiState.value.editingCopy).isNotNull()
        viewModel.save()
        advanceUntilIdle()

        val updated = dayRepository.getFlow(FRD.dayIds[1]).first()!!
        val (_, summary, _, location, tags) = updated

        assertThat(summary).isEqualTo(newSummary)
        assertThat(tags).contains(ContentTag(tag = FRD.tagSourceData[0]))
        assertThat(location).isEqualTo(
            FRD.locationSourceData[0]
        )

        collectJob.cancel()
    }

    @Test
    fun entriesViewModel_whenLoadAndFavorite_getsDayFromRepoAndUpdatesDayInRepo() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.singleUiState.collect {} }

        val day = dayRepository.getFlow(FRD.dayIds[0]).first()!!

        viewModel.load(FRD.dayIds[0])
        advanceUntilIdle()

        assertThat(viewModel.singleUiState.value.dayUiState).isEqualTo(DayUiState.Success(data = day))

        viewModel.favorite(isFavorite = true)
        advanceUntilIdle()

        val updated = dayRepository.getFlow(FRD.dayIds[0]).first()!!
        assertThat(updated.isFavorite).isEqualTo(true)

        collectJob.cancel()
    }

    @Test
    fun entriesViewModel_whenAddLocation_insertsLocationIntoRepo() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.listUiState.collect {} }

        val existing = locationRepository.getAllPropertiesFlow().first()
        val location = Pair("EntriesViewModelLocation", Coordinates(180.0, 180.0))

        assertThat(existing.map { it.name }).doesNotContain(location.first)

        viewModel.addLocation(location.first, location.second)
        advanceUntilIdle()

        val updated = locationRepository.getAllPropertiesFlow().first()
        val entry = updated.find { it.name == location.first }
        assertThat(entry).isNotNull()
        assertThat(entry!!.coordinates).isEqualTo(location.second)

        collectJob.cancel()
    }

    @Test
    fun entriesViewModel_whenAddTag_insertsTagIntoRepo() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.listUiState.collect {} }

        val existing = tagRepository.getAllFlow().first()
        val tag = "EntriesViewModelTag"

        assertThat(existing.map { it.name }).doesNotContain(tag)

        viewModel.addTag(tag)
        advanceUntilIdle()

        val updated = tagRepository.getAllFlow().first()
        assertThat(updated.find { it.name == tag }).isNotNull()

        collectJob.cancel()
    }
}