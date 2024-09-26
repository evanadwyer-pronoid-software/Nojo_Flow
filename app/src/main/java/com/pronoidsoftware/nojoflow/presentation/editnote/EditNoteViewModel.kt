package com.pronoidsoftware.nojoflow.presentation.editnote

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(

) : ViewModel() {

//    var state by mutableStateOf(EditNoteState())
//        private set

    private val eventChannel = Channel<EditNoteEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: EditNoteAction) {
        when (action) {

            else -> Unit
        }
    }
}