package com.example.todoapp.screens.details

import com.example.todoapp.annotations.RegressionTest
import com.example.todoapp.base.BaseTest
import com.example.todoapp.screens.list.ListScreen
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test

@HiltAndroidTest
class DetailsScreenRegressionTests : BaseTest() {

    private val listScreen = ListScreen(composeTestRule)
    private val detailsScreen = DetailsScreen(composeTestRule)
    val noteTitle = "Test Note"
    val noteContent = "Test Content"

    @Test
    @RegressionTest
    fun editAndDiscardChanges() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)

        detailsScreen.replaceNoteTitle(noteTitle, "Updated Title")
            .replaceNoteContent(noteContent, "Updated content")
            .clickBack()
            .assertDiscardChangesDialogIsDisplayed()
            .clickDiscard()

        listScreen.assertNoteIsDisplayed(noteTitle, noteContent)
            .assertNoteDoesNotExist("Updated Title")
    }

    @Test
    @RegressionTest
    fun editAndCancelDiscard() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)

        detailsScreen.replaceNoteTitle(noteTitle, "Updated Title")
            .clickBack()
            .assertDiscardChangesDialogIsDisplayed()
            .clickCancel()
            .assertNoteTitleIsDisplayed("Updated Title")
    }

    @Test
    @RegressionTest
    fun verifyNoChangesAllowForBackNavigation() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)

        detailsScreen.clickBack()
            .assertDiscardChangesDialogDoesNotExist()

        listScreen.assertNoteIsDisplayed(noteTitle)
    }


    @Test
    @RegressionTest
    fun verifyChangesPreventBackNavigation() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)

        detailsScreen.replaceNoteContent(noteTitle, "Updated content")
            .clickBack()
            .assertDiscardChangesDialogIsDisplayed()
    }

    @Test
    @RegressionTest
    fun cancelDeleteFromDetailsScreen() {
        runBlocking {
            insertTestNote(title = noteTitle)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)

        detailsScreen.clickDeleteNote()
            .clickCancel()
            .assertNoteTitleIsDisplayed(noteTitle)
    }

    @Test
    @RegressionTest
    fun starNoteFromDetailsScreen() {
        runBlocking {
            insertTestNote(title = noteTitle, isStarred = false)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)

        detailsScreen.clickStar(isStarred = false)
            .assertStarIsDisplayed(isStarred = true)
            .clickSaveNote()

        listScreen.assertStarIsDisplayed(isStarred = true)
    }

    @Test
    @RegressionTest
    fun toggleStarMultipleTimes() {
        runBlocking {
            insertTestNote(title = noteTitle)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)

        detailsScreen.clickStar(isStarred = false)
            .assertStarIsDisplayed(isStarred = true)
            .clickStar(isStarred = true)
            .assertStarIsDisplayed(isStarred = false)
            .clickStar(isStarred = false)
            .assertStarIsDisplayed(isStarred = true)
    }

    @Test
    @RegressionTest
    fun navigateBackFromDetails() {
        runBlocking {
            insertTestNote(title = noteTitle)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)

        detailsScreen.clickBack()

        listScreen.assertNoteIsDisplayed(noteTitle)
    }
}
