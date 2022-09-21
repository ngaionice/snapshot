package me.ionice.snapshot.ui.entries.single

import androidx.activity.compose.BackHandler
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
import androidx.hilt.navigation.compose.hiltViewModel
import me.ionice.snapshot.data.database.model.*
import me.ionice.snapshot.ui.common.DayUiState
import me.ionice.snapshot.ui.common.LocationsUiState
import me.ionice.snapshot.ui.common.TagsUiState
import me.ionice.snapshot.ui.common.components.BackButton
import me.ionice.snapshot.ui.common.screens.ErrorScreen
import me.ionice.snapshot.ui.common.screens.LoadingScreen
import me.ionice.snapshot.ui.entries.EntriesSingleUiState
import me.ionice.snapshot.ui.entries.EntriesViewModel
import me.ionice.snapshot.ui.entries.single.components.*
import me.ionice.snapshot.ui.navigation.Navigator
import me.ionice.snapshot.utils.Utils
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
            LoadingScreen()
        }
        is DayUiState.Error -> {
            ErrorScreen()
        }
        is DayUiState.Success -> {
            if (uiState.dayUiState.data == null) {
                NotFoundScreen(dayId = uiState.dayId, onBack = onBack)
            } else {
                EntryScreen(
                    day = uiState.dayUiState.data,
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
    day: Day,
    editingCopy: Day?,
    locationProvider: () -> LocationsUiState,
    tagProvider: () -> TagsUiState,
    onBack: () -> Unit,
    onEdit: (Day?) -> Unit,
    onSave: () -> Unit,
    onFavorite: (Boolean) -> Unit,
    onAddLocation: (String, Coordinates) -> Unit,
    onAddTag: (String) -> Unit
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
                Icon(imageVector = Icons.Filled.Save, contentDescription = "save")
            }
        } else {
            IconButton(onClick = {
                onToggleEdit(true)
            }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit")
            }
            IconButton(onClick = { onFavorite(!day.properties.isFavorite) }) {
                Icon(
                    imageVector = if (day.properties.isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Filled.FavoriteBorder
                    },
                    contentDescription = "favorite"
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
    val onLocationChange: (LocationProperties) -> Unit = { loc ->
        editingCopy?.let {
            onEdit(
                it.copy(
                    location = LocationEntry(dayId = it.properties.id, locationId = loc.id)
                )
            )
        }
    }
    val tags = if (editing && editingCopy != null) {
        editingCopy.tags
    } else {
        day.tags
    }
    val onTagsChange: (List<TagEntry>) -> Unit = { newTags ->
        editingCopy?.let { onEdit(it.copy(tags = newTags)) }
    }

    BackHandler(enabled = editing) { onToggleEdit(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(backAction) },
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
    Scaffold(
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

enum class EntrySection(val description: String, val icon: ImageVector) {
    Summary(description = "Summary", icon = Icons.Filled.TextSnippet),
    Location(description = "Location", icon = Icons.Filled.LocationOn),
    Tags(description = "Tags", icon = Icons.Filled.Tag)
}


