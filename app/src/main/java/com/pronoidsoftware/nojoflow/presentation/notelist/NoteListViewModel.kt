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
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val localNoteDataSource: LocalNoteDataSource
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

            is NoteListAction.OpenRenameNote -> {
                state = state.copy(
                    noteBeingRenamed = action.note,
                    isShowingRenameNoteDialog = true,
                )
            }

            is NoteListAction.SubmitNewTitle -> {
                viewModelScope.launch {
                    state.noteBeingRenamed?.copy(
                        title = action.title,
                        lastUpdatedAt = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                    )?.let { localNoteDataSource.upsertNote(it) }
                    state = state.copy(
                        noteBeingRenamed = null,
                        isShowingRenameNoteDialog = false
                    )
                }
            }

            NoteListAction.DismissNewTitle -> {
                state = state.copy(
                    noteBeingRenamed = null,
                    isShowingRenameNoteDialog = false,
                )
            }

            is NoteListAction.DeleteNote -> {
                viewModelScope.launch {
                    localNoteDataSource.deleteNote(action.note)
                }
            }

            else -> Unit
        }
    }
}