package com.pronoidsoftware.nojoflow.presentation.notelist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronoidsoftware.nojoflow.domain.LocalNoteDataSource
import com.pronoidsoftware.nojoflow.domain.PreferencesConstants
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
    private val dataStore: DataStore<Preferences>,
    private val localNoteDataSource: LocalNoteDataSource
) : ViewModel() {

    var state by mutableStateOf(NoteListState())
        private set

    private val eventChannel = Channel<NoteListEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        localNoteDataSource.getNotes()
            .onEach {
                state = state.copy(
                    notes = it,
                )
            }
            .launchIn(viewModelScope)

        dataStore.data
            .onEach {
                val requiredWritingTimeMin =
                    it[intPreferencesKey(PreferencesConstants.REQUIRED_WRITING_TIME)] ?: 5
                val isAutoDNDEnabled = it[booleanPreferencesKey(PreferencesConstants.AUTO_DND_ENABLED)] ?: false
                state = state.copy(
                    requiredWritingTime = requiredWritingTimeMin,
                    isAutoDNDEnabled = isAutoDNDEnabled
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: NoteListAction) {
        when (action) {

            is NoteListAction.SelectRequiredWritingTimeMin -> {
                viewModelScope.launch {
                    dataStore.edit {
                        it[intPreferencesKey(PreferencesConstants.REQUIRED_WRITING_TIME)] = action.newRequiredWritingTimeMin
                    }
                }
            }

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