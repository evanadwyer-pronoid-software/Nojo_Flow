package com.pronoidsoftware.nojoflow.presentation.notelist

sealed interface NoteListAction {
    data object CreateNote : NoteListAction
    data class EditNote(
        val id: String,
    ) : NoteListAction
}