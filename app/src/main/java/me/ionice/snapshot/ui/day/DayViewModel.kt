package me.ionice.snapshot.ui.day

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import me.ionice.snapshot.database.DayWithMetrics
import me.ionice.snapshot.ui.utils.Data
import java.time.LocalDate

// TODO: remove default DayWithMetrics, should always be passed in
class DayViewModel(dayWithMetrics: DayWithMetrics = Data.daysWithMetrics[0]) : ViewModel() {

    private var _day = dayWithMetrics.day

    var summary by mutableStateOf( _day.summary )
    var date: LocalDate by mutableStateOf( LocalDate.ofEpochDay(_day.id) )
    var location by mutableStateOf(_day.location)
    var metrics = dayWithMetrics.metrics.toMutableStateList()




}