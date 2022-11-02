package dev.ionice.snapshot.core.model

import java.time.Instant
import java.time.LocalDate

data class Day(
    val id: Long,
    val summary: String,
    private val createdAt: Long,         // epoch second
    private val lastModifiedAt: Long,    // epoch second
    val isFavorite: Boolean = false,
    val location: LocationEntry? = null,
    val tags: List<TagEntry>
) {
    fun getDate(): LocalDate = LocalDate.ofEpochDay(id)

    fun getCreatedAt(): Instant = Instant.ofEpochSecond(createdAt)

    fun getLastModifiedAt(): Instant = Instant.ofEpochSecond(lastModifiedAt)
}