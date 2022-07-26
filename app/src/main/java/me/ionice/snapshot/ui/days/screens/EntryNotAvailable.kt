package me.ionice.snapshot.ui.days.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.ionice.snapshot.R
import me.ionice.snapshot.ui.common.components.AddFAB
import me.ionice.snapshot.ui.common.components.BackButton
import me.ionice.snapshot.ui.common.screens.BaseScreen
import me.ionice.snapshot.ui.days.DayEntryUiState
import me.ionice.snapshot.utils.Utils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryNotAvailableScreen(
    uiState: DayEntryUiState.EntryNotFound,
    onDayAdd: (Long) -> Unit,
    onBack: () -> Unit
) {
    BaseScreen(
        headerText = Utils.dateFormatter.format(LocalDate.ofEpochDay(uiState.dayId)),
        navigationIcon = { BackButton(onBack) }, floatingActionButton = {
            AddFAB(
                onClick = { onDayAdd(uiState.dayId) },
                description = stringResource(R.string.day_screen_add_day)
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.day_screen_not_found),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}