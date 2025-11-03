package com.example.todoapp.base

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.rule.IntentsRule
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

    @get:Rule(order = 2)
    val intentsRule = IntentsRule()

    @Inject
    lateinit var appDatabase: AppDatabase

    lateinit var todoDao: TodoDao

    @Before
    fun setup() {
        hiltRule.inject()
        todoDao = appDatabase.todoDao()
        clearAllNotes()
        composeTestRule.waitForIdle()
    }

    @After
    fun tearDown() {
        clearAllNotes()
    }

    private fun clearAllNotes() {
        runBlocking {
            todoDao.deleteAllNotes()
        }
    }

    suspend fun insertTestNote(
        title: String = "Test Note",
        content: String = "Test content",
        isStarred: Boolean = false
    ): Long {
        val note = TodoNote(
            title = title,
            content = content,
            isStarred = isStarred,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return todoDao.insertNote(note)
    }

    suspend fun insertMultipleTestNotes(count: Int): List<Long> {
        val ids = mutableListOf<Long>()
        for (i in 1..count) {
            ids.add(insertTestNote(title = "Test Note $i", content = "Content for note $i"))
        }
        return ids
    }
}
