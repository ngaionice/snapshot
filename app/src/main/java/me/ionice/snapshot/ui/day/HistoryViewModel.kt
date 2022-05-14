package me.ionice.snapshot.ui.day

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import me.ionice.snapshot.database.DayWithMetrics
import me.ionice.snapshot.ui.utils.Data

class HistoryViewModel : ViewModel() {

    // TODO: update _days to fetch from Room
    private val _days = Data.daysWithMetrics.toMutableStateList()
    val days: List<DayWithMetrics>
        get() = _days
}