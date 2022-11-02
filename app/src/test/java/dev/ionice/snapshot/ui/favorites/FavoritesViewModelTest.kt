package dev.ionice.snapshot.ui.favorites

import com.google.common.truth.Truth.assertThat
import dev.ionice.snapshot.feature.favorites.FavoritesUiState
import dev.ionice.snapshot.feature.favorites.FavoritesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import dev.ionice.snapshot.testtools.MainCoroutineRule
import dev.ionice.snapshot.testtools.data.database.repository.FakeDayRepository
import dev.ionice.snapshot.testtools.data.database.repository.FakeLocationRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var viewModel: FavoritesViewModel

    // VM dependencies
    private val dayRepository = FakeDayRepository()
    private val locationRepository = FakeLocationRepository()

    @Before
    fun setupViewModel() {
        viewModel = FavoritesViewModel(dayRepository, locationRepository)
    }

    @Test
    fun uiState_whenInitialized_showsLoading() = runTest {
        assertThat(viewModel.uiState.value).isEqualTo(FavoritesUiState.Loading)
    }

    @Test
    fun uiState_whenLoaded_showsSuccess() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Arrange
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(FavoritesUiState.Success::class.java)

        collectJob.cancel()
    }
}