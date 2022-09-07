package me.ionice.snapshot.utils

import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

object Utils {
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val shortDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd")
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val fullDateMonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd")

    val locale: Locale = Locale.US

    val firstDayOfWeek: DayOfWeek = WeekFields.of(locale).firstDayOfWeek
    val lastDayOfWeek: DayOfWeek = DayOfWeek.of(((firstDayOfWeek.value + 5) % DayOfWeek.values().size) + 1)
}