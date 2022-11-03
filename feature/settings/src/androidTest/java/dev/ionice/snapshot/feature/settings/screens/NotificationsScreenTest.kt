package dev.ionice.snapshot.feature.settings.screens

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.ionice.snapshot.feature.settings.BackupUiState
import dev.ionice.snapshot.feature.settings.NotifsUiState
import dev.ionice.snapshot.feature.settings.R
import dev.ionice.snapshot.feature.settings.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

class NotificationsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun screen_whenUiStateIsLoading_showsLoading() {
        composeTestRule.setContent {
            NotificationsScreen(
                uiStateProvider = {
                    SettingsUiState(
                        backupUiState = BackupUiState.Loading,
                        notifsUiState = NotifsUiState.Loading
                    ).notifsUiState
                },
                onNotifsEnabledChange = {},
                onRemindersChange = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithTag(getString(R.string.tt_common_loading_screen))
            .assertIsDisplayed()
    }

    @Test
    fun mainToggle_whenEnablingNotifs_showsEnabledAndNotifsOptions() {
        val state = MutableStateFlow(
            NotifsUiState.Success(
                areNotifsEnabled = false,
                isRemindersEnabled = false,
                reminderTime = LocalTime.MIDNIGHT
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            NotificationsScreen(
                uiStateProvider = { uiState },
                onNotifsEnabledChange = { newVal ->
                    state.update { it.copy(areNotifsEnabled = newVal) }
                },
                onRemindersChange = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_notifs_main_toggle))
            .performClick()
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_notifs_main_toggle))
            .assertIsOn()
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_notifs_reminders_toggle))
            .assertIsDisplayed()
    }

    @Test
    fun remindersSection_whenEnablingReminders_showsEnabled() {
        val state = MutableStateFlow(
            NotifsUiState.Success(
                areNotifsEnabled = true,
                isRemindersEnabled = false,
                reminderTime = LocalTime.MIDNIGHT
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            NotificationsScreen(
                uiStateProvider = { uiState },
                onNotifsEnabledChange = {},
                onRemindersChange = { enabled, _ ->
                    state.update { it.copy(isRemindersEnabled = enabled) }
                }
            )
        }

        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_notifs_reminders_time_btn))
            .assertIsNotEnabled()

        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_notifs_reminders_toggle))
            .performClick()
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_notifs_reminders_toggle))
            .assertIsOn()
        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_notifs_reminders_time_btn))
            .assertIsEnabled()
    }

    @Test
    fun remindersSection_whenSelectingTime_showsUpdatedTime() {
        val state = MutableStateFlow(
            NotifsUiState.Success(
                areNotifsEnabled = true,
                isRemindersEnabled = true,
                reminderTime = LocalTime.MIDNIGHT
            )
        )

        composeTestRule.setContent {
            val uiState by state.collectAsState()
            NotificationsScreen(
                uiStateProvider = { uiState },
                onNotifsEnabledChange = {},
                onRemindersChange = { _, time ->
                    state.update { it.copy(reminderTime = time) }
                }
            )
        }

        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_notifs_reminders_time_btn))
            .performClick()

        composeTestRule.onNodeWithTag(getString(R.string.tt_common_time_picker_digit_hr))
            .performClick()
        composeTestRule.onNodeWithTag(getString(R.string.tt_common_time_picker_digit_hr))
            .performTextInput("20")

        composeTestRule.onNodeWithTag(getString(R.string.tt_common_time_picker_digit_min))
            .performClick()
        composeTestRule.onNodeWithTag(getString(R.string.tt_common_time_picker_digit_min))
            .performTextInput("30")

        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_time_picker_confirm))
            .performClick()

        composeTestRule.onNodeWithTag(getString(R.string.tt_settings_notifs_reminders_time_btn))
            .assertTextContains("20:30")
    }

    private fun getString(@StringRes resId: Int) =
        composeTestRule.activity.resources.getString(resId)
}