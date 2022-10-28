package dev.ionice.snapshot.ui.settings

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import dev.ionice.snapshot.testtools.MainCoroutineRule
import dev.ionice.snapshot.testtools.data.backup.FakeBackupRepository
import dev.ionice.snapshot.testtools.data.preferences.FakePreferencesRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var viewModel: SettingsViewModel

    // VM dependencies
    private val backupRepository = FakeBackupRepository()
    private val preferencesRepository = FakePreferencesRepository()

    @Before
    fun setupViewModel() {
        viewModel = SettingsViewModel(backupRepository, preferencesRepository)
    }

    @Test
    fun uiStateBackup_whenInitialized_showsLoading() = runTest {
        assertThat(viewModel.uiState.value.backupUiState).isEqualTo(BackupUiState.Loading)
    }

    @Test
    fun uiStatePreferences_whenInitialized_showsLoading() = runTest {
        assertThat(viewModel.uiState.value.notifsUiState).isEqualTo(NotifsUiState.Loading)
    }

//    @Test
//    fun uiStateBackup_whenSuccessAndLoggingInToGoogle_thenShowEmailAndBackupTime() = runTest {
//        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
//
//        // Assert start condition != end condition
//        val startState = viewModel.uiState.value.backupUiState as BackupUiState.Success
//        assertThat(startState.signedInGoogleAccountEmail).isNull()
//        assertThat(startState.lastBackupTime).isNull()
//
//        // Action
//
//        // Need to figure out how to mock Google account
//
//        // Assert end condition
//
//        collectJob.cancel()
//    }

    @Test
    fun uiStateBackup_whenSuccessAndStartingBackup_thenShowBackupInProgress() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Setup and wait for flow to stabilize
        advanceUntilIdle()

        // Assert start condition != end condition
        val startState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(startState.isBackupInProgress).isFalse()

        // Action
        viewModel.backupDatabase()

        // Wait for flow to stabilize
        advanceUntilIdle()

        // Assert end condition
        val endState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(endState.isBackupInProgress).isTrue()

        collectJob.cancel()
    }

    @Test
    fun uiStateBackup_whenSuccessAndStartingRestore_thenShowBackupInProgress() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Setup and wait for flow to stabilize
        advanceUntilIdle()

        // Assert start condition != end condition
        val startState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(startState.isBackupInProgress).isFalse()

        // Action
        viewModel.restoreDatabase()

        // Wait for flow to stabilize
        advanceUntilIdle()

        // Assert end condition
        val endState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(endState.isBackupInProgress).isTrue()

        collectJob.cancel()
    }

    @Test
    fun uiStateBackup_whenSuccessAndEndingBackup_thenShowBackupNotInProgress() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Setup and wait for flow to stabilize
        viewModel.backupDatabase()
        advanceUntilIdle()

        // Assert start condition != end condition
        val startState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(startState.isBackupInProgress).isTrue()

        // Action
        backupRepository.endBackup()

        // Wait for flow to stabilize
        advanceUntilIdle()

        // Assert end condition
        val endState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(endState.isBackupInProgress).isFalse()

        collectJob.cancel()
    }

    @Test
    fun uiStateBackup_whenSuccessAndEndingRestore_thenShowBackupInProgress() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Setup and wait for flow to stabilize
        viewModel.restoreDatabase()
        advanceUntilIdle()

        // Assert start condition != end condition
        val startState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(startState.isBackupInProgress).isTrue()

        // Action
        backupRepository.endRestore()

        // Wait for flow to stabilize
        advanceUntilIdle()

        // Assert end condition
        val endState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(endState.isBackupInProgress).isFalse()

        collectJob.cancel()
    }

    @Test
    fun uiStatePreferences_whenEnablingBackups_thenShowBackupsEnabled() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Setup and wait for flow to stabilize
        advanceUntilIdle()

        // Assert start condition != end condition
        val startState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(startState.isEnabled).isFalse()

        // Action
        viewModel.setBackupEnabled(true)

        // Wait for flow to stabilize
        advanceUntilIdle()

        // Assert end condition
        val endState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(endState.isEnabled).isTrue()

        collectJob.cancel()
    }

    @Test
    fun uiStateBPreferences_whenChangingAutoBackupConfig_thenShowNewAutoBackupConfig() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Setup and wait for flow to stabilize
        advanceUntilIdle()

        // Assert start condition != end condition
        val startState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(startState.autoBackupFrequency).isEqualTo(0)
        assertThat(startState.autoBackupTime).isEqualTo(LocalTime.MIDNIGHT)
        assertThat(startState.autoBackupOnCellular).isFalse()

        // Action
        viewModel.setAutoBackups(frequency = 7, time = LocalTime.of(20, 0), useMeteredData = true)

        // Wait for flow to stabilize
        advanceUntilIdle()

        // Assert end condition
        val endState = viewModel.uiState.value.backupUiState as BackupUiState.Success
        assertThat(endState.autoBackupFrequency).isEqualTo(7)
        assertThat(endState.autoBackupTime).isEqualTo(LocalTime.of(20, 0))
        assertThat(endState.autoBackupOnCellular).isTrue()

        collectJob.cancel()
    }

    @Test
    fun uiStatePreferences_whenEnablingNotifs_thenShowNotifsEnabled() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Setup and wait for flow to stabilize
        advanceUntilIdle()

        // Assert start condition != end condition
        val startState = viewModel.uiState.value.notifsUiState as NotifsUiState.Success
        assertThat(startState.areNotifsEnabled).isFalse()

        // Action
        viewModel.setNotifsEnabled(true)

        // Wait for flow to stabilize
        advanceUntilIdle()

        // Assert end condition
        val endState = viewModel.uiState.value.notifsUiState as NotifsUiState.Success
        assertThat(endState.areNotifsEnabled).isTrue()

        collectJob.cancel()
    }

    @Test
    fun uiStatePreferences_whenChangingReminderConfig_thenShowNewReminderConfig() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Setup and wait for flow to stabilize
        advanceUntilIdle()

        // Assert start condition != end condition
        val startState = viewModel.uiState.value.notifsUiState as NotifsUiState.Success
        assertThat(startState.areNotifsEnabled).isFalse()

        // Action
        viewModel.setDailyReminders(enabled = true, time = LocalTime.of(20, 0))

        // Wait for flow to stabilize
        advanceUntilIdle()

        // Assert end condition
        val endState = viewModel.uiState.value.notifsUiState as NotifsUiState.Success
        assertThat(endState.isRemindersEnabled).isTrue()
        assertThat(endState.reminderTime).isEqualTo(LocalTime.of(20, 0))

        collectJob.cancel()
    }
}