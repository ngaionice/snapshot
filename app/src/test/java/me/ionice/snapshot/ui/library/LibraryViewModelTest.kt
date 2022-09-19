package me.ionice.snapshot.ui.library

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import me.ionice.snapshot.MainCoroutineRule
import me.ionice.snapshot.data.database.repository.FakeDayRepository
import me.ionice.snapshot.data.database.repository.FakeLocationRepository
import me.ionice.snapshot.data.database.repository.FakeTagRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var viewModel: LibraryViewModel

    // VM dependencies
    private val dayRepository = FakeDayRepository()
    private val locationRepository = FakeLocationRepository()
    private val tagRepository = FakeTagRepository()

    @Before
    fun setupViewModel() {
        viewModel = LibraryViewModel(dayRepository, locationRepository, tagRepository)
    }

    @Test
    fun isStateOnActionPlaceholder() = runTest {

    }
}