package me.ionice.snapshot.ui.library

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import me.ionice.snapshot.MainCoroutineRule
import me.ionice.snapshot.data.database.repository.MockDayRepository
import me.ionice.snapshot.data.database.repository.MockLocationRepository
import me.ionice.snapshot.data.database.repository.MockTagRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Test subject
    private lateinit var viewModel: LibraryViewModel

    // VM dependencies
    private val dayRepository = MockDayRepository()
    private val locationRepository = MockLocationRepository()
    private val tagRepository = MockTagRepository()

    @Before
    fun setupViewModel() {
        viewModel = LibraryViewModel(dayRepository, locationRepository, tagRepository)
    }

    @Test
    fun isStateOnActionPlaceholder() = runTest {

    }
}