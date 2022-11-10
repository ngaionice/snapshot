package dev.ionice.snapshot.feature.search

import com.google.common.truth.Truth.assertThat
import dev.ionice.snapshot.testtools.MainCoroutineRule
import dev.ionice.snapshot.testtools.data.database.repository.FRD
import dev.ionice.snapshot.testtools.data.database.repository.FakeDayRepository
import dev.ionice.snapshot.testtools.data.database.repository.FakeLocationRepository
import dev.ionice.snapshot.testtools.data.database.repository.FakeTagRepository
import dev.ionice.snapshot.testtools.data.preferences.FakePreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class SearchViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SearchViewModel

    private val dayRepository = FakeDayRepository()
    private val locationRepository = FakeLocationRepository()
    private val tagRepository = FakeTagRepository()
    private val prefsRepository = FakePreferencesRepository()

    @Before
    fun setupViewModel() {
        viewModel =
            SearchViewModel(dayRepository, locationRepository, tagRepository, prefsRepository)
    }

    @Test
    fun uiStates_whenInitialized_areLoading() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // assert
        val uiState = viewModel.uiState.value
        assertThat(uiState.fullResultsUiState).isEqualTo(ResultsUiState.Loading)
        assertThat(uiState.locationsUiState).isEqualTo(LocationsUiState.Loading)
        assertThat(uiState.tagsUiState).isEqualTo(TagsUiState.Loading)

        collectJob.cancel()
    }

    @Test
    fun searchString_whenSetSearchStringFunctionCalled_isUpdated() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // act
        val searchString = "test"
        viewModel.setSearchString(searchString)
        advanceUntilIdle()

        // assert
        assertThat(viewModel.uiState.value.searchString).isEqualTo(searchString)

        collectJob.cancel()
    }

    @Test
    fun filters_whenSetFiltersFunctionCalled_isUpdated() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        val filters = Filters(
            DateFilter.OlderThan.ONE_YEAR,
            setOf(FRD.locationSourceData[0]),
            setOf(FRD.tagSourceData[0])
        )

        // act
        viewModel.setFilters(filters)
        advanceUntilIdle()

        // assert
        assertThat(viewModel.uiState.value.filters).isEqualTo(filters)

        collectJob.cancel()
    }

    @Test
    fun quickResults_whenInitialized_isEmpty() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // assert
        assertThat(viewModel.uiState.value.quickResults).isEmpty()

        collectJob.cancel()
    }

    @Test
    fun quickResults_whenNonEmptySearchString_hasResults() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // act: set search string
        viewModel.setSearchString("summary")
        advanceUntilIdle()

        // assert
        assertThat(viewModel.uiState.value.quickResults).isNotEmpty()

        collectJob.cancel()
    }

    @Test
    fun quickResults_whenEmptySearchString_isEmpty() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // arrange: set quick results to be non-empty
        viewModel.setSearchString("summary")
        advanceUntilIdle()

        // act: set search string to be empty
        viewModel.setSearchString("")
        advanceUntilIdle()

        // assert
        assertThat(viewModel.uiState.value.quickResults).isEmpty()

        collectJob.cancel()
    }

    @Test
    fun quickResults_whenHasActiveFilters_isFiltered() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        val searchString = "summary"
        val filters = Filters(
            DateFilter.OlderThan.ONE_WEEK,
            setOf(FRD.locationSourceData[0]),
            setOf(FRD.tagSourceData[0])
        )

        val (_, endDate) = DateFilter.OlderThan.ONE_WEEK.getDates()

        // act
        viewModel.setSearchString(searchString)
        viewModel.setFilters(filters)
        advanceUntilIdle()

        // assert
        val expected = dayRepository.getListFlowByYear(LocalDate.now().year).first()
            .filter { it.summary.contains(searchString, ignoreCase = true) }
            .filter { it.id <= endDate!! }
            .filter { filters.locationFilters.map{ loc -> loc.id }.contains(it.location?.id) }
            .filter { it.tags.any { tag -> filters.tagFilters.contains(tag.tag) } }
        assertThat(viewModel.uiState.value.quickResults).isEqualTo(expected)

        collectJob.cancel()
    }

    @Test
    fun fullResultsUiState_whenSearchFunctionCalled_transitionsToLoadingThenSuccess() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        val searchString = "summary"

        // act
        viewModel.setSearchString(searchString)
        viewModel.search()

        // assert
        assertThat(viewModel.uiState.value.fullResultsUiState).isEqualTo(ResultsUiState.Loading)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.fullResultsUiState).isEqualTo(
            ResultsUiState.Success(FRD.daySourceData.filter {
                it.summary.contains(
                    searchString,
                    ignoreCase = true
                )
            }.reversed())
        )

        collectJob.cancel()
    }

    @Test
    fun fullResultsUiState_whenSearchFunctionCalledWithFilters_getsFilteredResults() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        val searchString = "summary"
        val filters = Filters(
            DateFilter.OlderThan.ONE_WEEK,
            setOf(FRD.locationSourceData[0]),
            setOf(FRD.tagSourceData[0])
        )

        val (_, endDate) = DateFilter.OlderThan.ONE_WEEK.getDates()

        // act
        viewModel.setSearchString(searchString)
        viewModel.setFilters(filters)
        viewModel.search()
        advanceUntilIdle()

        // assert
        val expected = FRD.daySourceData
            .filter { it.summary.contains(searchString, ignoreCase = true) }
            .filter { it.id <= endDate!! }
            .filter { filters.locationFilters.map{ loc -> loc.id }.contains(it.location?.id) }
            .filter { it.tags.any { tag -> filters.tagFilters.contains(tag.tag) } }
            .reversed()
        val results = viewModel.uiState.value.fullResultsUiState
        assertThat(results).isInstanceOf(ResultsUiState.Success::class.java)
        assertThat((results as ResultsUiState.Success).data).isEqualTo(expected)

        collectJob.cancel()
    }

    @Test
    fun recentSearches_whenSearchFunctionCalled_isUpdated() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        val searchString = "summary"

        // act
        viewModel.setSearchString(searchString)
        viewModel.search()
        advanceUntilIdle()

        // assert
        assertThat(viewModel.uiState.value.searchHistory).contains(searchString)

        collectJob.cancel()
    }

}