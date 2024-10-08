package com.pronoidsoftware.nojoflow.presentation.editnote

sealed interface EditNoteAction {
    data object Cancel: EditNoteAction
    data class OnUserInput(
        val newBody: String,
    ): EditNoteAction
    data class OnTitleEntered(
        val title: String,
    ): EditNoteAction
}