package dev.ionice.snapshot.ui.entries.single

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.common.truth.Truth.assertThat
import dev.ionice.snapshot.R
import dev.ionice.snapshot.core.database.model.Day
import dev.ionice.snapshot.core.database.model.LocationEntry
import dev.ionice.snapshot.core.database.model.TagEntry
import dev.ionice.snapshot.core.database.model.TagProperties
import dev.ionice.snapshot.testtools.data.database.repository.FRD
import dev.ionice.snapshot.ui.common.DayUiState
import dev.ionice.snapshot.ui.common.LocationsUiState
import dev.ionice.snapshot.ui.common.TagsUiState
import dev.ionice.snapshot.ui.entries.EntriesSingleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EntriesSingleScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var dataLoading: String
    private lateinit var dataNotFound: String
    private lateinit var dataFound: String

    private lateinit var favorite: String
    private lateinit var edit: String
    private lateinit var exitEdit: String
    private lateinit var save: String

    private lateinit var summaryBtn: String
    private lateinit var locationBtn: String
    private lateinit var tagsBtn: String

    private lateinit var summarySection: String
    private lateinit var dialogOk: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            dataLoading = getString(R.string.entries_single_loading)
            dataNotFound = getString(R.string.tt_entries_single_not_found)
            dataFound = getString(R.string.tt_entries_single_entry)
            favorite = getString(R.string.entries_single_favorite)
            edit = getString(R.string.entries_single_edit)
            exitEdit = getString(R.string.entries_single_exit_edit)
            save = getString(R.string.entries_single_save)

            summaryBtn = getString(R.string.entries_single_summary_btn)
            locationBtn = getString(R.string.entries_single_location_btn)
            tagsBtn = getString(R.string.entries_single_tags_btn)

            summarySection = getString(R.string.tt_entries_single_summary)

            dialogOk = getString(R.string.common_dialog_ok)
        }
    }

    @Test
    fun screen_whenUiStateLoading_showsLoading() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateLoading },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }
        composeTestRule.onNodeWithTag(dataLoading).assertExists()
    }

    @Test
    fun screen_whenUiStateSuccessNoData_showsNotFound() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessNoData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }
        composeTestRule.onNodeWithTag(dataNotFound).assertExists()
    }

    @Test
    fun screen_whenUiStateSuccessHasData_showsEntryScreen() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }
        composeTestRule.onNodeWithTag(dataFound).assertExists()
    }

    @Test
    fun screen_whenSaveFromEditMode_callsOnSaveAndExitsEditMode() {
        var saved = false
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData },
                onBack = { },
                onEdit = { },
                onSave = { saved = true },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }
        getParentOfNodeByCD(edit).performClick()
        getParentOfNodeByCD(save).performClick()
        assertThat(saved).isTrue()

        composeTestRule.onNodeWithContentDescription(exitEdit).assertDoesNotExist()
    }

    @Test
    fun screen_whenFavorite_callsOnFavorite() {
        var isFavorite = false
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { isFavorite = it },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }
        getParentOfNodeByCD(favorite).performClick()
        assertThat(isFavorite).isTrue()
    }

    @Test
    fun uiState_whenToggleEditMode_updatesEditingCopy() {
        var copy: Day? = null
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData },
                onBack = { },
                onEdit = { copy = it },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }
        getParentOfNodeByCD(edit).performClick()
        assertThat(copy).isEqualTo((uiStateSuccessHasFilledData.dayUiState as DayUiState.Success).data)

        getParentOfNodeByCD(exitEdit).performClick()
        assertThat(copy).isNull()
    }

    @Test
    fun toggles_whenSelected_updatesDisplayedSection() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }

        val day = (uiStateSuccessHasFilledData.dayUiState as DayUiState.Success).data!!

        getParentOfNodeByCD(summaryBtn).performClick()
        composeTestRule.onNodeWithText(day.properties.summary).assertIsDisplayed()

        val locationName =
            FRD.locationSourceData.find { it.properties.id == day.location!!.locationId }!!.properties.name
        getParentOfNodeByCD(locationBtn).performClick()
        composeTestRule.onNodeWithText(locationName).assertIsDisplayed()

        val tagName =
            FRD.tagSourceData.find { it.properties.id == day.tags[0].tagId }!!.properties.name
        getParentOfNodeByCD(tagsBtn).performClick()
        composeTestRule.onNodeWithText(tagName).assertIsDisplayed()
    }

    @Test
    fun summary_whenViewModeAndBlankSummary_showPlaceholderText() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasBlankData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }

        getParentOfNodeByCD(getString(R.string.entries_single_summary_btn)).performClick()
        composeTestRule.onNodeWithText(getString(R.string.entries_single_summary_placeholder))
            .assertIsDisplayed()
    }

    @Test
    fun summary_whenViewModeAndHasSummary_showsSummary() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }

        getParentOfNodeByCD(getString(R.string.entries_single_summary_btn)).performClick()
        val summary =
            (uiStateSuccessHasFilledData.dayUiState as DayUiState.Success).data!!.properties.summary
        composeTestRule.onNodeWithText(summary).assertExists()
    }

    @Test
    fun summary_whenEnterEditMode_autofocuses() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }

        val day = (uiStateSuccessHasFilledData.dayUiState as DayUiState.Success).data!!

        composeTestRule.onNodeWithContentDescription(edit).performClick()
        composeTestRule.onNodeWithText(day.properties.summary).assertIsFocused()
    }

    @Test
    fun summary_whenChangeText_callsOnEditAndUpdatesText() {
        val modified = MutableStateFlow<Day?>(null)
        composeTestRule.setContent {
            val editingCopy = modified.collectAsState()
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData.copy(editingCopy = editingCopy.value) },
                onBack = { },
                onEdit = { modified.tryEmit(it) },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }

        val day = (uiStateSuccessHasFilledData.dayUiState as DayUiState.Success).data!!

        composeTestRule.onNodeWithContentDescription(edit).performClick()
        composeTestRule.onNodeWithTag(summarySection).performTextClearance()
        composeTestRule.onNodeWithTag(summarySection).assertTextContains("")

        assertThat(modified.value).isEqualTo(day.copy(properties = day.properties.copy(summary = "")))
    }

    @Test
    fun location_whenViewModeAndNoLocation_showsPlaceholderText() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasBlankData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }

        composeTestRule.onNodeWithContentDescription(locationBtn).performClick()
        composeTestRule.onNodeWithText(getString(R.string.entries_single_location_placeholder))
            .assertIsDisplayed()
    }

    @Test
    fun location_whenViewModeAndHasLocation_showsLocation() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }
        val day = (uiStateSuccessHasFilledData.dayUiState as DayUiState.Success).data!!

        composeTestRule.onNodeWithContentDescription(locationBtn).performClick()
        val locationName =
            FRD.locationSourceData.find { it.properties.id == day.location!!.locationId }!!.properties.name
        getParentOfNodeByCD(locationBtn).performClick()
        composeTestRule.onNodeWithText(locationName).assertIsDisplayed()
    }

    @Test
    fun location_whenEditModeAndSelectLocation_callsOnEdit() {
        val modified = MutableStateFlow<Day?>(null)
        composeTestRule.setContent {
            val editingCopy = modified.collectAsState()
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasBlankData.copy(editingCopy = editingCopy.value) },
                onBack = { },
                onEdit = { modified.tryEmit(it) },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }

        val day = (uiStateSuccessHasBlankData.dayUiState as DayUiState.Success).data!!
        val editor = getString(R.string.tt_entries_single_location_selector)

        composeTestRule.onNodeWithContentDescription(edit).performClick()
        composeTestRule.onNodeWithContentDescription(locationBtn).performClick()
        composeTestRule.onNodeWithTag(editor).performClick()

        val locationName = FRD.locationSourceData[0].properties.name

        composeTestRule.onNodeWithText(locationName).performClick()
        composeTestRule.onNodeWithTag(editor).printToLog("TAG")
        composeTestRule.onNodeWithText(locationName).assertIsDisplayed()

        assertThat(modified.value).isEqualTo(
            day.copy(
                location = LocationEntry(
                    dayId = day.properties.id, locationId = FRD.locationSourceData[0].properties.id
                )
            )
        )
    }

    @Test
    fun tags_whenViewModeAndNoTags_showsPlaceholderText() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasBlankData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }

        composeTestRule.onNodeWithContentDescription(tagsBtn).performClick()
        composeTestRule.onNodeWithText(getString(R.string.entries_single_tags_placeholder))
            .assertIsDisplayed()
    }

    @Test
    fun tags_whenViewModeAndHasTags_showsTags() {
        composeTestRule.setContent {
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasFilledData },
                onBack = { },
                onEdit = { },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }

        val day = (uiStateSuccessHasFilledData.dayUiState as DayUiState.Success).data!!

        val tagName =
            FRD.tagSourceData.find { it.properties.id == day.tags[0].tagId }!!.properties.name
        getParentOfNodeByCD(tagsBtn).performClick()
        composeTestRule.onNodeWithText(tagName).assertIsDisplayed()
    }

    @Test
    fun tags_whenSelectTag_callsOnEdit() {
        val modified = MutableStateFlow<Day?>(null)
        composeTestRule.setContent {
            val editingCopy = modified.collectAsState()
            EntriesSingleScreen(uiStateProvider = { uiStateSuccessHasBlankData.copy(editingCopy = editingCopy.value) },
                onBack = { },
                onEdit = { modified.tryEmit(it) },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {})
        }
        val day = (uiStateSuccessHasBlankData.dayUiState as DayUiState.Success).data!!

        composeTestRule.onNodeWithContentDescription(tagsBtn).performClick()
        composeTestRule.onNodeWithContentDescription(edit).performClick()

        composeTestRule.onNodeWithContentDescription(getString(R.string.entries_single_tag_add))
            .performClick()
        composeTestRule.onNodeWithText(getString(R.string.entries_single_tag_add_select_existing))
            .performClick()
        composeTestRule.onNodeWithTag(getString(R.string.tt_entries_single_tag_selector))
            .performClick()
        composeTestRule.onNodeWithText(FRD.tagSourceData[0].properties.name).performClick()
        composeTestRule.onNodeWithText(dialogOk).performClick()

        assertThat(modified.value).isEqualTo(
            day.copy(
                tags = listOf(
                    TagEntry(
                        dayId = day.properties.id, tagId = FRD.tagSourceData[0].properties.id
                    )
                )
            )
        )
    }

    @Test
    fun tags_whenAddTag_callsOnAddTagAndOnEdit() {
        val modified = MutableStateFlow<Day?>(null)
        val tags = MutableStateFlow<List<TagProperties>>(emptyList())
        composeTestRule.setContent {
            val editingCopy = modified.collectAsState()
            val tagsState = tags.collectAsState()
            EntriesSingleScreen(uiStateProvider = {
                uiStateSuccessHasBlankData.copy(
                    editingCopy = editingCopy.value,
                    tagsUiState = TagsUiState.Success(tagsState.value)
                )
            },
                onBack = { },
                onEdit = { modified.tryEmit(it) },
                onSave = { },
                onFavorite = { },
                onAddLocation = { _, _ -> },
                onAddTag = {
                    tags.tryEmit(tags.value + TagProperties(id = 1001, name = it, lastUsedAt = 0))
                })
        }
        val day = (uiStateSuccessHasBlankData.dayUiState as DayUiState.Success).data!!
        val newTagName = "OnAddTag"

        composeTestRule.onNodeWithContentDescription(tagsBtn).performClick()
        composeTestRule.onNodeWithContentDescription(edit).performClick()

        composeTestRule.onNodeWithContentDescription(getString(R.string.entries_single_tag_add))
            .performClick()
        composeTestRule.onNodeWithText(getString(R.string.entries_single_tag_add_create_new))
            .performClick()
        composeTestRule.onNodeWithTag(getString(R.string.tt_entries_single_tag_creator))
            .performTextInput(newTagName)
        composeTestRule.onNodeWithText(dialogOk).performClick()

        composeTestRule.onNodeWithText(dialogOk).assertDoesNotExist()
        assertThat(tags.value.map { it.name }).contains(newTagName)
        val tagId = tags.value.find { it.name == newTagName }!!.id
        assertThat(modified.value).isEqualTo(
            day.copy(
                tags = listOf(TagEntry(dayId = day.properties.id, tagId = tagId))
            )
        )
    }

    private fun getString(@StringRes resId: Int) =
        composeTestRule.activity.resources.getString(resId)

    private fun getParentOfNodeByCD(contentDesc: String) =
        composeTestRule.onNodeWithContentDescription(contentDesc, useUnmergedTree = true).onParent()

}

