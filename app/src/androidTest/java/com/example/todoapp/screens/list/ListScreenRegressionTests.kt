package com.example.todoapp.screens.list

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.example.todoapp.annotations.RegressionTest
import com.example.todoapp.base.BaseTest
import com.example.todoapp.utils.assertTextDisplayed
import com.example.todoapp.utils.waitForText
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test

@HiltAndroidTest
class ListScreenRegressionTests : BaseTest() {

    @RegressionTest
    @Test
    fun verifyEmptyState() {
        composeTestRule.waitForIdle()

        composeTestRule.assertTextDisplayed("No notes yet")
        composeTestRule.assertTextDisplayed("Create your first note to get started")
    }

    @RegressionTest
    @Test
    fun cancelDeleteFromListScreen() {
        runBlocking {
            insertTestNote(title = "Note to Keep")
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Note to Keep", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Note to Keep", useUnmergedTree = true)
            .performTouchInput {
                down(center)
                advanceEventTime(durationMillis = 1000)
                up()
            }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Delete note").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Delete \"Note to Keep\"?", substring = true).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Dismiss").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Note to Keep", useUnmergedTree = true)
            .assertCountEquals(2)
    }

    @RegressionTest
    @Test
    fun enterSelectionModeViaLongPress() {
        runBlocking {
            insertTestNote(title = "Select Me")
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Select Me", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Select Me", useUnmergedTree = true)
            .performTouchInput {
                down(center)
                advanceEventTime(durationMillis = 1000)
                up()
            }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Exit selection mode").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Share note").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Delete note").assertIsDisplayed()
    }

    @RegressionTest
    @Test
    fun exitSelectionMode() {
        runBlocking {
            insertTestNote(title = "Select and Exit")
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Select and Exit", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Select and Exit", useUnmergedTree = true)
            .performTouchInput {
                down(center)
                advanceEventTime(durationMillis = 1000)
                up()
            }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Exit selection mode").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("ToDo App").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Exit selection mode").assertDoesNotExist()
    }

    @RegressionTest
    @Test
    fun verifySingleSelection() {
        runBlocking {
            insertTestNote(title = "Note A")
            insertTestNote(title = "Note B")
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Note A", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty() &&
            composeTestRule
                .onAllNodesWithText("Note B", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Note A", useUnmergedTree = true)
            .performTouchInput { 
                down(center)
                advanceEventTime(durationMillis = 1000)
                up()
            }
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Note A").assertCountEquals(2)
        composeTestRule.onAllNodesWithText("Note B", useUnmergedTree = true).assertCountEquals(1)

        composeTestRule.onNodeWithText("Note B", useUnmergedTree = true)
            .performTouchInput { 
                down(center)
                advanceEventTime(durationMillis = 1000)
                up()
            }
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Note B").assertCountEquals(2)
        composeTestRule.onAllNodesWithText("Note A", useUnmergedTree = true).assertCountEquals(1)
    }

    @RegressionTest
    @Test
    fun verifyStarredCount() {
        runBlocking {
            insertTestNote(title = "Starred Note 1", isStarred = true)
            insertTestNote(title = "Starred Note 2", isStarred = true)
            insertTestNote(title = "Unstarred Note 1", isStarred = false)
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Starred Note 1", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithContentDescription("Remove star", useUnmergedTree = true)
            .assertCountEquals(2)

        composeTestRule.onAllNodesWithContentDescription("Remove star", useUnmergedTree = true)[0]
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithContentDescription("Remove star", useUnmergedTree = true)
            .assertCountEquals(1)
        composeTestRule.onAllNodesWithContentDescription("Add star", useUnmergedTree = true)
            .assertCountEquals(2)

        composeTestRule.onNodeWithText("Starred Note 1", useUnmergedTree = true)
            .performTouchInput { 
                down(center)
                advanceEventTime(durationMillis = 1000)
                up()
            }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Delete note")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForText("DELETE", timeoutMillis = 10000)

        composeTestRule.onNodeWithText("DELETE")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitForText("Note deleted successfully", timeoutMillis = 3000)

        composeTestRule.onAllNodesWithContentDescription("Remove star", useUnmergedTree = true)
            .assertCountEquals(0)
    }
}
