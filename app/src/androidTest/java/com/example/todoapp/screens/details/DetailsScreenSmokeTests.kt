package com.example.todoapp.screens.details

import com.example.todoapp.annotations.SmokeTest
import com.example.todoapp.base.BaseTest
import com.example.todoapp.screens.list.ListScreen
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test

@HiltAndroidTest
class DetailsScreenSmokeTests : BaseTest() {

    private val listScreen = ListScreen(composeTestRule)
    private val detailsScreen = DetailsScreen(composeTestRule)
    val noteTitle = "Test Note"
    val noteContent = "Test Content"

    @SmokeTest
    @Test
    fun editExistingNote() {
        val updatedTitle = "UpdatedTitle"
        val updatedContent = "UpdatedContent"
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)

        detailsScreen.replaceNoteTitle(noteTitle, updatedTitle)
            .replaceNoteContent(noteContent, updatedContent)
            .clickSaveNote()

        listScreen.waitUntilNoteIsDisplayed(updatedTitle)
            .assertNoteIsDisplayed(updatedTitle, updatedContent)
    }

    @SmokeTest
    @Test
    fun deleteNoteFromDetailsScreen() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)
            .waitForIdle()

        detailsScreen.assertMessageIsDisplayed(noteTitle)
            .clickDeleteNote()
            .assertMessageIsDisplayed("Delete Note")
            .confirmDeletion()
            .waitForIdle()

        listScreen.assertNoteDoesNotExist(noteTitle)
            .assertMessageIsDisplayed("Note deleted successfully")
    }

    @SmokeTest
    @Test
    fun starNoteFromDetailsScreen() {
        runBlocking {
            insertTestNote(title = noteTitle, isStarred = false)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)
        detailsScreen.waitForIdle()

        detailsScreen.clickStar(isStarred = false)
            .assertStarIsDisplayed(isStarred = true)
            .clickSaveNote()
        listScreen.waitForIdle()

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .assertStarIsDisplayed(isStarred = true)
    }
}
