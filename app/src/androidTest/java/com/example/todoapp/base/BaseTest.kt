package com.example.todoapp.base

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.todoapp.MainActivity
import com.example.todoapp.data.local.dao.TodoDao
import com.example.todoapp.data.local.database.AppDatabase
import com.example.todoapp.data.local.entity.TodoNote
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

@HiltAndroidTest
abstract class BaseTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var appDatabase: AppDatabase

    lateinit var todoDao: TodoDao

    @Before
    fun setup() {
        hiltRule.inject()
        todoDao = appDatabase.todoDao()
        // Clear all notes before each test
        clearAllNotes()
        composeTestRule.waitForIdle()
    }

    @After
    fun tearDown() {
        // Clear all notes after each test to ensure isolation
        clearAllNotes()
    }

    private fun clearAllNotes() {
        runBlocking {
            // Directly use the DAO to clear notes
            todoDao.deleteAllNotes()
        }
    }

    /**
     * Insert a test note directly into the database.
     * Useful for setting up test data without going through the UI.
     */
    suspend fun insertTestNote(
        title: String = "Test Note",
        content: String = "Test content",
        isStarred: Boolean = false,
        id: Long = 0
    ): Long {
        val note = TodoNote(
            id = id,
            title = title,
            content = content,
            isStarred = isStarred,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return todoDao.insertNote(note)
    }

    /**
     * Insert multiple test notes.
     */
    suspend fun insertMultipleTestNotes(count: Int): List<Long> {
        val ids = mutableListOf<Long>()
        for (i in 1..count) {
            val id = insertTestNote(
                title = "Test Note $i",
                content = "Content for note $i",
                isStarred = i % 2 == 0 // Alternate starred status
            )
            ids.add(id)
        }
        return ids
    }
}
