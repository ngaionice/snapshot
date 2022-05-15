package me.ionice.snapshot.ui.day

import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import me.ionice.snapshot.database.DayWithMetrics
import me.ionice.snapshot.ui.utils.Data
import me.ionice.snapshot.ui.utils.SavableState

// TODO: remove default DayWithMetrics, should always be passed in
class DayViewModel(dayWithMetrics: DayWithMetrics = Data.daysWithMetrics[0], stateHandle: SavedStateHandle) : ViewModel() {

    private var _day = dayWithMetrics.day

    var summary by SavableState(stateHandle, "summary", _day.summary)
    var date by SavableState(stateHandle, "date", _day.id)
    var location by SavableState(stateHandle, "location", _day.location)
    var metrics = dayWithMetrics.metrics.toMutableStateList()



}