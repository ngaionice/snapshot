package me.ionice.snapshot.ui.entries.list

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.ionice.snapshot.ui.common.DaysUiState
import me.ionice.snapshot.R
import me.ionice.snapshot.data.database.model.Date
import me.ionice.snapshot.data.database.model.Day
import me.ionice.snapshot.data.database.model.DayProperties
import me.ionice.snapshot.data.database.model.TagEntry
import me.ionice.snapshot.utils.Utils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.TextStyle

class EntriesListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var placeholderWeekItem: String
    private lateinit var placeholderYearItem: String

    private lateinit var weekAddEntryItem: String
    private lateinit var weekItem: String

    private lateinit var yearNoResults: String
    private lateinit var yearItem: String
    private lateinit var yearSubItem: String
    private lateinit var yearHeaderPrev: String
    private lateinit var yearHeaderNext: String

    private lateinit var addButton: String
    private lateinit var dialogTextField: String
    private lateinit var dialogConfirm: String
    private lateinit var dialogError: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            placeholderWeekItem = getString(R.string.cd_entries_placeholder_week_item)
            placeholderYearItem = getString(R.string.cd_entries_placeholder_year_item)
            weekAddEntryItem = getString(R.string.cd_entries_week_add_entry_item)
            weekItem = getString(R.string.cd_entries_week_item)
            yearNoResults = getString(R.string.entries_year_no_results)
            yearItem =getString(R.string.cd_entries_year_item)
            yearSubItem = getString(R.string.cd_entries_year_subitem)
            yearHeaderPrev = getString(R.string.cd_entries_year_header_prev)
            yearHeaderNext = getString(R.string.cd_entries_year_header_next)
            addButton = getString(R.string.entries_add_entry)
            dialogTextField = getString(R.string.cd_common_datepicker)
            dialogConfirm = getString(R.string.cd_entries_dialog_confirm)
            dialogError = getString(R.string.common_datepicker_error_range)
        }
    }

    @Test
    fun placeholderWeekItem_whenWeekIsLoading_showsLoading() {
        composeTestRule.setContent {
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Loading },
                yearEntriesProvider = { DaysUiState.Error },
                yearProvider = { 0 },
                onAddEntry = { },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { }
            )
        }

        composeTestRule.onAllNodesWithContentDescription(placeholderWeekItem).onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun placeholderYearItem_whenYearIsLoading_exists() {
        composeTestRule.setContent {
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Error },
                yearEntriesProvider = { DaysUiState.Loading },
                yearProvider = { 0 },
                onAddEntry = { },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { }
            )
        }

        composeTestRule.onAllNodesWithContentDescription(placeholderYearItem).onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun weekSection_whenNoEntry_showsAddEntryItem() {
        composeTestRule.setContent {
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Success(emptyList()) },
                yearEntriesProvider = { DaysUiState.Loading },
                yearProvider = { 0 },
                onAddEntry = { },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { }
            )
        }

        composeTestRule.onAllNodesWithContentDescription(weekAddEntryItem).onFirst().assertIsDisplayed()
    }

    @Test
    fun weekSection_whenHasEntry_showsEntryItem() {
        composeTestRule.setContent {
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Success(listOf(entryToday)) },
                yearEntriesProvider = { DaysUiState.Loading },
                yearProvider = { 0 },
                onAddEntry = { },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { }
            )
        }

        val item = composeTestRule.onAllNodesWithContentDescription(weekItem).assertCountEquals(1).onFirst().assertIsDisplayed()
        item.assertTextContains("${today.dayOfMonth}")
        item.assertTextContains(
            today.dayOfWeek.getDisplayName(TextStyle.SHORT, Utils.locale)
                .uppercase(Utils.locale)
        )
        item.assertTextContains(entryToday.tags.size.toString())
    }

    @Test
    fun yearSection_whenNoEntry_showsEntriesNotFound() {
        composeTestRule.setContent {
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Loading },
                yearEntriesProvider = { DaysUiState.Success(emptyList()) },
                yearProvider = { 0 },
                onAddEntry = { },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { }
            )
        }

        composeTestRule.onNodeWithText(yearNoResults).assertIsDisplayed()
    }

    @Test
    fun yearSection_whenHasEntry_showsEntry() {
        composeTestRule.setContent {
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Loading },
                yearEntriesProvider = { DaysUiState.Success(listOf(entryToday)) },
                yearProvider = { 0 },
                onAddEntry = { },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { }
            )
        }

        // click on the week containing the entry
        composeTestRule.onAllNodesWithContentDescription(yearItem).assertCountEquals(1).onFirst().assertIsDisplayed().performClick()

        // click on the entry
        composeTestRule.onNodeWithContentDescription(yearSubItem).assertIsDisplayed()
    }

    @Test
    fun yearSection_whenSwitchYear_loadsNewYear() {
        val data = MutableStateFlow(emptyList<Day>())
        composeTestRule.setContent {
            val state = data.collectAsState()
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Loading },
                yearEntriesProvider = { DaysUiState.Success(state.value) },
                yearProvider = { 2021 },
                onAddEntry = { },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { year -> data.update { entries.filter { it.properties.date.year == year  } } }
            )
        }

        // check that there are currently no entries, to prevent false positives
        composeTestRule.onAllNodesWithContentDescription(yearItem).assertCountEquals(0)

        composeTestRule.onNodeWithContentDescription(yearHeaderNext).performClick()
        composeTestRule.onAllNodesWithContentDescription(yearItem).assertCountEquals(1)
    }

    @Test
    fun yearSection_whenYearIsCurrentYear_nextYearButtonIsDisabled() {
        composeTestRule.setContent {
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Loading },
                yearEntriesProvider = { DaysUiState.Success(listOf(entryToday)) },
                yearProvider = { LocalDate.now().year },
                onAddEntry = { },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { }
            )
        }

        composeTestRule.onNodeWithContentDescription(yearHeaderNext).assertIsNotEnabled()
    }

    @Test
    fun insertDialog_onValidDateSelection_passesCorrectDate() {
        var date: Long? = null
        composeTestRule.setContent {
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Loading },
                yearEntriesProvider = { DaysUiState.Loading },
                yearProvider = { LocalDate.now().year },
                onAddEntry = { date = it },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { }
            )
        }

        composeTestRule.onNodeWithContentDescription(addButton).performClick()
        val textField = composeTestRule.onNodeWithContentDescription(dialogTextField)
        textField.performTextClearance()
        textField.performTextInput("20220101")
        composeTestRule.onNodeWithContentDescription(dialogConfirm).performClick()

        assertThat(date).isEqualTo(LocalDate.of(2022, 1, 1).toEpochDay())
    }

    @Test
    fun insertDialog_onFutureDateSelection_disablesConfirmButton() {
        composeTestRule.setContent {
            EntriesListScreen(
                weekEntriesProvider = { DaysUiState.Loading },
                yearEntriesProvider = { DaysUiState.Loading },
                yearProvider = { LocalDate.now().year },
                onAddEntry = { },
                onSelectEntry = { },
                onSelectSettings = { },
                onChangeYear = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(addButton).performClick()
        val textField = composeTestRule.onNodeWithContentDescription(dialogTextField)
        textField.performTextClearance()
        textField.performTextInput("99990101")
        composeTestRule.onNodeWithText(dialogError).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(dialogConfirm).assertIsNotEnabled()
    }
}

private val today = LocalDate.now()
private val entryToday = Day(
    properties = DayProperties(
        summary = "EntriesListScreenTestSummary",
        createdAt = 0,
        lastModifiedAt = 0,
        date = Date(today.year, today.monthValue, today.dayOfMonth)
    ),
    tags = listOf(TagEntry(today.toEpochDay(), 1)),
    location = null
)
private val entries = listOf(Day(
    properties = DayProperties(
        id = LocalDate.of(2022, 1, 1).toEpochDay(),
        summary = "EntriesListScreenTestSummary",
        createdAt = 0,
        lastModifiedAt = 0,
        date = Date(today.year, today.monthValue, today.dayOfMonth)
    ),
    tags = emptyList(),
    location = null
))
