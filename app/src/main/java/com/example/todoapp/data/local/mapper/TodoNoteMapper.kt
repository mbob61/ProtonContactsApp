package com.example.todoapp.data.local.mapper

import com.example.todoapp.data.local.entity.TodoNote
import com.example.todoapp.domain.model.Note
import java.util.Date

object TodoNoteMapper {

    fun toDomain(entity: TodoNote): Note {
        return Note(
            id = entity.id,
            title = entity.title,
            content = entity.content,
            isStarred = entity.isStarred,
            createdAt = Date(entity.createdAt),
            updatedAt = Date(entity.updatedAt)
        )
    }

    fun toEntity(domain: Note): TodoNote {
        return TodoNote(
            id = domain.id,
            title = domain.title,
            content = domain.content,
            isStarred = domain.isStarred,
            createdAt = domain.createdAt.time,
            updatedAt = domain.updatedAt.time
        )
    }
    fun toDomainList(entities: List<TodoNote>): List<Note> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Note>): List<TodoNote> {
        return domains.map { toEntity(it) }
    }
}