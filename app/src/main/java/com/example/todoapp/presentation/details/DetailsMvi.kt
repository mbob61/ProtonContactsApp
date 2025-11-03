package com.example.todoapp.presentation.details

import com.example.todoapp.domain.mvi.Effect
import com.example.todoapp.domain.mvi.Intent
import com.example.todoapp.domain.mvi.ViewState
import com.example.todoapp.domain.model.Note

sealed interface DetailsViewState : ViewState {
    data object Loading : DetailsViewState
    data class Content(
        val note: Note,
        val isDirty: Boolean = false,
        val showDeleteConfirmation: Boolean = false,
        val navigateBack: Boolean = false
    ) : DetailsViewState
    data class Error(val message: String) : DetailsViewState
    data object CreatingNewNote : DetailsViewState
}

sealed interface DetailsIntent : Intent {
    data object LoadNote : DetailsIntent
    data object CreateNewNote : DetailsIntent
    data class TitleChanged(val title: String) : DetailsIntent
    data class ContentChanged(val content: String) : DetailsIntent
    data object SaveNote : DetailsIntent
    data object ToggleStar : DetailsIntent
    data object DeleteNote : DetailsIntent
    data object ConfirmDelete : DetailsIntent
    data object CancelDelete : DetailsIntent
    data object NavigateBack : DetailsIntent
    data object DiscardChanges : DetailsIntent
}

sealed interface DetailsEffect : Effect {
    data object NavigateBack : DetailsEffect
    data class ShowError(val message: String) : DetailsEffect
    data class ShowDeleteConfirmation(val noteTitle: String) : DetailsEffect
    data class NoteSaved(val note: Note) : DetailsEffect
    data object NoteDeleted : DetailsEffect
    data object ShowDiscardChangesDialog : DetailsEffect
}