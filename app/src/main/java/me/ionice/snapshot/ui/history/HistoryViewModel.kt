package me.ionice.snapshot.ui.history

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.ui.utils.FakeData

class HistoryViewModel : ViewModel() {

    // TODO: update _days to fetch from Room
    private val _days = FakeData.daysWithMetrics.toMutableStateList()
    val days: List<DayWithMetrics>
        get() = _days
}