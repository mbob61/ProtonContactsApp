package com.example.todoapp.screens.list

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.example.todoapp.screens.BaseScreen

/**
 * Represents the List Screen and its interactions.
 */
class ListScreen(composeTestRule: ComposeContentTestRule) : BaseScreen(composeTestRule) {

    fun clickCreateNewNoteFab(): ListScreen {
        composeTestRule.onNodeWithContentDescription("Create new note").performClick()
        return this
    }

    fun clickCreateNoteButton(): ListScreen {
        composeTestRule.onNodeWithText("Create Note").performClick()
        return this
    }

    fun assertNoteIsDisplayed(title: String, content: String? = null): ListScreen {
        composeTestRule.onNodeWithText(title, useUnmergedTree = true).assertIsDisplayed()
        content?.let {
            composeTestRule.onNodeWithText(it, useUnmergedTree = true).assertIsDisplayed()
        }
        return this
    }

    fun assertNoNotesMessageIsDisplayed(): ListScreen {
        composeTestRule.onNodeWithText("No notes yet").assertIsDisplayed()
        return this
    }

    fun clickNoteWithTitle(title: String): ListScreen {
        composeTestRule.onNodeWithText(title, useUnmergedTree = true).performClick()
        return this
    }

    fun waitUntilNoteIsDisplayed(title: String): ListScreen {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText(title, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        return this
    }

    fun longPressOnNote(title: String): ListScreen {
        composeTestRule.onNodeWithText(title, useUnmergedTree = true)
            .performTouchInput {
                down(center)
                advanceEventTime(durationMillis = 1000)
                up()
            }
        return this
    }

    fun clickDelete(): ListScreen {
        composeTestRule.onNodeWithContentDescription("Delete note")
            .assertIsDisplayed()
            .performClick()
        return this
    }

    fun confirmDeletion(): ListScreen {
        composeTestRule.onNodeWithText("DELETE")
            .performClick()
        return this
    }

    fun dismissDeletion(): ListScreen {
        composeTestRule.onNodeWithContentDescription("Dismiss").performClick()
        return this
    }

    fun assertNoteDoesNotExist(title: String): ListScreen {
        composeTestRule.onNodeWithText(title, useUnmergedTree = true)
            .assertDoesNotExist()
        return this
    }

    fun clickStarOnNote(isStarred: Boolean): ListScreen {
        val contentDescription = if (isStarred) "Remove star" else "Add star"
        composeTestRule.onAllNodesWithContentDescription(contentDescription, useUnmergedTree = true)[0]
            .performClick()
        return this
    }

    fun assertSnackbarIsDisplayed(text: String): ListScreen {
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(text).assertIsDisplayed()
        return this
    }

    fun assertStarIsDisplayed(isStarred: Boolean): ListScreen {
        val contentDescription = if (isStarred) "Remove star" else "Add star"
        composeTestRule.onNodeWithContentDescription(contentDescription, useUnmergedTree = true)
            .assertIsDisplayed()
        return this
    }

    fun clickShare(): ListScreen {
        composeTestRule.onNodeWithContentDescription("Share note")
            .assertIsDisplayed()
            .performClick()
        return this
    }

    fun assertSelectionModeToolbarIsDisplayed(): ListScreen {
        composeTestRule.onNodeWithContentDescription("Exit selection mode").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Share note").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Delete note").assertIsDisplayed()
        return this
    }

    fun clickExitSelectionMode(): ListScreen {
        composeTestRule.onNodeWithContentDescription("Exit selection mode").performClick()
        return this
    }

    fun assertMainToolbarIsDisplayed(): ListScreen {
        composeTestRule.onNodeWithContentDescription("Exit selection mode").assertDoesNotExist()
        return this
    }

    fun assertNoteIsSelected(title: String, isSelected: Boolean): ListScreen {
        val expectedCount = if (isSelected) 2 else 1
        composeTestRule.onAllNodesWithText(title, useUnmergedTree = true).assertCountEquals(expectedCount)
        return this
    }

    fun assertStarredCount(count: Int): ListScreen {
        composeTestRule.onAllNodesWithContentDescription("Remove star", useUnmergedTree = true)
            .assertCountEquals(count)
        return this
    }
}
