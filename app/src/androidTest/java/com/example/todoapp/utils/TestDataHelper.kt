package com.example.todoapp.utils

import com.example.todoapp.domain.model.Note
import java.util.Date

/**
 * Helper class for creating test data models.
 * Provides factory methods for creating test Note objects.
 */
object TestDataHelper {

    /**
     * Create a basic test note with title and content.
     */
    fun createTestNote(
        id: Long = 0L,
        title: String = "Test Note",
        content: String = "This is test content",
        isStarred: Boolean = false,
        createdAt: Date = Date(),
        updatedAt: Date = Date()
    ): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            isStarred = isStarred,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

