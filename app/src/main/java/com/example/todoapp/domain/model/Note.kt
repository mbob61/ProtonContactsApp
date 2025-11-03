package com.example.todoapp.domain.model

import java.text.SimpleDateFormat
import java.util.*

data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val isStarred: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    fun isEmpty(): Boolean = title.isBlank() && content.isBlank()

    fun getFormattedDate(): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return formatter.format(updatedAt)
    }

    fun getContentPreview(): String {
        return if (content.length > 100) {
            "${content.take(100)}..."
        } else {
            content
        }
    }

    fun getDisplayTitle(): String {
        return title.ifBlank {
            content.take(50).let { if (it.length >= 50) "$it..." else it }
        }
    }
}