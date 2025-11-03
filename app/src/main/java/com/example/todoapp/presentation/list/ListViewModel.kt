package com.example.todoapp.presentation.list

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<ListViewState>(ListViewState.Loading)
    val viewState: StateFlow<ListViewState> = _viewState.asStateFlow()

    private val _effect = MutableSharedFlow<ListEffect?>()
    val effect: SharedFlow<ListEffect?> = _effect.asSharedFlow()

    init {
        handleIntent(ListIntent.LoadNotes)
        handleIntent(ListIntent.LoadCounts)
    }

    fun handleIntent(intent: ListIntent) {
        when (intent) {
            is ListIntent.LoadNotes -> loadNotes()
            is ListIntent.RefreshNotes -> refreshNotes()
            is ListIntent.CreateNewNote -> createNewNote()
            is ListIntent.NoteClicked -> {
                val currentState = _viewState.value
                if (currentState is ListViewState.Content && currentState.isSelectionMode) {
                    exitSelectionMode()
                } else {
                    navigateToDetails(intent.note)
                }
            }

            is ListIntent.NoteLongPressed -> enterSelectionMode(intent.note)
            is ListIntent.ToggleStar -> toggleStar(intent.noteId, intent.isStarred)
            is ListIntent.DeleteNote -> showDeleteConfirmation(intent.noteId)
            is ListIntent.LoadCounts -> loadCounts()
            is ListIntent.ExitSelectionMode -> exitSelectionMode()
            is ListIntent.ShareSelectedNote -> shareSelectedNote()
            is ListIntent.DeleteSelectedNote -> deleteSelectedNote()
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            try {
                _viewState.value = ListViewState.Loading
                val notesFlow = repository.getAllNotes()
                val totalCountFlow = repository.getNotesCount()
                val starredCountFlow = repository.getStarredNotesCount()

                combine(totalCountFlow, starredCountFlow) { totalCount, starredCount ->
                    totalCount to starredCount
                }.catch { exception ->
                    _viewState.value =
                        ListViewState.Error("Failed to load notes: ${exception.message}")
                }.collect { (totalCount, starredCount) ->
                    _viewState.value = ListViewState.Content(
                        notes = notesFlow,
                        totalCount = totalCount,
                        starredCount = starredCount,
                        isEmpty = totalCount == 0,
                        isSelectionMode = false,
                        selectedNote = null
                    )
                }

            } catch (e: Exception) {
                _viewState.value = ListViewState.Error("Failed to load notes: ${e.message}")
            }
        }
    }

    private fun refreshNotes() {
        loadNotes()
    }

    private fun createNewNote() {
        viewModelScope.launch {
            _effect.emit(ListEffect.NavigateToCreateNote)
        }
    }

    private fun navigateToDetails(note: Note) {
        viewModelScope.launch {
            _effect.emit(ListEffect.NavigateToDetails(note.id))
        }
    }

    private fun toggleStar(noteId: Long, isStarred: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleStar(noteId, isStarred)
                _effect.emit(ListEffect.NoteStarToggled(noteId, isStarred))
            } catch (e: Exception) {
                _effect.emit(ListEffect.ShowError("Failed to toggle star: ${e.message}"))
            }
        }
    }

    private fun showDeleteConfirmation(noteId: Long) {
        viewModelScope.launch {
            try {
                repository.getNoteById(noteId).collect { note ->
                    note?.let {
                        _effect.emit(
                            ListEffect.ShowDeleteConfirmation(
                                noteId = noteId,
                                noteTitle = it.getDisplayTitle()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _effect.emit(ListEffect.ShowError("Failed to load note details: ${e.message}"))
            }
        }
    }

    private fun loadCounts() {
        viewModelScope.launch {
            try {
                val totalCount = repository.getNotesCount()
                val starredCount = repository.getStarredNotesCount()

                combine(totalCount, starredCount) { tc, sc ->
                    tc to sc
                }.collect { (totalCount, starredCount) ->
                    val currentState = _viewState.value
                    if (currentState is ListViewState.Content) {
                        _viewState.value = currentState.copy(
                            totalCount = totalCount,
                            starredCount = starredCount,
                            isEmpty = totalCount == 0
                        )
                    }
                }
            } catch (e: Exception) {
            }
        }
    }


    fun confirmDeleteNote(noteId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteNote(noteId)
                _effect.emit(ListEffect.NoteDeleted)

                handleIntent(ListIntent.LoadNotes)
            } catch (e: Exception) {
                _effect.emit(ListEffect.ShowError("Failed to delete note: ${e.message}"))
            }
        }
    }

    private fun enterSelectionMode(note: Note) {
        val currentState = _viewState.value
        if (currentState is ListViewState.Content) {
            _viewState.value = currentState.copy(
                isSelectionMode = true,
                selectedNote = note
            )
        }
    }

    private fun exitSelectionMode() {
        val currentState = _viewState.value
        if (currentState is ListViewState.Content) {
            _viewState.value = currentState.copy(
                isSelectionMode = false,
                selectedNote = null
            )
        }
    }

    private fun shareSelectedNote() {
        val currentState = _viewState.value
        if (currentState is ListViewState.Content && currentState.selectedNote != null) {
            viewModelScope.launch {
                _effect.emit(ListEffect.ShareNote(currentState.selectedNote))
            }
        }
    }

    private fun deleteSelectedNote() {
        val currentState = _viewState.value
        if (currentState is ListViewState.Content && currentState.selectedNote != null) {
            val note = currentState.selectedNote
            viewModelScope.launch {
                _effect.emit(
                    ListEffect.ShowDeleteConfirmation(
                        noteId = note.id,
                        noteTitle = note.getDisplayTitle()
                    )
                )
            }
        }
    }
}