package me.ionice.snapshot.utils

import me.ionice.snapshot.data.day.Day
import me.ionice.snapshot.data.day.DayWithMetrics
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey
import java.time.LocalDate

object FakeData {
    private val summaries = listOf(
        "",
        "Didn't do much today",
        "Went on a super long trip today. Ate a lot of food. Then did a 5000m row plus 2000m of swimming. Very exhausted after this, would do again.",
        "Worked on snapshot some more today. Honestly I like Compose a lot, but there's still a lot for me to learn still."
    )

    private val locations = listOf(
        "Toronto, ON, Canada",
        "Vancouver, BC, Canada",
        "Norway",
        "Hong Kong",
        ""
    )

    private val metricValues = listOf("2:15", "190lb", "2000m")

    private val startEpoch = LocalDate.now().toEpochDay()
    private val endEpoch = startEpoch + 15

    val days = (startEpoch..endEpoch).map { d ->
        Day(
            id = d,
            summary = summaries[summaries.indices.random()],
            location = locations[locations.indices.random()]
        )
    }

    val metricKeys = listOf(
        MetricKey(id = 1, name = "Rowing"),
        MetricKey(id = 2, name = "Swimming"),
        MetricKey(id = 3, name = "Squats 5x5")
    )

    val metricEntries = (0..10).map {
        MetricEntry(
            (1..metricKeys.size).random().toLong(),
            (startEpoch..endEpoch).random(),
            metricValues[metricValues.indices.random()]
        )
    }

    val longSummaryEntry = DayWithMetrics(Day(summary = summaries[2]), emptyList())

    val varyingDateEntries = listOf(
        DayWithMetrics(Day(id = LocalDate.now().minusMonths(1).toEpochDay(),summary = summaries[2], location = locations[1]), emptyList()),
        DayWithMetrics(Day(id = LocalDate.now().minusMonths(3).toEpochDay(), summary = summaries[2], location = locations[1]), emptyList()),
        DayWithMetrics(Day(id = LocalDate.now().minusMonths(6).toEpochDay(), summary = summaries[2], location = locations[1]), emptyList()),
        DayWithMetrics(Day(id = LocalDate.now().minusMonths(12).toEpochDay(), summary = summaries[2], location = locations[1]), emptyList()),
        DayWithMetrics(Day(id = LocalDate.now().minusMonths(24).toEpochDay(), summary = summaries[2], location = locations[1]), emptyList()),
        DayWithMetrics(Day(id = LocalDate.now().minusMonths(36).toEpochDay(), summary = summaries[2], location = locations[1]), emptyList())
    )

    val daysWithMetrics = days.map { d ->
        DayWithMetrics(
            d,
            metricEntries.subList(0, (0..metricEntries.size).random()) as MutableList<MetricEntry>
        )
    }
}
