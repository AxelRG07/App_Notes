package com.example.appnotes.ui.note

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appnotes.data.NoteWithDetails
import com.example.appnotes.ui.NoteDetailsViewModelProvider
import com.example.appnotes.ui.navigation.HomeDestination
import com.example.appnotes.ui.navigation.NavigationDestination
import com.example.appnotes.ui.navigation.NoteEditDestination
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Int,
    navController: NavController,
    viewModel: NoteDetailsViewModel = viewModel(factory = NoteDetailsViewModelProvider.Factory)
) {
    val noteWithDetails by viewModel.noteUiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { NoteEditDestination.titleRes },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { noteWithDetails?.note?.let {
                        navController.navigate("${NoteEditDestination.route}/${it.id}")
                    } }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        },
        floatingActionButton = {
            noteWithDetails?.let {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.toggleCompleted() },
                    text = { Text(if (it.note.isCompleted) "Marcar pendiente" else "Marcar completada") },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        noteWithDetails?.let {
            NoteDetailContent(
                note = it,
                modifier = Modifier.padding(innerPadding)
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar esta nota? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteNote {
                                navController.navigateUp()
                            }
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun NoteDetailContent(
    note: NoteWithDetails,
    modifier: Modifier = Modifier
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = note.note.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = note.note.description,
                style = MaterialTheme.typography.bodyLarge
            )
            if (note.note.isTask && note.note.dueDateTime != null) {
                Text(
                    text = "📅 Fecha límite: ${sdf.format(Date(note.note.dueDateTime))}",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            if (note.note.isCompleted) {
                Text(
                    text = "✅ Completada",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (note.reminders.isNotEmpty()) {
            item { Text("⏰ Recordatorios:", style = MaterialTheme.typography.titleMedium) }
            items(note.reminders) { reminder ->
                Text("- ${sdf.format(Date(reminder.remindAt))}")
            }
        }

        if (note.attachments.isNotEmpty()) {
            item { Text("📎 Archivos adjuntos:", style = MaterialTheme.typography.titleMedium) }
            items(note.attachments) { att ->
                Text("- ${att.type.uppercase()}: ${att.caption ?: att.uri}")
            }
        }
    }
}
