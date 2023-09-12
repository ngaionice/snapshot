package dev.ionice.snapshot.feature.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.ionice.snapshot.core.common.Utils
import dev.ionice.snapshot.core.model.Day
import dev.ionice.snapshot.core.ui.components.PageSection
import dev.ionice.snapshot.feature.search.R
import java.time.LocalDate

@Composable
internal fun FullResults(
    searchString: String,
    results: List<Day>,
    onSelectEntry: (Long) -> Unit
) {
    if (results.isEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.search_no_results_msg))
        }
        return
    }

    val currentYear = LocalDate.now().year

    PageSection(
        title = stringResource(R.string.results_header),
        headerTextColor = MaterialTheme.colorScheme.onSurface
    ) {
        LazyColumn {
            results.forEach {
                item {
                    val date = LocalDate.ofEpochDay(it.id)
                    ResultListItem(
                        date = date,
                        summary = it.summary,
                        searchString = searchString,
                        showShortDate = date.year == currentYear,
                        onClick = { onSelectEntry(it.id) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun QuickResults(
    searchString: String,
    resultsProvider: () -> List<Day>,
    onSelectEntry: (Long) -> Unit
) {
    val results = resultsProvider()
    if (results.isEmpty()) return

    val currentYear = LocalDate.now().year

    PageSection(
        title = stringResource(R.string.quick_results_header),
        headerTextColor = MaterialTheme.colorScheme.onSurface
    ) {
        LazyColumn {
            results.forEach {
                val date = LocalDate.ofEpochDay(it.id)
                item {
                    ResultListItem(
                        date = date,
                        summary = it.summary,
                        searchString = searchString,
                        showShortDate = date.year == currentYear,
                        onClick = { onSelectEntry(it.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultListItem(
    date: LocalDate,
    summary: String,
    searchString: String,
    showShortDate: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        headlineContent = {
            Text(date.format(if (showShortDate) Utils.shortDateFormatter else Utils.dateFormatter))
        },
        supportingContent = {
            Text(
                text = getSearchResultDisplayText(searchString, summary),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    )
}

/**
 * @param summary The summary to be extracted for display text. If it does not contain [searchString], an empty string will be returned.
 */
private fun getSearchResultDisplayText(searchString: String, summary: String): String {
    val firstValidToken =
        searchString.split(" ").filter { it.isNotEmpty() && it[0] != '-' }.getOrNull(0)

    val targetIdx =
        firstValidToken?.let { summary.indexOf(string = it, ignoreCase = true) } ?: return ""

    // Get the index of the 2nd last word before the search term
    // Why 2nd last word? Copied from Gmail, they probably determined w/ research that the 2nd last word provides enough context
    val get2ndLastWordStartIdx: (String, Int) -> Int = { string, startIdx ->
        var count = 0
        var idx = startIdx

        while (idx > 0 && count < 3) {
            if (string[idx].isWhitespace()) {
                count += 1
                if (count == 3) break
            }
            idx -= 1
        }

        if (idx != 0) idx + 1 else 0
    }

    return summary.substring(get2ndLastWordStartIdx(summary, targetIdx))
}