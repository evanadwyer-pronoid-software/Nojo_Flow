package com.pronoidsoftware.nojoflow.data

import com.pronoidsoftware.nojoflow.domain.Note
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun Note.toNoteEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        body = body,
        createdAt = createdAt
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds(),
        lastUpdatedAt = lastUpdatedAt
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    )
}

fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        body = body,
        createdAt = Instant.fromEpochMilliseconds(createdAt)
            .toLocalDateTime(TimeZone.currentSystemDefault()),
        lastUpdatedAt = Instant.fromEpochMilliseconds(lastUpdatedAt)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    )
}