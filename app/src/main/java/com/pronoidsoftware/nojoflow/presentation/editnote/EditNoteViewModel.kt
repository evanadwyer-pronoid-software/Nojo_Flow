@file:OptIn(ExperimentalCoroutinesApi::class)
@file:Suppress("OPT_IN_USAGE")

package com.pronoidsoftware.nojoflow.presentation.editnote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronoidsoftware.nojoflow.domain.LocalNoteDataSource
import com.pronoidsoftware.nojoflow.domain.Note
import com.pronoidsoftware.nojoflow.domain.timeAndEmitUntil
import com.pronoidsoftware.nojoflow.presentation.ui.format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val localNoteDataSource: LocalNoteDataSource,
) : ViewModel() {
    private val _noteBody = MutableStateFlow("")
    val noteBody = _noteBody
        .onStart {
            _noteBody.update {
                savedStateHandle.getId()
                    ?.let { existingNoteId -> localNoteDataSource.getNoteById(existingNoteId)?.body }
                    ?: ""
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ""
        )
    var title by mutableStateOf("")
        private set
    var isShowingTitleDialog by mutableStateOf(false)
        private set
    private val noteId = savedStateHandle.getId() ?: UUID.randomUUID().toString()
    private var createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    private var editMade by mutableStateOf(false)
    private val _canSave = MutableStateFlow(savedStateHandle.getId() != null)
    val canSave = _canSave.asStateFlow()
    private val requiredTime = 15.seconds
    private val resetTime = 5.seconds
    private val _writingTimerRunning = MutableStateFlow(false)
    val writingTimerRunning = _writingTimerRunning.asStateFlow()
    private val resetTimerRunning = MutableStateFlow(false)

    private val eventChannel = Channel<EditNoteEvent>()
    val events = eventChannel.receiveAsFlow()

    private fun SavedStateHandle.getId(): String? {
        return get<String>("id")
    }

    private val formattedWritingTime = timeAndEmitUntil(10f, requiredTime)
        .runningFold(requiredTime) { totalElapsedTime, newElapsedTime ->
            totalElapsedTime - newElapsedTime
        }
        .map { totalElapsedTime ->
            totalElapsedTime.format()
        }
        .onCompletion {
            if (it == null) {
                resetTimerRunning.update { false }
                _canSave.update { true }
                eventChannel.send(EditNoteEvent.WritingCompleted)
                localNoteDataSource.upsertNote(
                    Note(
                        id = noteId,
                        title = title,
                        body = noteBody.toString(),
                        createdAt = createdAt,
                        lastUpdatedAt = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                    )
                )
            }
        }

    val remainingTime = _writingTimerRunning
        .flatMapLatest { isRunning ->
            if (_canSave.value) {
                emptyFlow()
            } else {
                if (isRunning) formattedWritingTime else flowOf(requiredTime.format())
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            requiredTime.format()
        )

    private val formattedResetAlpha = timeAndEmitUntil(10f, resetTime)
        .runningFold(resetTime) { totalElapsedTime, newElapsedTime ->
            totalElapsedTime - newElapsedTime
        }
        .map { totalElapsedTime ->
            (totalElapsedTime / resetTime).toFloat().coerceIn(0f, 1f)
        }
        .onCompletion {
            if (it == null) {
                emit(1f)
                _writingTimerRunning.update { false }
                _noteBody.update { "" }
            }
        }

    val resetAlpha = resetTimerRunning
        .flatMapLatest { isRunning ->
            if (_canSave.value) {
                emptyFlow()
            } else {
                if (isRunning) formattedResetAlpha else flowOf(1f)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            1f
        )

    init {
        viewModelScope.launch {
            val existingNote = localNoteDataSource.getNoteById(noteId)
            createdAt = existingNote?.createdAt ?: Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
            title = existingNote?.title ?: ""
            if (existingNote == null) {
                isShowingTitleDialog = true
            }
        }

        noteBody
            .filter { it.isNotEmpty() }
            .onEach {
                _writingTimerRunning.update { true }
                resetTimerRunning.update { false }
            }
            .debounce(1000L)
            .onEach {
                if (_writingTimerRunning.value) resetTimerRunning.update { true }
                if (canSave.value && editMade) {
                    localNoteDataSource.upsertNote(
                        Note(
                            id = noteId,
                            title = title,
                            body = noteBody.value,
                            createdAt = createdAt,
                            lastUpdatedAt = Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: EditNoteAction) {
        when (action) {
            is EditNoteAction.OnUserInput -> {
                viewModelScope.launch {
                    _noteBody.update { action.newBody }
                    editMade = true
                }
            }

            is EditNoteAction.OnTitleEntered -> {
                title = action.title
                isShowingTitleDialog = false
            }

            EditNoteAction.Cancel -> {
                viewModelScope.launch {
                    isShowingTitleDialog = false
                    eventChannel.send(EditNoteEvent.Cancel)
                }
            }

            else -> Unit
        }
    }
}