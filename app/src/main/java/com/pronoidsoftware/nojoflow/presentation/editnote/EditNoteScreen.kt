@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pronoidsoftware.nojoflow.presentation.editnote

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pronoidsoftware.nojoflow.R
import com.pronoidsoftware.nojoflow.presentation.ui.EnterTitleDialog
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
    val focusRequester = remember {
        FocusRequester()
    }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .systemBarsPadding()
            .padding(top = 16.dp),
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Top),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction(EditNoteAction.Cancel)
                        },
                        enabled = !isWriting || canSave,
                        modifier = Modifier.alpha(if (!isWriting || canSave) 1f else 0f)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.cancel)
                        )
                    }
                },
                title = {
                    Text(noteTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                actions = {
                    if (canSave) {
                        Icon(
                            painterResource(R.drawable.time_completed),
                            contentDescription = stringResource(R.string.time_completed)
                        )
                    } else {
                        Text(remainingTime)
                    }
                }
            )
        }
    ) { innerPadding ->
        val isKeyboardOpen by keyboardAsState()

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
                .imePadding()
                .alpha(resetAlpha)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp, bottom = if (isKeyboardOpen) 32.dp else 0.dp)
                .focusRequester(focusRequester)
        )

        if (isShowingTitleDialog) {
            EnterTitleDialog(
                onSubmit = { newTitle ->
                    onAction(EditNoteAction.OnTitleEntered(newTitle))
                    focusRequester.requestFocus()
                },
                onDismiss = {
                    onAction(EditNoteAction.Cancel)
                }
            )
        }
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.isImeVisible
    return rememberUpdatedState(isImeVisible)
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