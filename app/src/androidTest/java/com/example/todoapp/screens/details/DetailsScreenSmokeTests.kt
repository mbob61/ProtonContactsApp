package com.example.todoapp.screens.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.example.todoapp.annotations.SmokeTest
import com.example.todoapp.base.BaseTest
import com.example.todoapp.utils.assertTextDisplayed
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test

@HiltAndroidTest
class DetailsScreenSmokeTests : BaseTest() {

    @SmokeTest
    @Test
    fun editExistingNote() {
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

        composeTestRule.onNodeWithText("Test Note")
            .performTextReplacement("Updated Title")

        composeTestRule.onNodeWithText("Test content")
            .performTextReplacement("Updated content")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Save note")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Updated Title", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.assertTextDisplayed("Updated Title")
        composeTestRule.assertTextDisplayed("Updated content")
    }

    @SmokeTest
    @Test
    fun deleteNoteFromDetailsScreen() {
        runBlocking {
            insertTestNote(title = "Note to Delete", content = "This note will be deleted")
        }

        // Wait for the note to appear on the list screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Note to Delete", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click the note to navigate to details
        composeTestRule.onNodeWithText("Note to Delete", useUnmergedTree = true)
            .performClick()

        composeTestRule.waitForIdle()

        // Verify we are on the details screen
        composeTestRule.onNodeWithText("Note to Delete").assertIsDisplayed()

        // Click the delete icon
        composeTestRule.onNodeWithContentDescription("Delete note")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify confirmation dialog appears
        composeTestRule.onNodeWithText("Delete Note").assertIsDisplayed()
        composeTestRule.onNodeWithText("Are you sure you want to delete", substring = true).assertIsDisplayed()

        // Confirm delete
        composeTestRule.onNodeWithText("Delete").performClick()

        composeTestRule.waitForIdle()

        // Verify the note is gone from the list screen
        composeTestRule.onNodeWithText("Note to Delete", useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @SmokeTest
    @Test
    fun starNoteFromDetailsScreen() {
        runBlocking {
            insertTestNote(title = "Star and Unstar Me", isStarred = false)
        }

        // Wait for the note to appear and navigate to it
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Star and Unstar Me", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Star and Unstar Me", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        // 1. Star the note
        composeTestRule.onNodeWithContentDescription("Add star").performClick()
        composeTestRule.waitForIdle()
        // Verify it is starred
        composeTestRule.onNodeWithContentDescription("Remove star").assertIsDisplayed()

        // 3. Save the note
        composeTestRule.onNodeWithContentDescription("Save note").performClick()
        composeTestRule.waitForIdle()

        // 4. Verify the final unstarred state on the list screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Star and Unstar Me", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithContentDescription("Remove star", useUnmergedTree = true).assertIsDisplayed()
    }
}
