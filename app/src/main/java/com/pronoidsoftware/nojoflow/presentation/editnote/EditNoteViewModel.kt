@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pronoidsoftware.nojoflow.presentation.editnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronoidsoftware.nojoflow.domain.timeAndEmitUntil
import com.pronoidsoftware.nojoflow.presentation.ui.format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class EditNoteViewModel @Inject constructor(

) : ViewModel() {
    private val requiredTime = 10.seconds
    private val timerTrigger = MutableSharedFlow<Boolean>()
    private val formattedTime = timeAndEmitUntil(10f, requiredTime)
        .runningFold(requiredTime) { totalElapsedTime, newElapsedTime ->
            totalElapsedTime - newElapsedTime
        }
        .map { totalElapsedTime ->
            totalElapsedTime.format()
        }
        .onCompletion {
            emit("")
        }
    val remainingTime = timerTrigger
        .flatMapLatest { shouldStart ->
            if (shouldStart) formattedTime else flowOf(requiredTime.format())
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            requiredTime.format()
        )

//    var state by mutableStateOf(EditNoteState())
//        private set

    private val eventChannel = Channel<EditNoteEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: EditNoteAction) {
        when (action) {
            EditNoteAction.StartCountdown -> {
                viewModelScope.launch {
                    timerTrigger.emit(true)
                }
            }

            EditNoteAction.StopCountdown -> {
                viewModelScope.launch {
                    timerTrigger.emit(false)
                }
            }
        }
    }
}