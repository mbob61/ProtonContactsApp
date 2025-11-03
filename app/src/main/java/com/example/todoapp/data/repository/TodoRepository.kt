package com.example.todoapp.data.repository

import androidx.paging.PagingData
import com.example.todoapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllNotes(): Flow<PagingData<Note>>

    fun getNoteById(id: Long): Flow<Note?>

    suspend fun createNote(note: Note): Note

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(id: Long)

    suspend fun toggleStar(id: Long, isStarred: Boolean)

    fun getStarredNotes(): Flow<List<Note>>

    fun getNotesCount(): Flow<Int>

    fun getStarredNotesCount(): Flow<Int>
}