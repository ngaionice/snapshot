package dev.ionice.snapshot.feature.search.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.model.Location
import dev.ionice.snapshot.core.model.Tag
import dev.ionice.snapshot.core.ui.screens.ErrorScreen
import dev.ionice.snapshot.core.ui.screens.LoadingScreen
import dev.ionice.snapshot.feature.search.*
import dev.ionice.snapshot.feature.search.R

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun FiltersBottomSheet(
    filters: Filters,
    setFilters: (Filters) -> Unit,
    locationsUiStateProvider: () -> LocationsUiState,
    tagsUiStateProvider: () -> TagsUiState,
    onShowDateRangePicker: () -> Unit,
    onCloseSheet: () -> Unit,
    sheetState: ModalBottomSheetState,
    focusRequester: FocusRequester,
    contentType: FilterType
) {

    val locationsUiState = locationsUiStateProvider()
    val tagsUiState = tagsUiStateProvider()

    BottomSheetLayout(
        sheetState = sheetState,
        focusRequester = focusRequester,
        filterType = contentType
    ) {
        Column {
            ListItem(
                headlineContent = {
                    Text(
                        when (contentType) {
                            FilterType.DATE -> stringResource(R.string.date_filter_label)
                            FilterType.LOCATION -> stringResource(R.string.location_filter_label)
                            FilterType.TAG -> stringResource(R.string.tag_filter_label)
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingContent = {
                    Icon(
                        modifier = Modifier.clickable { onCloseSheet() },
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_close_bottom_sheet)
                    )
                }
            )
            HorizontalDivider()
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                when (contentType) {
                    FilterType.DATE -> {
                        DateFilterOptions(
                            dateFilter = filters.dateFilter,
                            setDateFilter = {
                                setFilters(filters.copy(dateFilter = it))
                                onCloseSheet()
                            },
                            onShowDateRangePicker = {
                                onShowDateRangePicker()
                                onCloseSheet()
                            }
                        )
                    }
                    FilterType.LOCATION -> {
                        when (locationsUiState) {
                            is LocationsUiState.Loading -> LoadingScreen()
                            is LocationsUiState.Error -> ErrorScreen(message = stringResource(R.string.locations_load_error_msg))
                            is LocationsUiState.Success -> LocationFilterOptions(
                                selected = filters.locationFilters,
                                allLocations = locationsUiState.data,
                                onSelectLocationFilters = {
                                    setFilters(filters.copy(locationFilters = it))
                                    onCloseSheet()
                                }
                            )
                        }
                    }
                    FilterType.TAG -> {
                        when (tagsUiState) {
                            is TagsUiState.Loading -> LoadingScreen()
                            is TagsUiState.Error -> ErrorScreen(message = stringResource(R.string.tags_load_error_msg))
                            is TagsUiState.Success -> TagFilterOptions(
                                selected = filters.tagFilters,
                                allTags = tagsUiState.data,
                                onSelectTagFilters = {
                                    setFilters(filters.copy(tagFilters = it))
                                    onCloseSheet()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    BackHandler(enabled = sheetState.isVisible) {
        onCloseSheet()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BottomSheetLayout(
    sheetState: ModalBottomSheetState,
    focusRequester: FocusRequester,
    filterType: FilterType,
    sheetContent: @Composable () -> Unit
) {
    val cd = stringResource(R.string.cd_bottom_sheet)
    ModalBottomSheetLayout(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable()
            .semantics { contentDescription = cd },
        sheetState = sheetState,
        sheetShape = if (filterType == FilterType.DATE) {
            RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
        } else {
            RectangleShape
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            Box(modifier = Modifier.defaultMinSize(minHeight = 1.dp)) {
                sheetContent()
            }
        }
    ) {}
}

@Composable
private fun DateFilterOptions(
    dateFilter: DateFilter,
    setDateFilter: (DateFilter) -> Unit,
    onShowDateRangePicker: () -> Unit
) {
    DateFilterOptionItem(
        selected = dateFilter is DateFilter.Any,
        displayText = stringResource(R.string.date_any_time)
    ) {
        setDateFilter(DateFilter.Any)
    }
    DateFilterOptionItem(
        selected = dateFilter == DateFilter.OlderThan.ONE_WEEK,
        displayText = stringResource(R.string.date_at_least_one_week)
    ) {
        setDateFilter(DateFilter.OlderThan.ONE_WEEK)
    }
    DateFilterOptionItem(
        selected = dateFilter == DateFilter.OlderThan.ONE_MONTH,
        displayText = stringResource(R.string.date_at_least_one_month)
    ) {
        setDateFilter(DateFilter.OlderThan.ONE_MONTH)
    }
    DateFilterOptionItem(
        selected = dateFilter == DateFilter.OlderThan.SIX_MONTHS,
        displayText = stringResource(R.string.date_at_least_six_months)
    ) {
        setDateFilter(DateFilter.OlderThan.SIX_MONTHS)
    }
    DateFilterOptionItem(
        selected = dateFilter == DateFilter.OlderThan.ONE_YEAR,
        displayText = stringResource(R.string.date_at_least_one_year)
    ) {
        setDateFilter(DateFilter.OlderThan.ONE_YEAR)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowDateRangePicker() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.offset(x = 2.dp),
            text = stringResource(R.string.date_custom_range)
        )
    }
}

@Composable
private fun DateFilterOptionItem(
    selected: Boolean,
    displayText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text = displayText)
    }
}

@Composable
private fun LocationFilterOptions(
    selected: Set<Location>,
    allLocations: List<Location>,
    onSelectLocationFilters: (Set<Location>) -> Unit
) {
    SearchableFilterOptions(
        options = allLocations.map {
            Triple(
                it.id,
                it.name,
                selected.contains(it)
            )
        },
        onSelect = { id, isSelected ->
            val location = allLocations.filter { it.id == id }[0]
            onSelectLocationFilters(if (isSelected) selected + location else selected - location)
        },
        searchPlaceholder = stringResource(R.string.locations_filter_placeholder),
        searchContentDescription = stringResource(R.string.cd_bottom_sheet_locations_filter_text_field)
    )
}

@Composable
private fun TagFilterOptions(
    selected: Set<Tag>,
    allTags: List<Tag>,
    onSelectTagFilters: (Set<Tag>) -> Unit
) {
    SearchableFilterOptions(
        options = allTags.map {
            Triple(
                it.id,
                it.name,
                selected.contains(it)
            )
        },
        onSelect = { id, isSelected ->
            val tag = allTags.filter { it.id == id }[0]
            onSelectTagFilters(if (isSelected) selected + tag else selected - tag)
        },
        searchPlaceholder = stringResource(R.string.tags_filter_placeholder),
        searchContentDescription = stringResource(R.string.cd_bottom_sheet_tags_filter_text_field)
    )
}

/**
 * Takes in a list of Triples and renders a list of options that can be filtered and selected.
 *
 * @param options A list of [Triple]s;
 * first is the ID of the item; second is the label of the item; third is whether the item is selected
 */
@Composable
private fun SearchableFilterOptions(
    options: List<Triple<Long, String, Boolean>>,
    onSelect: (id: Long, selected: Boolean) -> Unit,
    searchPlaceholder: String,
    searchContentDescription: String
) {
    val (filterTerm, setFilterTerm) = remember { mutableStateOf("") }
    val displayedOptions = options
        .filter { it.second.contains(filterTerm, ignoreCase = true) }
        .sortedBy { it.second }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp)
        ) {
            if (filterTerm.isEmpty()) {
                Text(
                    text = searchPlaceholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            BasicTextField(
                value = filterTerm,
                onValueChange = setFilterTerm,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = searchContentDescription },
                singleLine = true,
                textStyle = TextStyle.Default.copy(
                    color = LocalContentColor.current,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontFamily = LocalTextStyle.current.fontFamily
                ),
                cursorBrush = SolidColor(LocalContentColor.current)
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            displayedOptions.forEach {
                item {
                    SelectableFilterOptionItem(
                        label = it.second,
                        checked = it.third,
                        setChecked = { isChecked -> onSelect(it.first, isChecked) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectableFilterOptionItem(
    label: String,
    checked: Boolean,
    setChecked: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { setChecked(!checked) }
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = setChecked)
        Text(text = label)
    }
}