package com.pronoidsoftware.nojoflow.presentation.notelist

import com.pronoidsoftware.nojoflow.domain.Note

sealed interface NoteListAction {
    data class SelectRequiredWritingTimeMin(
        val newRequiredWritingTimeMin: Int
    ) : NoteListAction

    data class CreateNote(
        val requiredWritingTimeMin: Int
    ) : NoteListAction

    data class EditNote(
        val id: String,
    ) : NoteListAction

    data class OpenRenameNote(
        val note: Note,
    ) : NoteListAction

    data class SubmitNewTitle(
        val title: String
    ) : NoteListAction

    data object DismissNewTitle : NoteListAction

    data class DeleteNote(
        val note: Note,
    ) : NoteListAction

    data object ShowDNDDialog : NoteListAction
    data object HideDNDDialog : NoteListAction
}