@file:OptIn(ExperimentalMaterial3Api::class)

package com.pronoidsoftware.nojoflow.presentation.notelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.pronoidsoftware.nojoflow.presentation.ui.ObserveAsEvents
import com.pronoidsoftware.nojoflow.presentation.ui.theme.NojoFlowTheme

@Composable
fun NoteListScreenRoot(
    onNavigateToEditNote: (String?) -> Unit,
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
                NoteListAction.CreateNote -> onNavigateToEditNote(null)
                is NoteListAction.EditNote -> onNavigateToEditNote(action.id)
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
                    IconButton(
                        onClick = {
                            onAction(NoteListAction.CreateNote)
                        }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.create_new)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
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
            ) {
                Text(
                    text = it.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAction(NoteListAction.EditNote(it.id))
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