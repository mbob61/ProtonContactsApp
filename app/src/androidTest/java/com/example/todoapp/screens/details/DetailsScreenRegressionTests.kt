package com.example.todoapp.screens.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.example.todoapp.annotations.RegressionTest
import com.example.todoapp.base.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Regression tests for the Details Screen.
 */
@HiltAndroidTest
@RegressionTest
class DetailsScreenRegressionTests : BaseTest() {

    @Test
    fun editAndDiscardChanges() {
        runBlocking {
            insertTestNote(
                title = "Original Title",
                content = "Original content"
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Original Title", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Original Title", useUnmergedTree = true)
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Original Title")
            .performTextReplacement("Updated Title")

        composeTestRule.onNodeWithText("Original content")
            .performTextReplacement("Updated content")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Back")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Discard Changes?").assertIsDisplayed()

        composeTestRule.onNodeWithText("Discard").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Original Title", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Original content", useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Updated Title", useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun editAndCancelDiscard() {
        runBlocking {
            insertTestNote(
                title = "Original Title",
                content = "Original content"
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Original Title", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Original Title", useUnmergedTree = true)
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Original Title")
            .performTextReplacement("Updated Title")

        composeTestRule.onNodeWithContentDescription("Back")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Discard Changes?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Updated Title").assertIsDisplayed()
    }

    @Test
    fun verifyNoChangesAllowForBackNavigation() {
        runBlocking {
            insertTestNote(
                title = "Clean Note",
                content = "Clean content"
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Clean Note", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Clean Note", useUnmergedTree = true)
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Back")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Discard Changes?").assertDoesNotExist()
        composeTestRule.onNodeWithText("Clean Note", useUnmergedTree = true).assertIsDisplayed()
    }


    @Test
    fun verifyChangesPreventBackNavigation() {
        runBlocking {
            insertTestNote(
                title = "Clean Note",
                content = "Clean content"
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Clean Note", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Clean Note", useUnmergedTree = true)
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Clean content")
            .performTextReplacement("Updated content")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Back")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Discard Changes?").assertExists()
    }

    @Test
    fun cancelDeleteFromDetailsScreen() {
        runBlocking {
            insertTestNote(title = "Note to Keep")
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Note to Keep", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Note to Keep", useUnmergedTree = true)
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Delete note").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Delete Note").assertIsDisplayed()

        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Delete Note").assertDoesNotExist()
        composeTestRule.onNodeWithText("Note to Keep").assertIsDisplayed()
    }

    @Test
    fun starNoteFromDetailsScreen() {
        runBlocking {
            insertTestNote(title = "Star Me Detailed", isStarred = false)
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Star Me Detailed", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Star Me Detailed", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Add star").performClick()

        composeTestRule.onNodeWithContentDescription("Remove star").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Save note").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Remove star", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun toggleStarMultipleTimes() {
        runBlocking {
            insertTestNote(title = "Toggle Star Test")
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Toggle Star Test", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Add star", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Remove star", useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Remove star", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Add star", useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Add star", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Remove star", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun navigateBackFromDetails() {
        runBlocking {
            insertTestNote(title = "Navigate Back Test")
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Navigate Back Test", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Navigate Back Test", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Navigate Back Test", useUnmergedTree = true).assertIsDisplayed()
    }
}
