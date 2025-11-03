package com.example.todoapp.screens.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.example.todoapp.annotations.NegativeTest
import com.example.todoapp.annotations.SmokeTest
import com.example.todoapp.base.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Negative tests for the Details Screen.
 */
@HiltAndroidTest
@NegativeTest
class DetailsScreenNegativeTests : BaseTest() {

    @Test
    fun deleteOptionNotAvailableForUnsavedNote() {
        composeTestRule.onNodeWithText("Create Note").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Delete note").assertDoesNotExist()
    }

    @Test
    fun createNoteWithEmptyTitleAndContent() {
        composeTestRule.onNodeWithText("Create Note").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Add star").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Save note").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Cannot save empty note. Please add a title or content.").assertIsDisplayed()

     }

    @NegativeTest
    @Test
    fun cannotRemoveContentAndTitleFromANote() {
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
            .performTextReplacement("")

        composeTestRule.onNodeWithText("Test content")
            .performTextReplacement("")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Save note")
            .performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Cannot save empty note. Please add a title or content.").assertIsDisplayed()
    }
}
