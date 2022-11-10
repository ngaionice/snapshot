package dev.ionice.snapshot.feature.search.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.model.Location
import dev.ionice.snapshot.core.model.Tag
import dev.ionice.snapshot.feature.search.DateFilter
import dev.ionice.snapshot.feature.search.Filters
import dev.ionice.snapshot.feature.search.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchFilters(
    filters: Filters,
    onTriggerBottomSheet: (FilterType) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            val cd = stringResource(R.string.cd_filter_date)
            FilterChip(
                modifier = Modifier.semantics { contentDescription = cd },
                selected = filters.dateFilter !is DateFilter.Any,
                onClick = { onTriggerBottomSheet(FilterType.DATE) },
                label = { FilterChipText(getDateChipText(filters.dateFilter)) },
                trailingIcon = { DropdownIcon() }
            )
        }

        item {
            val cd = stringResource(R.string.cd_filter_location)
            FilterChip(
                modifier = Modifier.semantics { contentDescription = cd },
                selected = filters.locationFilters.isNotEmpty(),
                onClick = { onTriggerBottomSheet(FilterType.LOCATION) },
                label = { FilterChipText(getLocationsChipText(filters.locationFilters)) },
                trailingIcon = { DropdownIcon() }
            )
        }

        item {
            val cd = stringResource(R.string.cd_filter_tag)
            FilterChip(
                modifier = Modifier.semantics { contentDescription = cd },
                selected = filters.tagFilters.isNotEmpty(),
                onClick = { onTriggerBottomSheet(FilterType.TAG) },
                label = { FilterChipText(getTagsChipText(filters.tagFilters)) },
                trailingIcon = { DropdownIcon() }
            )
        }
    }
}

@Composable
private fun getDateChipText(dateFilter: DateFilter): String {
    return when (dateFilter) {
        is DateFilter.Any -> stringResource(R.string.date_filter_label)
        DateFilter.OlderThan.ONE_WEEK -> stringResource(R.string.date_at_least_one_week)
        DateFilter.OlderThan.ONE_MONTH -> stringResource(R.string.date_at_least_one_month)
        DateFilter.OlderThan.SIX_MONTHS -> stringResource(R.string.date_at_least_six_months)
        DateFilter.OlderThan.ONE_YEAR -> stringResource(R.string.date_at_least_one_year)
        is DateFilter.Custom -> {
            val fmtr = Utils.numericDateFormatter
            "${fmtr.format(dateFilter.startDate)}—${fmtr.format(dateFilter.endDate)}"
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun getLocationsChipText(locations: Set<Location>): String {
    return when (locations.size) {
        0 -> stringResource(R.string.location_filter_label)
        1 -> locations.first().name
        else -> pluralStringResource(id = R.plurals.location_chip_count, count = locations.size, locations.size)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun getTagsChipText(tags: Set<Tag>): String {
    return when (tags.size) {
        0 -> stringResource(R.string.tag_filter_label)
        1 -> tags.first().name
        else -> pluralStringResource(id = R.plurals.tag_chip_count, count = tags.size, tags.size)
    }
}

@Composable
private fun FilterChipText(text: String) {
    Text(modifier = Modifier.offset(y = (-1).dp), text = text)
}

@Composable
private fun DropdownIcon() {
    Icon(
        modifier = Modifier.size(16.dp),
        imageVector = Icons.Filled.ArrowDropDown,
        contentDescription = null
    )
}

internal enum class FilterType {
    DATE,
    LOCATION,
    TAG
}