package com.example.todoapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.todoapp.data.local.entity.TodoNote
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_notes ORDER BY updatedAt DESC")
    fun getAllNotesPagingSource(): PagingSource<Int, TodoNote>

    @Query("SELECT * FROM todo_notes ORDER BY updatedAt DESC")
    fun getAllNotesFlow(): Flow<List<TodoNote>>

    @Query("SELECT * FROM todo_notes WHERE id = :id")
    suspend fun getNoteById(id: Long): TodoNote?

    @Query("SELECT * FROM todo_notes WHERE id = :id")
    fun getNoteByIdFlow(id: Long): Flow<TodoNote?>

    @Insert
    suspend fun insertNote(note: TodoNote): Long

    @Update
    suspend fun updateNote(note: TodoNote)

    @Delete
    suspend fun deleteNote(note: TodoNote)

    @Query("DELETE FROM todo_notes")
    suspend fun deleteAllNotes()

    @Query("UPDATE todo_notes SET isStarred = :isStarred, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun toggleStar(noteId: Long, isStarred: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM todo_notes WHERE isStarred = 1 ORDER BY updatedAt DESC")
    fun getStarredNotesFlow(): Flow<List<TodoNote>>

    @Query("SELECT COUNT(*) FROM todo_notes")
    suspend fun getNotesCount(): Int

    @Query("SELECT COUNT(*) FROM todo_notes WHERE isStarred = 1")
    suspend fun getStarredNotesCount(): Int
}