private val uiStateLoading = EntriesSingleUiState(
    dayId = FRD.dayIds[0],
    dayUiState = DayUiState.Loading,
    locationsUiState = LocationsUiState.Loading,
    tagsUiState = TagsUiState.Loading,
    editingCopy = null
)

private val uiStateSuccessNoData = EntriesSingleUiState(
    dayId = FRD.dayIds[0],
    dayUiState = DayUiState.Success(null),
    locationsUiState = LocationsUiState.Loading,
    tagsUiState = TagsUiState.Loading,
    editingCopy = null
)

private val uiStateSuccessHasBlankData = EntriesSingleUiState(
    dayId = FRD.emptyDay.properties.id,
    dayUiState = DayUiState.Success(FRD.emptyDay),
    locationsUiState = LocationsUiState.Success(FRD.locationSourceData.map { it.properties }),
    tagsUiState = TagsUiState.Success(FRD.tagSourceData.map { it.properties }),
    editingCopy = null
)

private val uiStateSuccessHasFilledData = EntriesSingleUiState(
    dayId = FRD.filledDay.properties.id,
    dayUiState = DayUiState.Success(FRD.filledDay),
    locationsUiState = LocationsUiState.Success(FRD.locationSourceData.map { it.properties }),
    tagsUiState = TagsUiState.Success(FRD.tagSourceData.map { it.properties }),
    editingCopy = null
)