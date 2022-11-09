package dev.ionice.snapshot.core.model

import java.time.Instant
import java.time.LocalDate

data class Day(
    val id: Long,
    val summary: String,
    val isFavorite: Boolean,
    val location: Location?,
    val tags: List<ContentTag>,
    private val createdAt: Long,         // epoch second
    private val lastModifiedAt: Long,    // epoch second
) {
    fun date(): LocalDate = LocalDate.ofEpochDay(id)

    fun createdAt(): Instant = Instant.ofEpochSecond(createdAt)

    fun lastModifiedAt(): Instant = Instant.ofEpochSecond(lastModifiedAt)
}