@file:OptIn(ExperimentalCoroutinesApi::class)
@file:Suppress("OPT_IN_USAGE")

package com.pronoidsoftware.nojoflow.presentation.editnote

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronoidsoftware.nojoflow.domain.timeAndEmitUntil
import com.pronoidsoftware.nojoflow.presentation.ui.format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class EditNoteViewModel @Inject constructor(

) : ViewModel() {
    private val requiredTime = 15.seconds
    val noteBody = TextFieldState()
    private val writingTimerRunning = MutableStateFlow(false)
    private val resetTimerRunning = MutableStateFlow(false)
    private val formattedWritingTime = timeAndEmitUntil(10f, requiredTime)
        .runningFold(requiredTime) { totalElapsedTime, newElapsedTime ->
            totalElapsedTime - newElapsedTime
        }
        .map { totalElapsedTime ->
            totalElapsedTime.format()
        }
        .onCompletion {
            emit("")
        }

    private val formattedResetTime = timeAndEmitUntil(10f, 5.seconds)
        .runningFold(5.seconds) { totalElapsedTime, newElapsedTime ->
            totalElapsedTime - newElapsedTime
        }
        .map { totalElapsedTime ->
            totalElapsedTime.format()
        }
        .onCompletion {
            emit(5.seconds.format())
            if (it == null) {
                writingTimerRunning.emit(false)
                noteBody.clearText()
            }
        }

    val remainingTime = writingTimerRunning
        .flatMapLatest { isRunning ->
            if (isRunning) formattedWritingTime else flowOf(requiredTime.format())
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            requiredTime.format()
        )

    val resetTime = resetTimerRunning
        .flatMapLatest { isRunning ->
            if (isRunning) formattedResetTime else flowOf(5.seconds.format())
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            5.seconds.format()
        )

    private val eventChannel = Channel<EditNoteEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        snapshotFlow { noteBody.text }
            .filter { it.isNotEmpty() }
            .onEach {
                writingTimerRunning.update { true }
                resetTimerRunning.update { false }
            }
            .debounce(2000L)
            .onEach { if (writingTimerRunning.value) resetTimerRunning.update { true } }
            .launchIn(viewModelScope)
    }

    fun onAction(action: EditNoteAction) {
        when (action) {
            else -> Unit
        }
    }
}