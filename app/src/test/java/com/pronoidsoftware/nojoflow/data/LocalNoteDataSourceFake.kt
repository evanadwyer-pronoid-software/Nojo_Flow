package com.pronoidsoftware.nojoflow.data

import androidx.compose.runtime.snapshotFlow
import com.pronoidsoftware.nojoflow.domain.LocalNoteDataSource
import com.pronoidsoftware.nojoflow.domain.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalNoteDataSourceFake : LocalNoteDataSource {
    private val notes = mutableMapOf<String, Note>()
    private val notesFlow = snapshotFlow { notes }

    override suspend fun upsertNote(note: Note) {
        notes[note.id] = note
    }

    override fun getNotes(): Flow<List<Note>> {
        return notesFlow.map { it.values.toList() }
    }

    override suspend fun getNoteById(id: String): Note? {
        return notes.getOrDefault(id, null)
    }

    override suspend fun deleteNote(note: Note) {
        notes.remove(note.id)
    }
}