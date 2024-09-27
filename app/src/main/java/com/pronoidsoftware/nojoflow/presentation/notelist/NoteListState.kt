package com.pronoidsoftware.nojoflow.presentation.notelist

import com.pronoidsoftware.nojoflow.domain.Note

data class NoteListState(
    val notes: List<Note> = emptyList()
)
