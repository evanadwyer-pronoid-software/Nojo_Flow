package com.pronoidsoftware.nojoflow.presentation.editnote

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pronoidsoftware.nojoflow.R
import com.pronoidsoftware.nojoflow.presentation.ui.ObserveAsEvents
import com.pronoidsoftware.nojoflow.presentation.ui.theme.NojoFlowTheme

@Composable
fun EditNoteScreenRoot(
    viewModel: EditNoteViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            EditNoteEvent.WritingCompleted -> {
                Toast.makeText(
                    context,
                    R.string.writing_completed,
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    val remainingTime by viewModel.remainingTime.collectAsStateWithLifecycle()
    val resetAlpha by viewModel.resetAlpha.collectAsStateWithLifecycle()
    EditNoteScreen(
        remainingTime = remainingTime,
        resetAlpha = resetAlpha,
        noteBody = viewModel.noteBody,
        canSave = viewModel.canSave,
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
    resetAlpha: Float,
    noteBody: TextFieldState,
    canSave: Boolean,
    onAction: (EditNoteAction) -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (canSave) {
                Text("Able to save")
            } else {
                Text("writing time: $remainingTime", color = Color.Black)
            }
            BasicTextField(
                state = noteBody,
                modifier = Modifier
                    .alpha(resetAlpha)
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
            resetAlpha = 1f,
            canSave = false,
            noteBody = TextFieldState()
        )
    }
}