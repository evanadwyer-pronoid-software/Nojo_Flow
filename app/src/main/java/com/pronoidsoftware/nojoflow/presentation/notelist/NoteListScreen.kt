@file:OptIn(ExperimentalMaterial3Api::class)

package com.pronoidsoftware.nojoflow.presentation.notelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pronoidsoftware.nojoflow.R
import com.pronoidsoftware.nojoflow.presentation.ui.EnterTitleDialog
import com.pronoidsoftware.nojoflow.presentation.ui.ObserveAsEvents
import com.pronoidsoftware.nojoflow.presentation.ui.format
import com.pronoidsoftware.nojoflow.presentation.ui.theme.NojoFlowTheme
import kotlin.time.Duration.Companion.minutes

@Composable
fun NoteListScreenRoot(
    onNavigateToEditNote: (String?, Int?) -> Unit,
    viewModel: NoteListViewModel = hiltViewModel(),
) {
    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            else -> Unit
        }
    }

    NoteListScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                is NoteListAction.CreateNote -> onNavigateToEditNote(null, action.requiredWritingTimeMin)
                is NoteListAction.EditNote -> onNavigateToEditNote(action.id, null)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
internal fun NoteListScreen(
    state: NoteListState,
    onAction: (NoteListAction) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                actions = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clickable {
                                onAction(NoteListAction.CreateNote(state.requiredWritingTime))
                            }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.create_new)
                        )
                        Text("(${state.requiredWritingTime.minutes.format()})")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.isShowingRenameNoteDialog) {
            EnterTitleDialog(
                initialText = state.noteBeingRenamed?.title ?: "",
                onSubmit = { newTitle ->
                    onAction(NoteListAction.SubmitNewTitle(newTitle))
                },
                onDismiss = {
                    onAction(NoteListAction.DismissNewTitle)
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = state.notes,
                key = { it.id }
            ) { note ->
                NoteItem(
                    noteTitle = note.title,
                    onRenameClick = {
                        onAction(NoteListAction.OpenRenameNote(note))
                    },
                    onDeletedClick = {
                        onAction(NoteListAction.DeleteNote(note))
                    },
                    onEditNoteClick = {
                        onAction(NoteListAction.EditNote(note.id))
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun NoteListScreenPreview() {
    NojoFlowTheme {
        NoteListScreen(
            state = NoteListState(),
            onAction = {}
        )
    }
}