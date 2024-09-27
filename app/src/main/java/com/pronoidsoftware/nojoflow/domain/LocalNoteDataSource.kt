package com.pronoidsoftware.nojoflow.domain

import kotlinx.coroutines.flow.Flow

interface LocalNoteDataSource {
    suspend fun upsertNote(note: Note)
    fun getNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: String): Note?
    suspend fun deleteNote(note: Note)
}