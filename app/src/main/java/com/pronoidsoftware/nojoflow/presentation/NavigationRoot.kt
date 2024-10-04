package com.pronoidsoftware.nojoflow.presentation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
                    animationSpec = tween(1000),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(1000),
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
                    animationSpec = tween(1000),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(1000),
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