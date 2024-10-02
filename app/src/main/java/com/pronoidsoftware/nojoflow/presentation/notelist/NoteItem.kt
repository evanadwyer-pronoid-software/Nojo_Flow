package com.pronoidsoftware.nojoflow.presentation.notelist

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pronoidsoftware.nojoflow.R

@Composable
fun NoteItem(
    noteTitle: String,
    modifier: Modifier = Modifier,
    onRenameClick: () -> Unit = {},
    onDeletedClick: () -> Unit = {},
    onEditNoteClick: () -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val config = LocalConfiguration.current
    config.screenWidthDp

    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .indication(interactionSource, LocalIndication.current)
                .pointerInput(true) {
                    detectTapGestures(
                        onLongPress = {
                            expanded = true
                        },
                        onTap = {
                            onEditNoteClick.invoke()
                        },
                        onPress = {
                            val press = PressInteraction.Press(it)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press))
                        }
                    )
                }
                .padding(16.dp)
        ) {
            Text(noteTitle)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.rename))
                },
                onClick = {
                    onRenameClick()
                    expanded = false
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.delete))
                },
                onClick = {
                    onDeletedClick()
                    expanded = false
                }
            )
        }
    }
}