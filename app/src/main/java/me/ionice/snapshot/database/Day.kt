package me.ionice.snapshot.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "day_entry")
class Day {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    val date: Date = Calendar.getInstance().time
    var summary: String = ""
    var location: String? = null
}