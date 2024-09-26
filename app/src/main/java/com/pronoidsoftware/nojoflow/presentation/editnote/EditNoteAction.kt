package com.pronoidsoftware.nojoflow.presentation.editnote

sealed interface EditNoteAction {
    data object StartCountdown: EditNoteAction
    data object StopCountdown: EditNoteAction
}