package dev.ionice.snapshot.ui.settings.screens

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dev.ionice.snapshot.ui.settings.screens.BackupScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import dev.ionice.snapshot.R
import dev.ionice.snapshot.ui.settings.BackupUiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

class BackupScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mainToggle: String

    @Before
    fun setup() {
        mainToggle = getString(R.string.tt_settings_backup_main_toggle)
    }

    @Test
    fun screen_whenUiStateIsLoading_showsLoading() {
        // Arrange
        composeTestRule.setContent {
            renderScreen(uiState = BackupUiState.Loading)
        }

        // Assert
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_backup_loading))
            .assertIsDisplayed()
    }

    @Test
    fun mainToggle_whenEnablingBackups_enablesToggleAndShowsBackupOptions() {
        // Arrange
        val state = MutableStateFlow(baseBackupUiState.copy(isEnabled = false))

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(
                uiState = uiState,
                onEnableBackup = { enabled -> state.update { it.copy(isEnabled = enabled) } }
            )
        }

        // Act
        composeTestRule.onNodeWithTag(mainToggle).performClick()

        // Assert
        composeTestRule.onNodeWithTag(mainToggle).assertIsOn()
        composeTestRule.onNodeWithText(getString(R.string.settings_screen_backup_drive_settings))
            .assertIsDisplayed()
    }

    @Test
    fun backupOptions_whenEmailIsNull_showsLoginSection() {
        // Arrange
        val state = MutableStateFlow(
            baseBackupUiState.copy(
                isEnabled = true,
                signedInGoogleAccountEmail = null
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(uiState = uiState)
        }

        // Assert
        composeTestRule.onNodeWithText(getString(R.string.settings_screen_backup_drive_settings))
            .assertIsDisplayed()
    }

    @Test
    fun backupOptions_whenEmailIsNotNull_showsBackupFunctions() {
        // Arrange
        val state = MutableStateFlow(
            baseBackupUiState.copy(
                isEnabled = true,
                signedInGoogleAccountEmail = "abc"
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(uiState = uiState)
        }

        // Assert
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_backup_functions))
            .assertIsDisplayed()
    }

    @Test
    fun backupFunctions_whenBackupInProgress_hidesAutoAndManualSections() {
        // Arrange
        val state = MutableStateFlow(
            baseBackupUiState.copy(
                isEnabled = true,
                signedInGoogleAccountEmail = "abc",
                isBackupInProgress = true
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(uiState = uiState)
        }

        // Assert
        composeTestRule.onNodeWithText(getString(R.string.settings_screen_backup_auto_backup_subsection_header))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.settings_screen_backup_manual_actions_subsection_header))
            .assertDoesNotExist()
    }

    @Test
    fun autoBackupOptions_whenAutoBackupDisabled_disablesBackupTimeSelection() {
        // Arrange
        val state = MutableStateFlow(
            baseBackupUiState.copy(
                isEnabled = true,
                signedInGoogleAccountEmail = "abc",
                isBackupInProgress = false,
                autoBackupFrequency = 0
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(uiState = uiState)
        }

        // Assert
        composeTestRule.onNodeWithText(getString(R.string.settings_auto_backup_time))
            .assertIsNotEnabled()
    }

    @Test
    fun autoBackupOptions_whenEnablingAutoBackup_showsUpdatedFrequencyAndEnablesOptions() {
        // Arrange
        val state = MutableStateFlow(
            baseBackupUiState.copy(
                isEnabled = true,
                signedInGoogleAccountEmail = "abc",
                isBackupInProgress = false,
                autoBackupFrequency = 0
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(
                uiState = uiState,
                onAutoBackupConfigChange = { freq, _, _ ->
                    state.update {
                        it.copy(
                            autoBackupFrequency = freq
                        )
                    }
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText(getString(R.string.settings_auto_backup_frequency))
            .performClick()
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_backup_freq_selector))
            .performClick()
        composeTestRule.onNodeWithText(getString(R.string.settings_auto_backup_freq_daily))
            .performClick()
        composeTestRule.onNodeWithText(getString(R.string.common_dialog_ok)).performClick()

        // Assert
        composeTestRule.onNodeWithText(getString(R.string.settings_auto_backup_frequency))
            .assertTextContains(getString(R.string.settings_auto_backup_freq_daily))
        composeTestRule.onNodeWithText(getString(R.string.settings_auto_backup_time))
            .assertIsEnabled()
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_backup_metered_toggle))
            .assertIsEnabled()
    }

    @Test
    fun autoBackupSection_whenSelectingTime_showsUpdatedTime() {
        // Arrange
        val state = MutableStateFlow(
            baseBackupUiState.copy(
                isEnabled = true,
                signedInGoogleAccountEmail = "abc",
                isBackupInProgress = false,
                autoBackupFrequency = 1,
                autoBackupTime = LocalTime.MIDNIGHT
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(
                uiState = uiState,
                onAutoBackupConfigChange = { _, time, _ -> state.update { it.copy(autoBackupTime = time) } }
            )
        }

        // Act
        composeTestRule.onNodeWithText(getString(R.string.settings_auto_backup_time)).performClick()

        composeTestRule.onNodeWithTag(getString(R.string.tt_common_time_picker_digit_hr))
            .performClick()
        composeTestRule.onNodeWithTag(getString(R.string.tt_common_time_picker_digit_hr))
            .performTextInput("20")

        composeTestRule.onNodeWithTag(getString(R.string.tt_common_time_picker_digit_min))
            .performClick()
        composeTestRule.onNodeWithTag(getString(R.string.tt_common_time_picker_digit_min))
            .performTextInput("30")

        composeTestRule.onNodeWithText(getString(R.string.common_dialog_ok)).performClick()

        // Assert
        composeTestRule.onNodeWithText(getString(R.string.settings_auto_backup_time))
            .assertTextContains("20:30")
    }

    @Test
    fun autoBackupOptions_whenEnablingMeteredData_showsEnabled() {
        // Arrange
        val state = MutableStateFlow(
            baseBackupUiState.copy(
                isEnabled = true,
                signedInGoogleAccountEmail = "abc",
                isBackupInProgress = false,
                autoBackupFrequency = 1,
                autoBackupOnCellular = false
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(
                uiState = uiState,
                onAutoBackupConfigChange = { _, _, useData ->
                    state.update {
                        it.copy(
                            autoBackupOnCellular = useData
                        )
                    }
                }
            )
        }

        // Act
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_backup_metered_toggle))
            .performClick()

        // Assert
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_backup_metered_toggle))
            .assertIsOn()
    }

    @Test
    fun manualBackupSection_whenSelectingBackUp_showsConfirmDialog() {
        // Arrange
        val state = MutableStateFlow(
            baseBackupUiState.copy(
                isEnabled = true,
                signedInGoogleAccountEmail = "abc",
                isBackupInProgress = false
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(uiState = uiState)
        }

        // Act
        composeTestRule.onNodeWithText(getString(R.string.settings_screen_backup_start_backup))
            .performClick()

        // Assert
        composeTestRule.onNodeWithText(getString(R.string.settings_screen_backup_dialog_content))
            .assertIsDisplayed()
    }

    @Test
    fun manualBackupSection_whenSelectingRestore_showsConfirmDialog() {
// Arrange
        val state = MutableStateFlow(
            baseBackupUiState.copy(
                isEnabled = true,
                signedInGoogleAccountEmail = "abc",
                isBackupInProgress = false
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            renderScreen(uiState = uiState)
        }

        // Act
        composeTestRule.onNodeWithText(getString(R.string.settings_screen_backup_start_restore))
            .performClick()

        // Assert
        composeTestRule.onNodeWithText(getString(R.string.settings_screen_restore_dialog_content))
            .assertIsDisplayed()
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun renderScreen(
        uiState: BackupUiState,
        onEnableBackup: (Boolean) -> Unit = { },
        onSuccessfulLogin: (GoogleSignInAccount) -> Unit = { },
        onStartBackup: () -> Unit = { },
        onStartRestore: () -> Unit = { },
        onAutoBackupConfigChange: (Int, LocalTime, Boolean) -> Unit = { _, _, _ -> }
    ) {
        BackupScreen(
            uiStateProvider = { uiState },
            onEnableBackup = onEnableBackup,
            onSuccessfulLogin = onSuccessfulLogin,
            onStartBackup = onStartBackup,
            onStartRestore = onStartRestore,
            onAutoBackupConfigChange = onAutoBackupConfigChange
        )
    }

    private val baseBackupUiState = BackupUiState.Success(
        isEnabled = false,
        signedInGoogleAccountEmail = null,
        lastBackupTime = null,
        isBackupInProgress = false,
        autoBackupFrequency = 0,
        autoBackupTime = LocalTime.MIDNIGHT,
        autoBackupOnCellular = false
    )

    private fun getString(@StringRes resId: Int) =
        composeTestRule.activity.resources.getString(resId)
}