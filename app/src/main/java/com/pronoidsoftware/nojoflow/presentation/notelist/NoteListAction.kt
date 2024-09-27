package com.pronoidsoftware.nojoflow.presentation.notelist

import java.util.UUID

sealed interface NoteListAction {
    data object Cancel : NoteListAction
    data object CreateNote : NoteListAction
    data class EditNote(val id: UUID) : NoteListAction
}