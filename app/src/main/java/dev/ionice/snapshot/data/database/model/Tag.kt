package dev.ionice.snapshot.data.database.model

import androidx.room.*

@Entity(tableName = "Tag")
data class TagProperties(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val lastUsedAt: Long
)

@Entity(
    primaryKeys = ["dayId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = DayProperties::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagProperties::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dayId"), Index("tagId")]
)
data class TagEntry(
    val dayId: Long,
    val tagId: Long,
    val content: String? = null
)

@Fts4(contentEntity = TagEntry::class)
@Entity
data class TagEntryFts(
    val dayId: Long,
    val tagId: Long,
    val content: String?
)

data class Tag(
    @Embedded
    val properties: TagProperties,
    @Relation(parentColumn = "id", entityColumn = "tagId")
    val entries: List<TagEntry>
)
