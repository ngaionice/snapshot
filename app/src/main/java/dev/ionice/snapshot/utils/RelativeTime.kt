package dev.ionice.snapshot.utils

import java.time.Instant
import java.time.LocalDate
import java.util.concurrent.TimeUnit

// Adapted from https://stackoverflow.com/a/23215152
object RelativeTime {
    val times = listOf(
        TimeUnit.DAYS.toMillis(365),
        TimeUnit.DAYS.toMillis(30),
        TimeUnit.DAYS.toMillis(1),
        TimeUnit.HOURS.toMillis(1),
        TimeUnit.MINUTES.toMillis(1),
        TimeUnit.SECONDS.toMillis(1))

    private val timesString =
        listOf("year", "month", "day", "hour", "minute", "second")

    fun getPastDuration(date: LocalDate): String {
        val duration = (Instant.now().epochSecond - date.toEpochDay() * 24 * 60 * 60) * 1000
        val res = StringBuffer()
        for (i in times.indices) {
            val current: Long = times[i]
            val temp = duration / current
            if (temp > 0) {
                res.append(temp).append(" ").append(timesString[i])
                    .append(if (temp != 1L) "s" else "").append(" ago")
                break
            }
        }
        return if ("" == res.toString()) "0 seconds ago" else res.toString()
    }

    fun getPastDuration(epochSecond: Long): String {
        val duration = (Instant.now().epochSecond - epochSecond) * 1000
        val res = StringBuffer()
        for (i in times.indices) {
            val current: Long = times[i]
            val temp = duration / current
            if (temp > 0) {
                res.append(temp).append(" ").append(timesString[i])
                    .append(if (temp != 1L) "s" else "").append(" ago")
                break
            }
        }
        return if ("" == res.toString()) "0 seconds ago" else res.toString()
    }
}