package me.ionice.snapshot.utils

import java.time.format.DateTimeFormatter

object Utils {
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
}