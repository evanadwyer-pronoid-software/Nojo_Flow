@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pronoidsoftware.nojoflow.presentation.editnote

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.pronoidsoftware.nojoflow.MainCoroutineExtension
import com.pronoidsoftware.nojoflow.data.LocalNoteDataSourceFake
import com.pronoidsoftware.nojoflow.domain.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class EditNoteViewModelTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension(UnconfinedTestDispatcher())
    }

    private lateinit var viewModel: EditNoteViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var localNoteDataSourceFake: LocalNoteDataSourceFake

    @BeforeEach
    fun `set up`() {
        savedStateHandle = SavedStateHandle()
        localNoteDataSourceFake = LocalNoteDataSourceFake()
        viewModel = EditNoteViewModel(
            savedStateHandle = savedStateHandle,
            localNoteDataSource = localNoteDataSourceFake
        )
    }

    @Test
    fun `load & edit note`() = runTest(mainCoroutineExtension.testDispatcher) {
        savedStateHandle["id"] = "testNote"
        localNoteDataSourceFake.upsertNote(
            Note(
                id = "testNote",
                title = "testTitle",
                body = "testBody",
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                lastUpdatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
        )
        viewModel = EditNoteViewModel(
            savedStateHandle = savedStateHandle,
            localNoteDataSource = localNoteDataSourceFake
        )
        assertThat(viewModel.noteBody.value).isEqualTo("testBody")
        assertThat(viewModel.canSave.value).isTrue()
        assertThat(viewModel.writingTimerRunning.value).isTrue()
        assertThat(viewModel.remainingTime.value).isEqualTo("5:00")
        viewModel.onAction(EditNoteAction.OnUserInput("testBody edited"))
        advanceUntilIdle()
        assertThat(localNoteDataSourceFake.getNoteById("testNote")?.body).isEqualTo("testBody edited")
    }

    // TODO: fix with correct time advancement
    @Test
    fun `writing disappears`() = runTest(mainCoroutineExtension.testDispatcher) {
        viewModel.noteBody.test {
            val emptyStart = awaitItem()
            assertThat(emptyStart).isEmpty()
            viewModel.onAction(EditNoteAction.OnUserInput("test"))
            val initialText = awaitItem()
            assertThat(initialText).isEqualTo("test")
            advanceUntilIdle()
            val finalText = awaitItem()
            assertThat(finalText).isEmpty()
        }
    }

    // TODO: fix with time reset
    @Test
    fun `formatted writing time resets`() = runTest(mainCoroutineExtension.testDispatcher) {
        viewModel.remainingTime.test {
            val initialTime = awaitItem()
            assertThat(initialTime).isEqualTo("5:00")
            viewModel.onAction(EditNoteAction.OnUserInput("test"))
            val nextEmission = awaitItem()
            assertThat(nextEmission).isEqualTo("4:59")
            skipItems(5)
            val timeReset = awaitItem()
            assertThat(timeReset).isEqualTo("5:00")
        }
    }
}