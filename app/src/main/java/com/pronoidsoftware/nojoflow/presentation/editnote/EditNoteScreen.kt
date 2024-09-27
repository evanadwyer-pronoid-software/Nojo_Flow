package com.pronoidsoftware.nojoflow.presentation.editnote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pronoidsoftware.nojoflow.presentation.ui.ObserveAsEvents
import com.pronoidsoftware.nojoflow.presentation.ui.theme.NojoFlowTheme

@Composable
fun EditNoteScreenRoot(
    viewModel: EditNoteViewModel = hiltViewModel(),
) {
    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {

            }
        }

    val remainingTime by viewModel.remainingTime.collectAsStateWithLifecycle()
    val resetTime by viewModel.resetTime.collectAsStateWithLifecycle()
    EditNoteScreen(
        remainingTime = remainingTime,
        resetTime = resetTime,
        noteBody = viewModel.noteBody,
        onAction = { action ->
            when (action) {

                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
internal fun EditNoteScreen(
    remainingTime: String,
    resetTime: String,
    noteBody: TextFieldState,
    onAction: (EditNoteAction) -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("writing time: $remainingTime", color = Color.Black)
            Text("reset time: $resetTime", color = Color.Black)
            Row {
                TextButton(
                    onClick = {
                        onAction(EditNoteAction.StartCountdown)
                    }
                ) {
                    Text("Start Countdown")
                }
                TextButton(
                    onClick = {
                        onAction(EditNoteAction.StopCountdown)
                    }
                ) {
                    Text("Stop Countdown")
                }
            }
            BasicTextField(
                state = noteBody,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
private fun EditNoteScreenPreview() {
    NojoFlowTheme {
        EditNoteScreen(
            onAction = {},
            remainingTime = "5:00",
            resetTime = "0:05",
            noteBody = TextFieldState()
        )
    }
}