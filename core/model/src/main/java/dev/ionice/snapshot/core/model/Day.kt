package dev.ionice.snapshot.core.model

import java.time.Instant
import java.time.LocalDate

data class Day(
    val id: Long,
    val summary: String,
    private val createdAt: Long,         // epoch second
    private val lastModifiedAt: Long,    // epoch second
    val isFavorite: Boolean,
    val location: Location?,
    val tags: List<ContentTag>
) {
    fun date(): LocalDate = LocalDate.ofEpochDay(id)

    fun createdAt(): Instant = Instant.ofEpochSecond(createdAt)

    fun lastModifiedAt(): Instant = Instant.ofEpochSecond(lastModifiedAt)
}