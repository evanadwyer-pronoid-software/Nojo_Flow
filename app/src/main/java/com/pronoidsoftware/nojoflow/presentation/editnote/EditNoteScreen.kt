@file:OptIn(ExperimentalMaterial3Api::class)

package com.pronoidsoftware.nojoflow.presentation.editnote

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pronoidsoftware.nojoflow.R
import com.pronoidsoftware.nojoflow.presentation.ui.ObserveAsEvents
import com.pronoidsoftware.nojoflow.presentation.ui.theme.NojoFlowTheme

@Composable
fun EditNoteScreenRoot(
    onCancel: () -> Unit,
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

            EditNoteEvent.Cancel -> {
                onCancel()
            }
        }
    }

    val isWriting by viewModel.writingTimerRunning.collectAsStateWithLifecycle()
    val remainingTime by viewModel.remainingTime.collectAsStateWithLifecycle()
    val resetAlpha by viewModel.resetAlpha.collectAsStateWithLifecycle()
    val canSave by viewModel.canSave.collectAsStateWithLifecycle()
    val noteBody by viewModel.noteBody.collectAsStateWithLifecycle()

    BackHandler(enabled = isWriting && !canSave) { }

    EditNoteScreen(
        isWriting = isWriting,
        remainingTime = remainingTime,
        resetAlpha = resetAlpha,
        noteTitle = viewModel.title,
        isShowingTitleDialog = viewModel.isShowingTitleDialog,
        noteBody = noteBody,
        canSave = canSave,
        onAction = { action ->
            when (action) {
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
internal fun EditNoteScreen(
    isWriting: Boolean,
    remainingTime: String,
    resetAlpha: Float,
    noteTitle: String,
    isShowingTitleDialog: Boolean,
    noteBody: String,
    canSave: Boolean,
    onAction: (EditNoteAction) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (!isWriting || canSave) {
                        IconButton(
                            onClick = {
                                onAction(EditNoteAction.Cancel)
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.cancel)
                            )
                        }
                    }
                },
                title = {
                    Text(noteTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                actions = {
                    if (canSave) {
                        Text(stringResource(R.string.savable))
                    } else {
                        Text(remainingTime)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BasicTextField(
                value = noteBody,
                onValueChange = {
                    onAction(EditNoteAction.OnUserInput(it))
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                textStyle = TextStyle.Default.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .alpha(resetAlpha)
                    .weight(1f)
                    .fillMaxSize()
            )
        }

        if (isShowingTitleDialog) {
            BasicAlertDialog(
                onDismissRequest = {
                    onAction(EditNoteAction.Cancel)
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.LightGray),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val titleTextField = rememberTextFieldState()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.enter_title), color = Color.Black)
                    BasicTextField(
                        titleTextField,
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            capitalization = KeyboardCapitalization.Words
                        ),
                        onKeyboardAction = {
                            onAction(EditNoteAction.OnTitleEntered(titleTextField.text.toString()))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.DarkGray),
                        onClick = {
                            onAction(EditNoteAction.OnTitleEntered(titleTextField.text.toString()))
                        }
                    ) {
                        Text(stringResource(R.string.use_title), color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun EditNoteScreenPreview() {
    NojoFlowTheme {
        EditNoteScreen(
            isWriting = false,
            onAction = {},
            remainingTime = "5:00",
            resetAlpha = 1f,
            canSave = false,
            noteTitle = "New Note",
            isShowingTitleDialog = true,
            noteBody = ""
        )
    }
}