package me.ionice.snapshot.data.database

import me.ionice.snapshot.data.database.model.Coordinates
import java.time.LocalDate

object TestingData {

    object Day {
        const val year = 1989
        const val month = 12
        const val dayOfMonth = 13
        val date = LocalDate.of(year, month, dayOfMonth).toEpochDay()
        const val daySummary = "Test summary!"
    }

    object Location {
        const val initialId = 0L
        private const val lat = -82.8628
        private const val lon = 135.0000
        val coordinates = Coordinates(lat, lon)
        const val name = "TestLocation"
        const val lastUsedAt = 0L
    }

    object Tag {
        const val initialId = 0L
        const val name = "TestTag"
        const val lastUsedAt = 0L
        const val content = "TestTagContent"
    }

}