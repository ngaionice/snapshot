package me.ionice.snapshot.ui.days

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.R
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate

@Composable
fun SearchButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.day_screen_search))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchYearButton(onSwitch: (Int) -> Unit) {

    var showDialog by rememberSaveable { mutableStateOf(false) }

    val options = (LocalDate.now().year downTo LocalDate.ofEpochDay(0).year).toList()
    var selected by rememberSaveable { mutableStateOf(if (options.isEmpty()) LocalDate.now().year else options[0]) }

    IconButton(onClick = { showDialog = true }) {
        Icon(
            Icons.Filled.CalendarMonth,
            contentDescription = stringResource(R.string.day_screen_switch_year)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(id = R.string.common_dialog_cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSwitch(selected)
                    showDialog = false
                }) {
                    Text(stringResource(id = R.string.common_dialog_ok))
                }
            },
            text = {

                // TODO: find a better way to select year,
                //  as ExposedDropDownMenu uses ColumnScope and we have a lot of items,
                //  leading to performance issues
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }) {
                    TextField(
                        readOnly = true,
                        value = selected.toString(),
                        onValueChange = {},
                        label = { Text("Year") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        options.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.toString()) },
                                onClick = {
                                    selected = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            })
    }
}

@Composable
fun EntryList(
    days: List<DayWithMetrics>,
    modifier: Modifier = Modifier,
    onDaySelect: (Long) -> Unit
) {
    if (days.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.common_no_results),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(items = days, key = { day -> day.day.id }) { day ->
                EntryListItem(day = day) { onDaySelect(day.day.id) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun EntryListItem(day: DayWithMetrics, onClick: () -> Unit) {
    val date = LocalDate.ofEpochDay(day.day.id)
    val location: String = day.day.location
    val metricCount = day.metrics.size

    Card(modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Column(
                verticalArrangement = Arrangement.Center, modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = Utils.dateFormatter.format(date),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.day_screen_metric_count,
                        metricCount,
                        metricCount
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
            ) {
                Text(text = location, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

