package com.pronoidsoftware.nojoflow.presentation.editnote

sealed interface EditNoteAction {
    data object Cancel: EditNoteAction
}