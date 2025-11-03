package com.example.todoapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.domain.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<DetailsViewState>(DetailsViewState.Loading)
    val viewState: StateFlow<DetailsViewState> = _viewState.asStateFlow()

    private val _effect = MutableSharedFlow<DetailsEffect?>()
    val effect: SharedFlow<DetailsEffect?> = _effect.asSharedFlow()

    private var currentNote: Note? = null
    private var originalNote: Note? = null

    fun handleIntent(intent: DetailsIntent, noteId: Long? = null) {
        when (intent) {
            is DetailsIntent.LoadNote -> noteId?.let { loadNote(it) }
            is DetailsIntent.CreateNewNote -> createNewNote()
            is DetailsIntent.TitleChanged -> updateTitle(intent.title)
            is DetailsIntent.ContentChanged -> updateContent(intent.content)
            is DetailsIntent.SaveNote -> saveNote()
            is DetailsIntent.ToggleStar -> toggleStar()
            is DetailsIntent.DeleteNote -> showDeleteConfirmation()
            is DetailsIntent.ConfirmDelete -> confirmDelete()
            is DetailsIntent.CancelDelete -> cancelDelete()
            is DetailsIntent.NavigateBack -> navigateBack()
            is DetailsIntent.DiscardChanges -> discardChanges()
        }
    }

    private fun loadNote(noteId: Long) {
        viewModelScope.launch {
            try {
                _viewState.value = DetailsViewState.Loading
                repository.getNoteById(noteId).collect { note ->
                    if (note != null) {
                        currentNote = note
                        originalNote = note.copy()
                        _viewState.value = DetailsViewState.Content(note = note)
                    } else {
                        _viewState.value = DetailsViewState.Error("Note not found")
                    }
                }
            } catch (e: Exception) {
                _viewState.value = DetailsViewState.Error("Failed to load note: ${e.message}")
            }
        }
    }

    private fun createNewNote() {
        currentNote = Note()
        originalNote = Note()
        _viewState.value = DetailsViewState.CreatingNewNote
    }

    private fun updateTitle(title: String) {
        currentNote = currentNote?.copy(title = title)
        updateDirtyState()
    }

    private fun updateContent(content: String) {
        currentNote = currentNote?.copy(content = content)
        updateDirtyState()
    }

    private fun updateDirtyState() {
        val currentState = _viewState.value
        if (currentState is DetailsViewState.Content || currentState is DetailsViewState.CreatingNewNote) {
            val note = currentNote ?: return
            val isDirty = note != originalNote
            _viewState.value = DetailsViewState.Content(
                note = note,
                isDirty = isDirty,
                showDeleteConfirmation = if (currentState is DetailsViewState.Content) currentState.showDeleteConfirmation else false
            )
        }
    }

    private fun saveNote() {
        val noteToSave = currentNote ?: return

        viewModelScope.launch {
            if (noteToSave.isEmpty()) {
                _effect.emit(DetailsEffect.ShowError("Cannot save empty note. Please add a title or content."))
                return@launch
            }
            try {
                val updatedNote = noteToSave.copy(updatedAt = Date())

                if (noteToSave.id == 0L) {
                    val createdNote = repository.createNote(updatedNote)
                    currentNote = createdNote
                    originalNote = createdNote.copy()
                    _effect.emit(DetailsEffect.NoteSaved(createdNote))
                } else {
                    repository.updateNote(updatedNote)
                    originalNote = updatedNote.copy()
                    _effect.emit(DetailsEffect.NoteSaved(updatedNote))
                }

                _viewState.value = DetailsViewState.Content(
                    note = currentNote!!,
                    isDirty = false
                )
                _effect.emit(DetailsEffect.NavigateBack)
            } catch (e: Exception) {
                _effect.emit(DetailsEffect.ShowError("Failed to save note: ${e.message}"))
            }
        }
    }

    private fun toggleStar() {
        currentNote?.let { note ->
            val updatedNote = note.copy(isStarred = !note.isStarred, updatedAt = Date())
            currentNote = updatedNote
            updateDirtyState()

            viewModelScope.launch {
                try {
                    repository.toggleStar(note.id, updatedNote.isStarred)
                    originalNote = updatedNote.copy()
                    _viewState.value = DetailsViewState.Content(
                        note = updatedNote,
                        isDirty = false
                    )
                } catch (e: Exception) {

                    currentNote = note
                    updateDirtyState()
                    viewModelScope.launch {
                        _effect.emit(DetailsEffect.ShowError("Failed to toggle star: ${e.message}"))
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmation() {
        val currentState = _viewState.value
        if (currentState is DetailsViewState.Content) {
            _viewState.value = currentState.copy(showDeleteConfirmation = true)
            currentNote?.let { note ->
                    viewModelScope.launch {
                        _effect.emit(DetailsEffect.ShowDeleteConfirmation(note.getDisplayTitle()))
                    }
                }
        }
    }

    private fun confirmDelete() {
        currentNote?.let { note ->
            if (note.id != 0L) {
                viewModelScope.launch {
                    try {
                        repository.deleteNote(note.id)
                        _effect.emit(DetailsEffect.NoteDeleted)
                    } catch (e: Exception) {
                        _effect.emit(DetailsEffect.ShowError("Failed to delete note: ${e.message}"))
                    }
                }
            } else {
                viewModelScope.launch {
                    _effect.emit(DetailsEffect.NavigateBack)
                }
            }
        }
    }

    private fun cancelDelete() {
        val currentState = _viewState.value
        if (currentState is DetailsViewState.Content) {
            _viewState.value = currentState.copy(showDeleteConfirmation = false)
        }
    }

    private fun navigateBack() {
        val currentState = _viewState.value
        if (currentState is DetailsViewState.Content && currentState.isDirty) {
            viewModelScope.launch {
                _effect.emit(DetailsEffect.ShowDiscardChangesDialog)
            }
        } else {
            viewModelScope.launch {
                _effect.emit(DetailsEffect.NavigateBack)
            }
        }
    }

    private fun discardChanges() {
        viewModelScope.launch {
            _effect.emit(DetailsEffect.NavigateBack)
        }
    }

    fun getCurrentNote(): Note? = currentNote
}