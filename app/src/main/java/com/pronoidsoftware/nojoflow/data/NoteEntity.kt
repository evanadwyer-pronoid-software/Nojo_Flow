package com.pronoidsoftware.nojoflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val body: String,
    val createdAt: Long,
    val lastUpdatedAt: Long,
)
