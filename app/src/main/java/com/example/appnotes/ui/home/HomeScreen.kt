package com.example.appnotes.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appnotes.R
import com.example.appnotes.data.NoteWithDetails
import com.example.appnotes.ui.HomeViewModelProvider
import com.example.appnotes.ui.navigation.NoteDetailDestination
import com.example.appnotes.ui.navigation.NoteEditDestination
import com.example.appnotes.ui.navigation.NoteEntryDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelProvider.Factory)
) {
    val notes by viewModel.noteUiState.collectAsState()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedFilter by remember { mutableStateOf("All") }

    Scaffold(
        topBar = { NotesTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NoteEntryDestination.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar nota")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onValueChange = { searchQuery = it }
            )
            FilterBar(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
            NotesList(
                notes = notes.filter {
                    val matchesSearch = it.note.title.contains(searchQuery.text, true) ||
                            it.note.description.contains(searchQuery.text, true)
                    val matchesFilter = when (selectedFilter) {
                        "Notes" -> !it.note.isTask
                        "Tasks" -> it.note.isTask
                        else -> true
                    }
                    matchesSearch && matchesFilter
                },
                onClickNote = { noteId ->
                    navController.navigate("${NoteDetailDestination.route}/$noteId")
                }
            )
        }
    }
}

@Composable
fun NotesTopBar() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
        }

    }
}

@Composable
fun SearchBar(
    searchQuery: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    TextField(
        value = searchQuery,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(R.string.placeholder_buscar)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun FilterBar(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            listOf("All", "Notes", "Tasks").forEach { filter ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (filter == selectedFilter) MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            )
                            else Color.Transparent
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable { onFilterSelected(filter) }
                ) {
                    val icon = when (filter) {
                        "Notes" -> R.drawable.ic_notes_note
                        "Tasks" -> R.drawable.ic_notes_task
                        else -> R.drawable.ic_notes_all
                    }
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(filter, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun NotesList(
    notes: List<NoteWithDetails>,
    onClickNote: (Int) -> Unit
) {
    if (notes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.notas_vacias))
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(notes) { noteWithDetails ->
                TaskCard(noteWithDetails, onClickNote)
            }
        }
    }
}

@Composable
fun TaskCard(noteWithDetails: NoteWithDetails, onClickNote: (Int) -> Unit) {
    val note = noteWithDetails.note
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickNote(note.id) },
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(note.description, style = MaterialTheme.typography.bodyMedium)
            if (note.isTask && note.dueDateTime != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("🕓 ${sdf.format(Date(note.dueDateTime))}", fontSize = 13.sp)
            }
        }
    }
}
