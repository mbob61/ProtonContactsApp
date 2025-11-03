package com.example.todoapp.screens.list

import android.content.Intent
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import com.example.todoapp.annotations.RegressionTest
import com.example.todoapp.base.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test

@HiltAndroidTest
class ListScreenRegressionTests : BaseTest() {

    private val listScreen = ListScreen(composeTestRule)
    private val noteTitle = "Note Title"

    @RegressionTest
    @Test
    fun verifyEmptyState() {
        listScreen.assertNoNotesMessageIsDisplayed()
    }

    @RegressionTest
    @Test
    fun cancelDeleteFromListScreen() {
        runBlocking {
            insertTestNote(title = noteTitle)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .longPressOnNote(noteTitle)
            .clickDelete()
            .dismissDeletion()
            .assertNoteIsSelected(noteTitle, isSelected = true)
    }

    @RegressionTest
    @Test
    fun enterSelectionModeViaLongPress() {
        runBlocking {
            insertTestNote(title = noteTitle)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .longPressOnNote(noteTitle)
            .assertSelectionModeToolbarIsDisplayed()
    }

    @RegressionTest
    @Test
    fun exitSelectionMode() {
        runBlocking {
            insertTestNote(title = noteTitle)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .longPressOnNote(noteTitle)
            .clickExitSelectionMode()
            .assertMainToolbarIsDisplayed()
    }

    @RegressionTest
    @Test
    fun verifySingleSelection() {
        val noteA = "Note A"
        val noteB = "Note B"
        runBlocking {
            insertTestNote(title = noteA)
            insertTestNote(title = noteB)
        }

        listScreen.waitUntilNoteIsDisplayed(noteA)
            .waitUntilNoteIsDisplayed(noteB)
            .longPressOnNote(noteA)
            .assertNoteIsSelected(noteA, isSelected = true)
            .assertNoteIsSelected(noteB, isSelected = false)
            .longPressOnNote(noteB)
            .assertNoteIsSelected(noteB, isSelected = true)
            .assertNoteIsSelected(noteA, isSelected = false)
    }

    @RegressionTest
    @Test
    fun verifyStarredCount() {
        runBlocking {
            insertTestNote(title = "Starred Note 1", isStarred = true)
            insertTestNote(title = "Starred Note 2", isStarred = true)
            insertTestNote(title = "Unstarred Note 1", isStarred = false)
        }

        listScreen.waitUntilNoteIsDisplayed("Starred Note 1")
            .assertStarredCount(2)
            .clickStarOnNote(isStarred = true)
            .assertStarredCount(1)
    }

    @RegressionTest
    @Test
    fun shareNoteWithEmptyTitle() {
        val noteContent = "Shared content"
        runBlocking {
            insertTestNote(title = "", content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteContent)
            .longPressOnNote(noteContent)
            .clickShare()

        intended(hasAction(Intent.ACTION_CHOOSER))
        intended(hasExtra(Intent.EXTRA_INTENT, allOf(
            hasAction(Intent.ACTION_SEND),
            hasExtra(Intent.EXTRA_SUBJECT, "Shared Note"),
            hasExtra(Intent.EXTRA_TEXT, noteContent)
        )))
    }

    @RegressionTest
    @Test
    fun shareNoteWithEmptyContent() {
        runBlocking {
            insertTestNote(title = noteTitle, content = "")
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .longPressOnNote(noteTitle)
            .clickShare()

        intended(hasAction(Intent.ACTION_CHOOSER))
        intended(hasExtra(Intent.EXTRA_INTENT, allOf(
            hasAction(Intent.ACTION_SEND),
            hasExtra(Intent.EXTRA_TEXT, "$noteTitle\n\n")
        )))
    }

    @RegressionTest
    @Test
    fun verifyNotePersistence() {
        runBlocking {
            insertTestNote(title = noteTitle)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .assertNoteIsDisplayed(noteTitle)

        composeTestRule.activityRule.scenario.recreate()

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .assertNoteIsDisplayed(noteTitle)
    }
}
