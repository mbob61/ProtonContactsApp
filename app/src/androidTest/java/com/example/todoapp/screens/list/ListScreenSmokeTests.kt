package com.example.todoapp.screens.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.todoapp.annotations.SmokeTest
import com.example.todoapp.base.BaseTest
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
        composeTestRule.onNodeWithText("New Note Content", useUnmergedTree = true)
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
        composeTestRule.onNodeWithText("Content for note 1", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Note 2", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Content for note 2", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Note 3", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Content for note 3", useUnmergedTree = true)
            .assertIsDisplayed()
    }
}
