package me.likenotesapp

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes")
data class Note(
    var text: String,
    val createdMs: Long,
    var updatedMs: Long,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)