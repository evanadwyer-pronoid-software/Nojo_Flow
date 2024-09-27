package com.pronoidsoftware.nojoflow.data

import com.pronoidsoftware.nojoflow.domain.LocalNoteDataSource
import com.pronoidsoftware.nojoflow.domain.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomLocalNoteDataSource @Inject constructor(
    private val noteDao: NoteDao
) : LocalNoteDataSource {
    override suspend fun upsertNote(note: Note) {
        noteDao.upsertNote(note.toNoteEntity())
    }

    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getNotes().map { noteEntities ->
            noteEntities.map { it.toNote() }
        }
    }

    override suspend fun getNoteById(id: String): Note? {
        return noteDao.getNoteById(id)?.toNote()
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toNoteEntity())
    }
}