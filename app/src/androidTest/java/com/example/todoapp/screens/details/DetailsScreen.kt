package com.example.todoapp.screens.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.example.todoapp.screens.BaseScreen

/**
 * Represents the Details Screen and its interactions.
 */
class DetailsScreen(composeTestRule: ComposeContentTestRule) : BaseScreen(composeTestRule) {

    fun enterNoteTitle(title: String): DetailsScreen {
        composeTestRule.onNodeWithText("Note Title").performTextInput(title)
        return this
    }

    fun enterNoteContent(content: String): DetailsScreen {
        composeTestRule.onNodeWithText("Write your note here...").performTextInput(content)
        return this
    }

    fun replaceNoteTitle(currentTitle: String, newTitle: String): DetailsScreen {
        composeTestRule.onNodeWithText(currentTitle).performTextReplacement(newTitle)
        return this
    }

    fun replaceNoteContent(currentContent: String, newContent: String): DetailsScreen {
        composeTestRule.onNodeWithText(currentContent).performTextReplacement(newContent)
        return this
    }

    fun clickSaveNote(): DetailsScreen {
        composeTestRule.onNodeWithContentDescription("Save note").performClick()
        waitForIdle()
        return this
    }

    fun assertNoteTitleIsDisplayed(title: String): DetailsScreen {
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        return this
    }

    fun assertNoteContentIsDisplayed(content: String): DetailsScreen {
        composeTestRule.onNodeWithText(content).assertIsDisplayed()
        return this
    }

    fun clickDeleteNote(): DetailsScreen {
        composeTestRule.onNodeWithContentDescription("Delete note").performClick()
        return this
    }

    fun confirmDeletion(): DetailsScreen {
        composeTestRule.onNodeWithText("Delete").performClick()
        return this
    }

    fun clickBack(): DetailsScreen {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        return this
    }

    fun assertDiscardChangesDialogIsDisplayed(): DetailsScreen {
        composeTestRule.onNodeWithText("Discard Changes?").assertIsDisplayed()
        return this
    }

    fun clickDiscard(): DetailsScreen {
        composeTestRule.onNodeWithText("Discard").performClick()
        return this
    }

    fun clickCancel(): DetailsScreen {
        composeTestRule.onNodeWithText("Cancel").performClick()
        return this
    }

    fun assertDiscardChangesDialogDoesNotExist(): DetailsScreen {
        composeTestRule.onNodeWithText("Discard Changes?").assertDoesNotExist()
        return this
    }

    fun clickStar(isStarred: Boolean): DetailsScreen {
        val contentDescription = if (isStarred) "Remove star" else "Add star"
        composeTestRule.onNodeWithContentDescription(contentDescription).performClick()
        return this
    }

    fun assertStarIsDisplayed(isStarred: Boolean): DetailsScreen {
        val contentDescription = if (isStarred) "Remove star" else "Add star"
        composeTestRule.onNodeWithContentDescription(contentDescription).assertIsDisplayed()
        return this
    }
}
