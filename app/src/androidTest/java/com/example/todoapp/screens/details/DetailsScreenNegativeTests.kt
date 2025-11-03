package com.example.todoapp.screens.details

import com.example.todoapp.annotations.NegativeTest
import com.example.todoapp.base.BaseTest
import com.example.todoapp.screens.list.ListScreen
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test

@HiltAndroidTest
class DetailsScreenNegativeTests : BaseTest() {

    private val listScreen = ListScreen(composeTestRule)
    private val detailsScreen = DetailsScreen(composeTestRule)
    val noteTitle = "Test Note"
    val noteContent = "Test Content"

    @NegativeTest
    @Test
    fun deleteOptionNotAvailableForUnsavedNote() {
        listScreen.clickCreateNoteButton()
        detailsScreen.waitForIdle()
    }

    @NegativeTest
    @Test
    fun createNoteWithEmptyTitleAndContent() {
        listScreen.clickCreateNoteButton()
        detailsScreen.clickSaveNote()
        listScreen.assertSnackbarIsDisplayed("Cannot save empty note. Please add a title or content.")
    }

    @NegativeTest
    @Test
    fun cannotRemoveContentAndTitleFromANote() {
        runBlocking {
            insertTestNote(title = noteTitle, content = noteContent)
        }

        listScreen.waitUntilNoteIsDisplayed(noteTitle)
            .clickNoteWithTitle(noteTitle)
        detailsScreen.replaceNoteTitle(noteTitle, "")
            .replaceNoteContent(noteContent, "")
            .clickSaveNote()
        listScreen.assertSnackbarIsDisplayed("Cannot save empty note. Please add a title or content.")
    }
}
