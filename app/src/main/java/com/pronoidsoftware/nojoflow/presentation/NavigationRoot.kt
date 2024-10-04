package com.pronoidsoftware.nojoflow.presentation

import androidx.compose.animation.AnimatedContentTransitionScope
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
        startDestination = NoteList,
    ) {
        composable<NoteList>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            }
        ) {
            NoteListScreenRoot(
                onNavigateToEditNote = { id, requiredWritingTimeMin ->
                    navHostController.navigate(
                        EditNote(
                            id = id,
                            requiredWritingTimeMin = requiredWritingTimeMin
                        )
                    )
                }
            )
        }
        composable<EditNote>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
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
    val id: String?,
    val requiredWritingTimeMin: Int?,
)