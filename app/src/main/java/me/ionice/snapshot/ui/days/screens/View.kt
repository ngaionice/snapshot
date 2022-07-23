package me.ionice.snapshot.ui.days.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.R
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey
import me.ionice.snapshot.ui.common.BackButton
import me.ionice.snapshot.ui.common.BaseScreen
import me.ionice.snapshot.ui.common.SectionHeader
import me.ionice.snapshot.ui.days.DayEntryUiState
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScreen(
    uiState: DayEntryUiState.EntryFound,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    BaseScreen(
        headerText = LocalDate.ofEpochDay(uiState.dayId).format(Utils.dateFormatter),
        navigationIcon = { BackButton(onBack = onBack) },
        floatingActionButton = { EditFAB(onClick = onEdit) })
    {
        ViewScreenContent(
            location = uiState.location,
            summary = uiState.summary,
            metrics = uiState.metrics,
            metricKeys = uiState.metricKeys
        )
    }
}

@Composable
private fun ViewScreenContent(
    location: String,
    summary: String,
    metrics: List<MetricEntry>,
    metricKeys: List<MetricKey>
) {
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        LocationText(location = location)
        SummaryText(summary = summary)
        MetricViewList(
            entries = metrics,
            keys = metricKeys
        )

    }
}

@Composable
private fun EditFAB(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.day_screen_edit_day))
    }
}

@Composable
private fun SummaryText(summary: String, modifier: Modifier = Modifier) {
    SectionHeader(
        icon = Icons.Filled.EditNote,
        displayText = stringResource(R.string.day_screen_summary_header)
    )
    Text(
        text = summary, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
private fun LocationText(location: String, modifier: Modifier = Modifier) {
    SectionHeader(
        icon = Icons.Filled.PinDrop,
        displayText = stringResource(R.string.day_screen_location_header)
    )
    Text(
        text = location, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}


@Composable
private fun MetricViewList(
    entries: List<MetricEntry>,
    keys: List<MetricKey>,
    modifier: Modifier = Modifier
) {
    val keyMap by remember(keys) {
        derivedStateOf {
            keys.associateBy({ it.id }, { it })
        }
    }

    Column(modifier = modifier) {
        SectionHeader(
            icon = Icons.Filled.List,
            displayText = stringResource(R.string.day_screen_metrics_header)
        )
        entries.map { entry ->
            val key = keyMap[entry.metricId]
            if (key != null) {
                MetricViewListItem(
                    entry = entry,
                    key = key
                )
            }
        }
    }
}

@Composable
private fun MetricViewListItem(entry: MetricEntry, key: MetricKey) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 24.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = key.name,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal
                )
            )
            Text(
                text = entry.value,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
        }
    }
}





