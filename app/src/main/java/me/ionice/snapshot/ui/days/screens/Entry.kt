//package me.ionice.snapshot.ui.days.screens
//
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import me.ionice.snapshot.data.database.model.LocationEntry
//import me.ionice.snapshot.data.database.model.TagEntry
//import me.ionice.snapshot.ui.common.screens.LoadingScreen
//import me.ionice.snapshot.ui.days.DayEntryUiState
//import me.ionice.snapshot.ui.days.DayEntryViewModel
//
//@Composable
//fun EntryRoute(viewModel: DayEntryViewModel, dayId: Long, onBack: () -> Unit) {
//
//    val uiState by viewModel.uiState.collectAsState()
//    var isEditing by rememberSaveable { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        viewModel.loadDay(dayId)
//    }
//
//    when (uiState) {
//        is DayEntryUiState.Loading -> {
//            LoadingScreen()
//        }
//        is DayEntryUiState.EntryNotFound -> {
//            EntryNotAvailableScreen(
//                uiState = uiState as DayEntryUiState.EntryNotFound,
//                onDayAdd = viewModel::insertDay,
//                onBack = onBack
//            )
//        }
//        is DayEntryUiState.EntryFound -> {
//            EntryScreen(
//                uiState = uiState as DayEntryUiState.EntryFound,
//                isEditing = isEditing,
//                setIsEditing = { isEditing = it },
//                onBack = onBack,
//                onSave = viewModel::saveDay,
//                onLocationChange = viewModel::setLocation,
//                onSummaryChange = viewModel::setSummary,
//                onTagAdd = viewModel::addTag,
//                onTagChange = viewModel::updateTag,
//                onTagDelete = viewModel::removeTag
//            )
//        }
//    }
//}
//
//@Composable
//fun EntryScreen(
//    uiState: DayEntryUiState.EntryFound,
//    isEditing: Boolean,
//    setIsEditing: (Boolean) -> Unit,
//    onBack: () -> Unit,
//    onSave: () -> Unit,
//    onLocationChange: (LocationEntry?) -> Unit,
//    onSummaryChange: (String) -> Unit,
//    onTagAdd: (TagEntry) -> Unit,
//    onTagChange: (Int, String) -> Unit,
//    onTagDelete: (TagEntry) -> Unit
//) {
//
//    if (isEditing) {
//        EditScreen(
//            uiState = uiState,
//            onLocationChange = onLocationChange,
//            onSummaryChange = onSummaryChange,
//            onTagAdd = onTagAdd,
//            onTagDelete = onTagDelete,
//            onTagChange = onTagChange,
//            onSave = {
//                onSave()
//                setIsEditing(false)
//            },
//            onBack = { setIsEditing(false) })
//    } else {
//        ViewScreen(uiState = uiState, onEdit = { setIsEditing(true) }, onBack = onBack)
//    }
//}