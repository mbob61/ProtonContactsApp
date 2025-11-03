package com.example.todoapp.screens.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.example.todoapp.annotations.SmokeTest
import com.example.todoapp.base.BaseTest
import com.example.todoapp.utils.assertTextDisplayed
import com.example.todoapp.utils.waitForText
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test

@HiltAndroidTest
class ListScreenSmokeTests : BaseTest() {

    @SmokeTest
    @Test
    fun createNewNoteViaFAB() {
        composeTestRule.onNodeWithContentDescription("Create new note")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Note Title")
            .performTextInput("New Note Title")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Write your note here...")
            .performTextInput("New Note Content")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Save note")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("New Note Title", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("New Note Content", useUnmergedTree = true).assertIsDisplayed()
    }

    @SmokeTest
    @Test
    fun createNoteViaEmptyStateButton() {
        composeTestRule.waitForIdle()

        composeTestRule.assertTextDisplayed("No notes yet")
        composeTestRule.onNodeWithText("Create Note").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Note Title")
            .performTextInput("Empty State Note")

        composeTestRule.onNodeWithText("Write your note here...")
            .performTextInput("Content from empty state")

        composeTestRule.onNodeWithContentDescription("Save note")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Empty State Note", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @SmokeTest
    @Test
    fun viewListOfNotes() {
        runBlocking {
            insertMultipleTestNotes(3)
        }

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Test Note 1", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Test Note 1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Content for note 1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Note 2", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Content for note 2", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Note 3", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Content for note 3", useUnmergedTree = true).assertIsDisplayed()
    }

    @SmokeTest
    @Test
    fun navigateToNoteDetails() {
        runBlocking {
            insertTestNote(title = "Test Note", content = "Test content")
        }

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Test Note", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Test Note", useUnmergedTree = true)
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.assertTextDisplayed("Test Note")
        composeTestRule.assertTextDisplayed("Test content")
    }

    @SmokeTest
    @Test
    fun deleteNoteFromListScreen() {
        runBlocking {
            insertTestNote(title = "Note to Delete", content = "This note will be deleted")
        }

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Note to Delete", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Note to Delete", useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Note to Delete", useUnmergedTree = true)
            .performTouchInput {
                down(center)
                advanceEventTime(durationMillis = 1000) // Long press duration
                up()
            }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Delete note")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("DELETE")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitForText("Note deleted successfully", timeoutMillis = 3000)

        composeTestRule.onNodeWithText("Note to Delete", useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @SmokeTest
    @Test
    fun starNoteFromListScreen() {
        runBlocking {
            insertTestNote(title = "Note to Star", content = "Content", isStarred = false)
        }

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Note to Star", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Add star", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitForText("Note starred", timeoutMillis = 3000)
        composeTestRule.assertTextDisplayed("Note starred")

        composeTestRule.onNodeWithContentDescription("Remove star", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @SmokeTest
    @Test
    fun unstarNoteFromListScreen() {
        runBlocking {
            insertTestNote(title = "Starred Note", content = "Content", isStarred = true)
        }

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Starred Note", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Remove star", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitForText("Star removed", timeoutMillis = 3000)
        composeTestRule.assertTextDisplayed("Star removed")

        composeTestRule.onNodeWithContentDescription("Add star", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @SmokeTest
    @Test
    fun shareNoteFromListScreen() {
        runBlocking {
            insertTestNote(title = "Note to Share", content = "This note will be shared")
        }

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Note to Share", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Note to Share", useUnmergedTree = true)
            .performTouchInput {
                down(center)
                advanceEventTime(durationMillis = 1000)
                up()
            }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Share note")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()
    }
}
