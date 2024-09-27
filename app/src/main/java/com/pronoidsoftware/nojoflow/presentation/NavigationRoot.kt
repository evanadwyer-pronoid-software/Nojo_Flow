package com.pronoidsoftware.nojoflow.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pronoidsoftware.nojoflow.presentation.editnote.EditNoteScreenRoot
import com.pronoidsoftware.nojoflow.presentation.notelist.NoteListScreenRoot
import kotlinx.serialization.Serializable

@Composable
fun NavigationRoot(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = NoteList
    ) {
        composable<NoteList> {
            NoteListScreenRoot(
                onNavigateToEditNote = { id ->
                    navHostController.navigate(
                        EditNote(
                            id = id
                        )
                    )
                }
            )
        }
        composable<EditNote> {
            EditNoteScreenRoot(
                onCancel = {
                    navHostController.navigateUp()
                }
            )
        }
    }
}

@Serializable
object NoteList

@Serializable
data class EditNote(
    val id: String?
)