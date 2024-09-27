package com.pronoidsoftware.nojoflow.presentation.notelist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronoidsoftware.nojoflow.domain.LocalNoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    localNoteDataSource: LocalNoteDataSource
) : ViewModel() {

    var state by mutableStateOf(NoteListState())
        private set

    private val eventChannel = Channel<NoteListEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        localNoteDataSource.getNotes()
            .onEach {
                state = NoteListState(it)
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: NoteListAction) {
        when (action) {

            else -> Unit
        }
    }
}