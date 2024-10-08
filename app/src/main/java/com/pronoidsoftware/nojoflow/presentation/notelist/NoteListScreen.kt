@file:OptIn(ExperimentalMaterial3Api::class)

package com.pronoidsoftware.nojoflow.presentation.notelist

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.pronoidsoftware.nojoflow.R
import com.pronoidsoftware.nojoflow.domain.PreferencesConstants
import com.pronoidsoftware.nojoflow.presentation.notelist.components.DNDDialog
import com.pronoidsoftware.nojoflow.presentation.ui.EnterTitleDialog
import com.pronoidsoftware.nojoflow.presentation.ui.ObserveAsEvents
import com.pronoidsoftware.nojoflow.presentation.ui.format
import com.pronoidsoftware.nojoflow.presentation.ui.theme.NojoFlowTheme
import kotlinx.coroutines.launch
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
                is NoteListAction.CreateNote -> onNavigateToEditNote(
                    null,
                    action.requiredWritingTimeMin
                )

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
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerState = drawerState,
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.time_selection),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        PreferencesConstants.TIME_SETTING_OPTIONS.forEach { timeSetting ->
                            NavigationDrawerItem(
                                label = {
                                    Text(timeSetting.minutes.format())
                                },
                                selected = timeSetting == state.requiredWritingTime,
                                onClick = {
                                    onAction(NoteListAction.SelectRequiredWritingTimeMin(timeSetting))
                                    scope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        painterResource(R.drawable.time_completed),
                                        contentDescription = stringResource(
                                            R.string.selected_time,
                                            timeSetting
                                        ),
                                        modifier = Modifier.alpha(if (timeSetting == state.requiredWritingTime) 1f else 0f)
                                    )
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 60.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Switch(
                                checked = state.isAutoDNDEnabled,
                                onCheckedChange = {
                                    onAction(NoteListAction.ShowDNDDialog)
                                }
                            )
                            Text(stringResource(R.string.dnd_mode))
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painterResource(R.drawable.pronoid_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(16.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    ) {
        SideEffect {
            val window = (context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.statusBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
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
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    },
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
                            Text(
                                text = "(${state.requiredWritingTime.minutes.format()})",
                                modifier = Modifier.alpha(if (state.requiredWritingTime > 0) 1f else 0f)
                            )
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

            if (state.isShowingDNDDialog) {
                DNDDialog(
                    explanation = stringResource(
                        R.string.auto_dnd_dialog_body,
                        if (state.isAutoDNDEnabled) stringResource(R.string.disable)
                        else stringResource(R.string.enable),
                        stringResource(R.string.app_name)
                    ),
                    onClick = {
                        val intent =
                            Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        context.startActivity(intent)
                        onAction(NoteListAction.HideDNDDialog)
                    },
                    onDismiss = {
                        onAction(NoteListAction.HideDNDDialog)
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
                        modifier = Modifier.animateItem(),
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