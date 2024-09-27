package com.pronoidsoftware.nojoflow.presentation.notelist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(

) : ViewModel() {

    var state by mutableStateOf(NoteListState())
        private set

    private val eventChannel = Channel<NoteListEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: NoteListAction) {
        when (action) {

            else -> Unit
        }
    }
}