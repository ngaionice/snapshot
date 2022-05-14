package me.ionice.snapshot.ui.dummydata

import me.ionice.snapshot.database.Day
import me.ionice.snapshot.database.DayWithMetrics
import me.ionice.snapshot.database.MetricEntry
import me.ionice.snapshot.database.MetricKey

object Data {
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

    private const val startEpoch = 19125
    private const val endEpoch = startEpoch + 15

    val days = (startEpoch..endEpoch).map { d ->
        Day(
            id = d.toLong(),
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
            (startEpoch..endEpoch).random().toLong(),
            metricValues[metricValues.indices.random()]
        )
    }

    val daysWithMetrics = days.map { d ->
        DayWithMetrics(
            d,
            metricEntries.subList(0, (0..metricEntries.size).random())
        )
    }
}
