package com.pronoidsoftware.nojoflow.presentation.notelist

import com.pronoidsoftware.nojoflow.domain.Note

data class NoteListState(
    val notes: List<Note> = emptyList(),
    val requiredWritingTime: Int = 0,
    val isShowingRenameNoteDialog: Boolean = false,
    val noteBeingRenamed: Note? = null,
    val isAutoDNDEnabled: Boolean = false,
)
