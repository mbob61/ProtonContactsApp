package com.example.todoapp.presentation.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarResult
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.Intent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.todoapp.domain.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onNavigateToDetails: (Long) -> Unit,
    onNavigateToCreateNote: () -> Unit,
    viewModel: ListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState? = null,
    scope: CoroutineScope? = null
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            effect?.let { currentEffect ->
                when (currentEffect) {
                    is ListEffect.NavigateToDetails -> {
                        onNavigateToDetails(currentEffect.noteId)
                    }
                    is ListEffect.NavigateToCreateNote -> {
                        onNavigateToCreateNote()
                    }
                    is ListEffect.ShowError -> {
                        snackbarHostState?.let { hostState ->
                            scope?.launch {
                                hostState.showSnackbar(currentEffect.message)
                            }
                        }
                    }
                    is ListEffect.NoteDeleted -> {
                        snackbarHostState?.let { hostState ->
                            scope?.launch {
                                hostState.showSnackbar("Note deleted successfully")
                            }
                        }
                    }
                    is ListEffect.NoteStarToggled -> {
                        val message = if (currentEffect.isStarred) "Note starred" else "Star removed"
                        snackbarHostState?.let { hostState ->
                            scope?.launch {
                                hostState.showSnackbar(message)
                            }
                        }
                    }
                    is ListEffect.ShowDeleteConfirmation -> {
                        snackbarHostState?.let { hostState ->
                            scope?.launch {
                                val result = hostState.showSnackbar(
                                    message = "Delete \"${currentEffect.noteTitle}\"?",
                                    actionLabel = "DELETE",
                                    withDismissAction = true
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.confirmDeleteNote(currentEffect.noteId)
                                }
                            }
                        }
                    }
                    is ListEffect.ShareNote -> {
                        val note = currentEffect.note
                        var content = ""
                        if (note.title.isNotBlank()) {
                            content += note.title + "\n\n"
                        }
                        if (note.content.isNotBlank()) {
                            content += note.content
                        }
                        if (content.isBlank()) {
                            content = "Empty note"
                        }

                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, content)
                            putExtra(Intent.EXTRA_SUBJECT, note.title.ifBlank { "Shared Note" })
                        }

                        ContextCompat.startActivity(context,
                            Intent.createChooser(shareIntent, "Share note"),
                            null)

                        viewModel.handleIntent(ListIntent.ExitSelectionMode)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            val currentState = viewState
            when {
                currentState is ListViewState.Content && currentState.isSelectionMode -> {
                    TopAppBar(
                        title = {
                            Text(
                                text = currentState.selectedNote?.getDisplayTitle() ?: "Selected",
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { viewModel.handleIntent(ListIntent.ExitSelectionMode) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Exit selection mode",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { viewModel.handleIntent(ListIntent.ShareSelectedNote) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share note",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            IconButton(
                                onClick = { viewModel.handleIntent(ListIntent.DeleteSelectedNote) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete note",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                else -> {
                    TopAppBar(
                        title = {
                            Text(
                                "ToDo App",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        actions = {
                            if (currentState is ListViewState.Content && currentState.totalCount > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    if (currentState.starredCount > 0) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${currentState.starredCount}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(ListIntent.CreateNewNote) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create new note"
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (viewState) {
                is ListViewState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ListViewState.Content -> {
                    when (val contentState = viewState) {
                        is ListViewState.Content -> {
                            val notes = contentState.notes.collectAsLazyPagingItems()

                            if (contentState.isEmpty && notes.itemCount == 0) {
                                EmptyState(onCreateNew = { viewModel.handleIntent(ListIntent.CreateNewNote) })
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(1),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        count = notes.itemCount,
                                        key = notes.itemKey { it.id }
                                    ) { index ->
                                        val note = notes[index]
                                        note?.let { currentNote ->
                                            val isSelected = when (val state = viewState) {
                                                is ListViewState.Content -> state.isSelectionMode && state.selectedNote?.id == currentNote.id
                                                else -> false
                                            }
                                            NoteCard(
                                                note = currentNote,
                                                onNoteClick = { viewModel.handleIntent(ListIntent.NoteClicked(currentNote)) },
                                                onNoteLongPress = { viewModel.handleIntent(ListIntent.NoteLongPressed(currentNote)) },
                                                onStarToggle = { viewModel.handleIntent(ListIntent.ToggleStar(currentNote.id, !currentNote.isStarred)) },
                                                onDelete = { viewModel.handleIntent(ListIntent.DeleteNote(currentNote.id)) },
                                                isSelected = isSelected
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }

                is ListViewState.Error -> {
                    when (val errorState = viewState) {
                        is ListViewState.Error -> {
                            ErrorState(
                                message = errorState.message,
                                onRetry = { viewModel.handleIntent(ListIntent.LoadNotes) }
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(
    note: Note,
    onNoteClick: () -> Unit,
    onNoteLongPress: () -> Unit,
    onStarToggle: () -> Unit,
    onDelete: () -> Unit,
    isSelected: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .combinedClickable(
                onClick = onNoteClick,
                onLongClick = onNoteLongPress,
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                note.isStarred -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = when {
                isSelected -> 6.dp
                note.isStarred -> 4.dp
                else -> 2.dp
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if (note.title.isNotBlank()) {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (note.content.isNotBlank()) {
                        Text(
                            text = note.getContentPreview(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = onStarToggle,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (note.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (note.isStarred) "Remove star" else "Add star",
                            tint = if (note.isStarred) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.getFormattedDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun EmptyState(onCreateNew: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No notes yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first note to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCreateNew,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Note")
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Retry")
        }
    }
}