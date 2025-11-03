package com.example.todoapp.presentation.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    noteId: String?,
    onNavigateBack: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState? = null,
    scope: CoroutineScope? = null
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var deleteDialogTitle by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        when {
            noteId == "new" -> {
                viewModel.handleIntent(DetailsIntent.CreateNewNote)
            }
            noteId != null -> {
                val noteIdLong = noteId.toLongOrNull()
                if (noteIdLong != null) {
                    viewModel.handleIntent(DetailsIntent.LoadNote, noteIdLong)
                }
            }
        }
    }

    LaunchedEffect(viewState) {
        when (val currentState = viewState) {
            is DetailsViewState.Content -> {
                title = currentState.note.title
                content = currentState.note.content
            }
            is DetailsViewState.CreatingNewNote -> {
                title = ""
                content = ""
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            effect?.let { currentEffect ->
                when (currentEffect) {
                    is DetailsEffect.NavigateBack -> {
                        onNavigateBack()
                    }
                    is DetailsEffect.ShowDeleteConfirmation -> {
                        deleteDialogTitle = currentEffect.noteTitle
                        showDeleteDialog = true
                    }
                    is DetailsEffect.ShowDiscardChangesDialog -> {
                        showDiscardDialog = true
                    }
                    is DetailsEffect.ShowError -> {
                        snackbarHostState?.let { hostState ->
                            scope?.launch {
                                hostState.showSnackbar(currentEffect.message)
                            }
                        }
                    }
                    is DetailsEffect.NoteSaved -> {
                        snackbarHostState?.let { hostState ->
                            scope?.launch {
                                hostState.showSnackbar("Note saved successfully")
                            }
                        }
                    }
                    is DetailsEffect.NoteDeleted -> {
                        snackbarHostState?.let { hostState ->
                            scope?.launch {
                                hostState.showSnackbar("Note deleted successfully")
                            }
                        }
                        onNavigateBack()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = title,
                        onValueChange = {
                            title = it
                            viewModel.handleIntent(DetailsIntent.TitleChanged(it))
                        },
                        placeholder = { Text("Note Title") },
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val currentState = viewState
                            when (currentState) {
                                is DetailsViewState.Content -> {
                                    if (currentState.isDirty) {
                                        showDiscardDialog = true
                                    } else {
                                        viewModel.handleIntent(DetailsIntent.NavigateBack)
                                    }
                                }
                                else -> {
                                    viewModel.handleIntent(DetailsIntent.NavigateBack)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (viewState is DetailsViewState.Content || viewState is DetailsViewState.CreatingNewNote) {
                        val currentNote = viewModel.getCurrentNote()
                        IconButton(
                            onClick = { viewModel.handleIntent(DetailsIntent.ToggleStar) }
                        ) {
                            Icon(
                                imageVector = if (currentNote?.isStarred == true)
                                    Icons.Default.Star
                                else
                                    Icons.Default.StarBorder,
                                contentDescription = if (currentNote?.isStarred == true) "Remove star" else "Add star",
                                tint = if (currentNote?.isStarred == true)
                                    MaterialTheme.colorScheme.secondary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.handleIntent(DetailsIntent.SaveNote) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save note",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (noteId != "new") {
                            IconButton(
                                onClick = { viewModel.handleIntent(DetailsIntent.DeleteNote) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete note",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (viewState) {
                is DetailsViewState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is DetailsViewState.Content, is DetailsViewState.CreatingNewNote -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = content,
                            onValueChange = {
                                content = it
                                viewModel.handleIntent(DetailsIntent.ContentChanged(it))
                            },
                            placeholder = { Text("Write your note here...") },
                            modifier = Modifier
                                .fillMaxSize()
                                .heightIn(min = 200.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }

                is DetailsViewState.Error -> {
                    when (val errorState = viewState) {
                        is DetailsViewState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Column {
                                    Text(
                                        text = "Error",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = errorState.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                viewModel.handleIntent(DetailsIntent.CancelDelete)
            },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete \"$deleteDialogTitle\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.handleIntent(DetailsIntent.ConfirmDelete)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.handleIntent(DetailsIntent.CancelDelete)
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard Changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        viewModel.handleIntent(DetailsIntent.DiscardChanges)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDiscardDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}