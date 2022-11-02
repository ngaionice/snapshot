package dev.ionice.snapshot.ui.entries.single

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import dev.ionice.snapshot.R
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.database.model.*
import dev.ionice.snapshot.core.ui.DayUiState
import dev.ionice.snapshot.core.ui.LocationsUiState
import dev.ionice.snapshot.core.ui.TagsUiState
import dev.ionice.snapshot.core.ui.components.BackButton
import dev.ionice.snapshot.core.ui.screens.ErrorScreen
import dev.ionice.snapshot.core.ui.screens.LoadingScreen
import dev.ionice.snapshot.ui.entries.EntriesSingleUiState
import dev.ionice.snapshot.ui.entries.EntriesViewModel
import dev.ionice.snapshot.ui.entries.single.components.*
import dev.ionice.snapshot.core.navigation.Navigator
import java.time.LocalDate

@Composable
fun EntriesSingleRoute(
    viewModel: EntriesViewModel = hiltViewModel(),
    dayId: Long,
    navigator: Navigator
) {
    val uiState by viewModel.singleUiState.collectAsState()

    LaunchedEffect(dayId) { viewModel.load(dayId) }

    EntriesSingleScreen(
        uiStateProvider = { uiState },
        onBack = navigator::navigateBack,
        onEdit = viewModel::edit,
        onSave = viewModel::save,
        onFavorite = viewModel::favorite,
        onAddLocation = viewModel::addLocation,
        onAddTag = viewModel::addTag
    )
}

@VisibleForTesting
@Composable
fun EntriesSingleScreen(
    uiStateProvider: () -> EntriesSingleUiState,
    onBack: () -> Unit,
    onEdit: (DayEntity?) -> Unit,
    onSave: () -> Unit,
    onFavorite: (Boolean) -> Unit,
    onAddLocation: (String, CoordinatesEntity) -> Unit,
    onAddTag: (String) -> Unit
) {
    val uiState = uiStateProvider()
    if (uiState.dayId == null) {
        LoadingScreen()
        return
    }

    when (uiState.dayUiState) {
        is DayUiState.Loading -> {
            LoadingScreen(testTag = stringResource(R.string.entries_single_loading))
        }
        is DayUiState.Error -> {
            ErrorScreen()
        }
        is DayUiState.Success -> {
            if (uiState.dayUiState.data == null) {
                NotFoundScreen(dayId = uiState.dayId, onBack = onBack)
            } else {
                EntryScreen(
                    day = uiState.dayUiState.data!!,
                    locationProvider = { uiState.locationsUiState },
                    tagProvider = { uiState.tagsUiState },
                    editingCopy = uiState.editingCopy,
                    onBack = onBack,
                    onEdit = onEdit,
                    onSave = onSave,
                    onFavorite = onFavorite,
                    onAddLocation = onAddLocation,
                    onAddTag = onAddTag
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    day: DayEntity,
    editingCopy: DayEntity?,
    locationProvider: () -> LocationsUiState,
    tagProvider: () -> TagsUiState,
    onBack: () -> Unit,
    onEdit: (DayEntity?) -> Unit,
    onSave: () -> Unit,
    onFavorite: (Boolean) -> Unit,
    onAddLocation: (String, CoordinatesEntity) -> Unit,
    onAddTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (editing, setEditing) = rememberSaveable { mutableStateOf(false) }
    val (selectedSection, setSelectedSection) = rememberSaveable { mutableStateOf(EntrySection.Summary) }

    val onToggleEdit: (Boolean) -> Unit = {
        setEditing(it)
        if (it) onEdit(day)
        else onEdit(null)
    }
    val backAction: () -> Unit = {
        if (editing) onToggleEdit(false)
        else onBack()
    }
    val actionButtons: @Composable () -> Unit = {
        if (editing) {
            IconButton(onClick = {
                onSave()
                onToggleEdit(false)
            }) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = stringResource(R.string.entries_single_save)
                )
            }
        } else {
            IconButton(onClick = {
                onToggleEdit(true)
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.entries_single_edit)
                )
            }
            IconButton(onClick = { onFavorite(!day.properties.isFavorite) }) {
                Icon(
                    imageVector = if (day.properties.isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Filled.FavoriteBorder
                    },
                    contentDescription = stringResource(R.string.entries_single_favorite)
                )
            }
        }
    }

    val summaryText = if (editing && editingCopy != null) {
        editingCopy.properties.summary
    } else {
        day.properties.summary
    }
    val onSummaryChange: (String) -> Unit = { text ->
        editingCopy?.let { onEdit(it.copy(properties = it.properties.copy(summary = text))) }
    }

    val location = if (editing && editingCopy != null) {
        editingCopy.location
    } else {
        day.location
    }
    val onLocationChange: (LocationPropertiesEntity) -> Unit = { loc ->
        editingCopy?.let {
            onEdit(
                it.copy(
                    location = LocationEntryEntity(dayId = it.properties.id, locationId = loc.id)
                )
            )
        }
    }
    val tags = if (editing && editingCopy != null) {
        editingCopy.tags
    } else {
        day.tags
    }
    val onTagsChange: (List<TagEntryEntity>) -> Unit = { newTags ->
        editingCopy?.let { onEdit(it.copy(tags = newTags)) }
    }

    BackHandler(enabled = editing) { onToggleEdit(false) }

    val tt = stringResource(R.string.tt_entries_single_entry)

    Scaffold(
        modifier = modifier.semantics { testTag = tt },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(
                        backAction,
                        contentDesc = if (editing) stringResource(R.string.entries_single_exit_edit) else null
                    )
                },
                actions = { actionButtons() }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            EntryHeader(dayId = day.properties.id)
            EntrySectionToggle(
                selectedProvider = { selectedSection }, onSelect = setSelectedSection
            )
            when (selectedSection) {
                EntrySection.Summary -> {
                    EntrySummarySection(
                        editing = editing,
                        text = summaryText,
                        onTextChange = onSummaryChange
                    )
                }
                EntrySection.Location -> {
                    EntryLocationSection(
                        editing = editing,
                        uiStateProvider = locationProvider,
                        selectedLocation = location,
                        onSelectLocation = onLocationChange,
                        onAddLocation = onAddLocation
                    )
                }
                EntrySection.Tags -> {
                    EntryTagSection(
                        editing = editing,
                        dayId = day.properties.id,
                        uiStateProvider = tagProvider,
                        selectedTags = tags,
                        onAddTag = onAddTag,
                        onSelectedTagsChange = onTagsChange
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotFoundScreen(dayId: Long, onBack: () -> Unit) {
    val tt = stringResource(R.string.tt_entries_single_not_found)
    Scaffold(
        modifier = Modifier.semantics { testTag = tt },
        topBar = {
            TopAppBar(title = {}, navigationIcon = { BackButton(onBack = onBack) })
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Failed to find the entry for ${
                    LocalDate.ofEpochDay(dayId).format(Utils.dateFormatter)
                }."
            )
        }
    }
}

enum class EntrySection(
    val label: String,
    val icon: ImageVector,
    @StringRes val descriptionResId: Int
) {
    Summary(
        label = "Summary",
        icon = Icons.Filled.TextSnippet,
        descriptionResId = R.string.entries_single_summary_btn
    ),
    Location(
        label = "Location",
        icon = Icons.Filled.LocationOn,
        descriptionResId = R.string.entries_single_location_btn
    ),
    Tags(
        label = "Tags",
        icon = Icons.Filled.Tag,
        descriptionResId = R.string.entries_single_tags_btn
    )
}


