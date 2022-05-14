package me.ionice.snapshot.ui.day

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import me.ionice.snapshot.database.DayWithMetrics
import me.ionice.snapshot.database.MetricEntry
import me.ionice.snapshot.ui.utils.Data
import java.time.LocalDate

// TODO: remove default DayWithMetrics, should always be passed in
class DayViewModel(dayWithMetrics: DayWithMetrics = Data.daysWithMetrics[0]): ViewModel() {

    private val _day by mutableStateOf( dayWithMetrics.day )
    private val _metrics = dayWithMetrics.metrics.toMutableStateList()

    val summary: String
        get() = _day.summary

    val date: LocalDate
        get() = LocalDate.ofEpochDay(_day.id)

    val location: String?
        get() = _day.location

    val metrics: List<MetricEntry>
        get() = _metrics

    fun setSummary(summary: String) {
        _day.summary = summary
    }

    fun setLocation(location: String) {
        _day.location = location
    }

    fun addMetric(metricEntry: MetricEntry) {
        _metrics.add(metricEntry)
    }

    fun removeMetric(metricEntry: MetricEntry) {
        _metrics.remove(metricEntry)
    }


}