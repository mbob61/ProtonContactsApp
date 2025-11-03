package com.example.todoapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.todoapp.data.local.dao.TodoDao
import com.example.todoapp.data.local.mapper.TodoNoteMapper
import com.example.todoapp.data.local.entity.TodoNote
import com.example.todoapp.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoRepository {

    override fun getAllNotes(): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { todoDao.getAllNotesPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { todoNote -> TodoNoteMapper.toDomain(todoNote) }
        }
    }

    override fun getNoteById(id: Long): Flow<Note?> {
        return todoDao.getNoteByIdFlow(id).map { entity ->
            entity?.let { TodoNoteMapper.toDomain(it) }
        }
    }

    override suspend fun createNote(note: Note): Note {
        val entity = TodoNoteMapper.toEntity(note.copy(
            createdAt = Date(),
            updatedAt = Date()
        ))
        val insertedId = todoDao.insertNote(entity)
        return note.copy(id = insertedId)
    }

    override suspend fun updateNote(note: Note) {
        val entity = TodoNoteMapper.toEntity(note.copy(
            updatedAt = Date()
        ))
        todoDao.updateNote(entity)
    }

    override suspend fun deleteNote(id: Long) {
        val note = todoDao.getNoteById(id)
        note?.let { todoDao.deleteNote(it) }
    }

    override suspend fun toggleStar(id: Long, isStarred: Boolean) {
        todoDao.toggleStar(id, isStarred)
    }

    override fun getStarredNotes(): Flow<List<Note>> {
        return todoDao.getStarredNotesFlow().map { entities ->
            TodoNoteMapper.toDomainList(entities)
        }
    }

    override fun getNotesCount(): Flow<Int> {
        return kotlinx.coroutines.flow.flow {
            emit(todoDao.getNotesCount())
        }
    }

    override fun getStarredNotesCount(): Flow<Int> {
        return kotlinx.coroutines.flow.flow {
            emit(todoDao.getStarredNotesCount())
        }
    }
}