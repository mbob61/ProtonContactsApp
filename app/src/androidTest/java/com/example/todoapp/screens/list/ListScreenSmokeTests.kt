package com.example.todoapp.screens.list

import android.content.Intent
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import com.example.todoapp.annotations.SmokeTest
import com.example.todoapp.base.BaseTest
import com.example.todoapp.screens.details.DetailsScreen
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.AllOf.allOf
import org.junit.Test

@HiltAndroidTest
class ListScreenSmokeTests : BaseTest() {
    private val listScreen = ListScreen(composeTestRule)
    private val detailsScreen = DetailsScreen(composeTestRule)

    val noteTitle = "Test Note"
    val noteContent = "Test Content"

    @SmokeTest
    @Test
    fun createNewNoteViaFAB() {
        listScreen.clickCreateNewNoteFab()
        detailsScreen.enterNoteTitle(noteTitle)
            .enterNoteContent(noteContent)
            .clickSaveNote()

        listScreen.assertNoteIsDisplayed(noteTitle, noteContent)
    }

    @SmokeTest
    @Test
    fun createNoteViaEmptyStateButton() {
        listScreen.assertMessageIsDisplayed("No notes yet")
            .clickCreateNoteButton()

        detailsScreen.enterNoteTitle(noteTitle)
            .enterNoteContent(noteContent)
            .clickSaveNote()

        listScreen.assertNoteIsDisplayed(noteTitle, noteContent)
    }

    @SmokeTest
    @Test
    fun viewListOfNotes() {
        runBlocking {
            insertMultipleTestNotes(3)
        }

        listScreen.waitUntilNoteIsDisplayed("Test Note 1")
            .assertNoteIsDisplayed("Test Note 1", "Content for note 1")
            .assertNoteIsDisplayed("Test Note 2", "Content for note 2")
            .assertNoteIsDisplayed("Test Note 3", "Content for note 3")
    }

    @SmokeTest
    @Test
    fun navigateToNoteDetails() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)

        detailsScreen.assertMessageIsDisplayed(noteTitle)
            .assertMessageIsDisplayed(noteContent)
    }

    @SmokeTest
    @Test
    fun deleteNoteFromListScreen() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .longPressOnNote(noteTitle)
            .clickDelete()
            .confirmDeletion()
            .assertNoteDoesNotExist(noteTitle)
            .assertMessageIsDisplayed("Note deleted successfully")

    }

    @SmokeTest
    @Test
    fun starNoteFromListScreen() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent, isStarred = false)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickStarOnNote(isStarred = false)
            .assertStarIsDisplayed(isStarred = true)
            .assertMessageIsDisplayed("Note starred")

    }

    @SmokeTest
    @Test
    fun unstarNoteFromListScreen() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent, isStarred = true)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickStarOnNote(isStarred = true)
            .assertStarIsDisplayed(isStarred = false)
            .assertMessageIsDisplayed("Star removed")

    }

    @SmokeTest
    @Test
    fun shareNoteFromListScreen() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .longPressOnNote(noteTitle)
            .clickShare()

        // Verify the chooser and the nested SEND intent are correct
        intended(hasAction(Intent.ACTION_CHOOSER))
        intended(hasExtra(Intent.EXTRA_INTENT, allOf(
            hasAction(Intent.ACTION_SEND),
            hasExtra(Intent.EXTRA_SUBJECT, noteTitle),
            hasExtra(Intent.EXTRA_TEXT, "$noteTitle\n\n$noteContent")
        )))
    }
}
