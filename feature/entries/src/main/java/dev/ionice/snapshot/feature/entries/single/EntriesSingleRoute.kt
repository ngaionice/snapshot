package dev.ionice.snapshot.feature.entries.single

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
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.model.ContentTag
import dev.ionice.snapshot.core.model.Coordinates
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.model.Location
import dev.ionice.snapshot.core.navigation.Navigator
import dev.ionice.snapshot.core.ui.DayUiState
import dev.ionice.snapshot.core.ui.LocationsUiState
import dev.ionice.snapshot.core.ui.TagsUiState
import dev.ionice.snapshot.core.ui.components.BackButton
import dev.ionice.snapshot.core.ui.screens.ErrorScreen
import dev.ionice.snapshot.core.ui.screens.LoadingScreen
import dev.ionice.snapshot.feature.entries.R
import dev.ionice.snapshot.feature.entries.single.components.*
import dev.ionice.snapshot.feature.entries.EntriesSingleUiState
import dev.ionice.snapshot.feature.entries.EntriesViewModel
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
internal fun EntriesSingleScreen(
    uiStateProvider: () -> EntriesSingleUiState,
    onBack: () -> Unit,
    onEdit: (Day?) -> Unit,
    onSave: () -> Unit,
    onFavorite: (Boolean) -> Unit,
    onAddLocation: (String, Coordinates) -> Unit,
    onAddTag: (String) -> Unit
) {
    val uiState = uiStateProvider()
    if (uiState.dayId == null) {
        LoadingScreen()
        return
    }

    when (uiState.dayUiState) {
        is DayUiState.Loading -> {
            LoadingScreen(testTag = stringResource(R.string.single_loading))
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
private fun EntryScreen(
    day: Day,
    editingCopy: Day?,
    locationProvider: () -> LocationsUiState,
    tagProvider: () -> TagsUiState,
    onBack: () -> Unit,
    onEdit: (Day?) -> Unit,
    onSave: () -> Unit,
    onFavorite: (Boolean) -> Unit,
    onAddLocation: (String, Coordinates) -> Unit,
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
                    contentDescription = stringResource(R.string.single_save)
                )
            }
        } else {
            IconButton(onClick = {
                onToggleEdit(true)
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.single_edit)
                )
            }
            IconButton(onClick = { onFavorite(!day.isFavorite) }) {
                Icon(
                    imageVector = if (day.isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Filled.FavoriteBorder
                    },
                    contentDescription = stringResource(R.string.single_favorite)
                )
            }
        }
    }

    val summaryText = if (editing && editingCopy != null) {
        editingCopy.summary
    } else {
        day.summary
    }
    val onSummaryChange: (String) -> Unit = { text ->
        editingCopy?.let { onEdit(it.copy(summary = text)) }
    }

    val location = if (editing && editingCopy != null) {
        editingCopy.location
    } else {
        day.location
    }
    val onLocationChange: (Location) -> Unit = { loc ->
        editingCopy?.let {
            onEdit(it.copy(location = loc))
        }
    }
    val tags = if (editing && editingCopy != null) {
        editingCopy.tags
    } else {
        day.tags
    }
    val onTagsChange: (List<ContentTag>) -> Unit = { newTags ->
        editingCopy?.let { onEdit(it.copy(tags = newTags)) }
    }

    BackHandler(enabled = editing) { onToggleEdit(false) }

    val tt = stringResource(R.string.tt_single_entry)

    Scaffold(
        modifier = modifier.semantics { testTag = tt },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(
                        backAction,
                        contentDesc = if (editing) stringResource(R.string.single_exit_edit) else null
                    )
                },
                actions = { actionButtons() }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            EntryHeader(dayId = day.id)
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
private fun NotFoundScreen(dayId: Long, onBack: () -> Unit) {
    val tt = stringResource(R.string.tt_single_not_found)
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
        descriptionResId = R.string.single_summary
    ),
    Location(
        label = "Location",
        icon = Icons.Filled.LocationOn,
        descriptionResId = R.string.single_location
    ),
    Tags(
        label = "Tags",
        icon = Icons.Filled.Tag,
        descriptionResId = R.string.single_tags
    )
}


