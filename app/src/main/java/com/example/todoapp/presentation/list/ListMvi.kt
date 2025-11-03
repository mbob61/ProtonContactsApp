package com.example.todoapp.presentation.list

import androidx.paging.PagingData
import com.example.todoapp.domain.mvi.Effect
import com.example.todoapp.domain.mvi.Intent
import com.example.todoapp.domain.mvi.ViewState
import com.example.todoapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

sealed interface ListViewState : ViewState {
    data object Loading : ListViewState
    data class Content(
        val notes: Flow<PagingData<Note>>,
        val totalCount: Int = 0,
        val starredCount: Int = 0,
        val isEmpty: Boolean = false,
        val isSelectionMode: Boolean = false,
        val selectedNote: Note? = null
    ) : ListViewState
    data class Error(val message: String) : ListViewState
}

sealed interface ListIntent : Intent {
    data object LoadNotes : ListIntent
    data object RefreshNotes : ListIntent
    data object CreateNewNote : ListIntent
    data class NoteClicked(val note: Note) : ListIntent
    data class NoteLongPressed(val note: Note) : ListIntent
    data class ToggleStar(val noteId: Long, val isStarred: Boolean) : ListIntent
    data class DeleteNote(val noteId: Long) : ListIntent
    data object LoadCounts : ListIntent
    data object ExitSelectionMode : ListIntent
    data object ShareSelectedNote : ListIntent
    data object DeleteSelectedNote : ListIntent
}

sealed interface ListEffect : Effect {
    data class NavigateToDetails(val noteId: Long) : ListEffect
    data object NavigateToCreateNote : ListEffect
    data class ShowDeleteConfirmation(val noteId: Long, val noteTitle: String) : ListEffect
    data class ShowError(val message: String) : ListEffect
    data object NoteDeleted : ListEffect
    data class NoteStarToggled(val noteId: Long, val isStarred: Boolean) : ListEffect
    data class ShareNote(val note: Note) : ListEffect
}