package me.ionice.snapshot.ui.days.screens
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CalendarMonth
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.pluralStringResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import me.ionice.snapshot.R
//import me.ionice.snapshot.data.day.DayWithMetrics
//import me.ionice.snapshot.ui.common.components.AddFAB
//import me.ionice.snapshot.ui.common.screens.BaseScreen
//import me.ionice.snapshot.ui.common.components.DatePicker
//import me.ionice.snapshot.ui.common.components.SearchHeaderBar
//import me.ionice.snapshot.ui.days.DayListUiState
//import me.ionice.snapshot.ui.days.DayListViewModel
//import me.ionice.snapshot.ui.days.DaySearchQuery
//import me.ionice.snapshot.utils.Utils
//import java.time.LocalDate
//
//@Composable
//fun OldListRoute(viewModel: DayListViewModel, onSelectItem: (Long) -> Unit) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    ListScreen(
//        uiState = uiState,
//        onDaySelect = onSelectItem,
//        onDayAdd = {
//            viewModel.insertDay(it)
//            onSelectItem(it)
//        },
//        onSwitchYear = viewModel::switchYear,
//        onSearch = {
//            viewModel.search(
//                DaySearchQuery(
//                    uiState.year,
//                    it
//                )
//            )
//        },
//        onClearSearch = viewModel::clearSearch
//    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun ListScreen(
//    uiState: DayListUiState,
//    onDaySelect: (Long) -> Unit,
//    onDayAdd: (Long) -> Unit,
//    onSwitchYear: (Int) -> Unit,
//    onSearch: (String) -> Unit,
//    onClearSearch: () -> Unit
//) {
//    var showDatePicker by remember { mutableStateOf(false) }
//
//    BaseScreen(
//        headerBar = {
//            SearchBar(
//                year = uiState.year,
//                onSearch = onSearch,
//                onClearSearch = onClearSearch,
//                onSwitchYear = onSwitchYear
//            )
//        },
//        floatingActionButton = {
//            AddFAB(
//                onClick = { showDatePicker = true },
//                description = stringResource(R.string.day_screen_add_day)
//            )
//        })
//    {
//        EntryList(days = uiState.yearEntries, onDaySelect = onDaySelect)
//        if (showDatePicker) {
//            DatePicker(
//                onSelect = onDayAdd,
//                onDismissRequest = { showDatePicker = false })
//        }
//    }
//}
//
//@Composable
//private fun SearchBar(
//    year: Int,
//    onSearch: (String) -> Unit,
//    onClearSearch: () -> Unit,
//    onSwitchYear: (Int) -> Unit
//) {
//    SearchHeaderBar(
//        placeholderText = "Search daily summaries in $year",
//        onSearchStringChange = {
//            if (it.isNotEmpty()) {
//                onSearch(it)
//            } else {
//                onClearSearch()
//            }
//        },
//        onSearchBarActiveStateChange = {
//            // TODO: change system bar colors
//        },
//        leadingIcon = {
//            Icon(
//                Icons.Filled.Search,
//                contentDescription = null,
//                modifier = Modifier.padding(12.dp)
//            )
//        },
//        trailingIcon = {
//            SwitchYearButton(onSwitch = onSwitchYear)
//        })
//}
//
//@Composable
//private fun EntryList(
//    days: List<DayWithMetrics>,
//    modifier: Modifier = Modifier,
//    onDaySelect: (Long) -> Unit
//) {
//    if (days.isEmpty()) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text(
//                stringResource(R.string.common_no_results),
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }
//    } else {
//        LazyColumn(modifier = modifier) {
//            items(items = days, key = { day -> day.core.id }) { day ->
//                EntryListItem(day = day) { onDaySelect(day.core.id) }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//private fun EntryListItem(day: DayWithMetrics, onClick: () -> Unit) {
//    val date = LocalDate.ofEpochDay(day.core.id)
//    val location: String = day.core.location
//    val metricCount = day.metrics.size
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .clickable(onClick = onClick)
//            .padding(vertical = 16.dp, horizontal = 24.dp)
//    ) {
//        Column(
//            verticalArrangement = Arrangement.Center, modifier = Modifier
//                .weight(1f)
//                .fillMaxHeight()
//        ) {
//            Text(
//                text = Utils.dateFormatter.format(date),
//                style = MaterialTheme.typography.titleLarge
//            )
//            Text(
//                text = pluralStringResource(
//                    R.plurals.day_screen_metric_count,
//                    metricCount,
//                    metricCount
//                ),
//                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
//            )
//        }
//        Column(
//            verticalArrangement = Arrangement.Center,
////            TODO: move metric count and location to right; show summary preview on left
////            horizontalAlignment = Alignment.End,
//            modifier = Modifier
//                .weight(0.75f)
//                .fillMaxHeight()
//        ) {
//            Text(text = location, style = MaterialTheme.typography.labelLarge)
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun SwitchYearButton(onSwitch: (Int) -> Unit) {
//
//    var showDialog by rememberSaveable { mutableStateOf(false) }
//
//    val options = (LocalDate.now().year downTo LocalDate.ofEpochDay(0).year).toList()
//    var selected by rememberSaveable { mutableStateOf(if (options.isEmpty()) LocalDate.now().year else options[0]) }
//
//    IconButton(onClick = { showDialog = true }) {
//        Icon(
//            Icons.Filled.CalendarMonth,
//            contentDescription = stringResource(R.string.day_screen_switch_year)
//        )
//    }
//
//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            dismissButton = {
//                TextButton(onClick = { showDialog = false }) {
//                    Text(stringResource(id = R.string.common_dialog_cancel))
//                }
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    onSwitch(selected)
//                    showDialog = false
//                }) {
//                    Text(stringResource(id = R.string.common_dialog_ok))
//                }
//            },
//            text = {
//
//                // TODO: find a better way to select year,
//                //  as ExposedDropDownMenu uses ColumnScope and we have a lot of items,
//                //  leading to performance issues
//                var expanded by remember { mutableStateOf(false) }
//
//                ExposedDropdownMenuBox(
//                    expanded = expanded,
//                    onExpandedChange = { expanded = !expanded }) {
//                    TextField(
//                        readOnly = true,
//                        value = selected.toString(),
//                        onValueChange = {},
//                        label = { Text("Year") },
//                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
//                    )
//                    ExposedDropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false },
//                    ) {
//                        options.forEach { selectionOption ->
//                            DropdownMenuItem(
//                                text = { Text(selectionOption.toString()) },
//                                onClick = {
//                                    selected = selectionOption
//                                    expanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//            })
//    }
//}