package com.pronoidsoftware.nojoflow.presentation.editnote

interface EditNoteEvent {
    data object WritingCompleted : EditNoteEvent
    data object Cancel : EditNoteEvent
}