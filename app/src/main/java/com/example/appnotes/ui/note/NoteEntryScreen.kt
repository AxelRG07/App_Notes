package com.example.appnotes.ui.note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appnotes.ui.NoteEntryViewModelProvider
import java.util.Calendar

@Composable
fun NoteEntryScreen (
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit = navigateBack,
    noteId: Int? = null,
    viewModel: NoteEntryViewModel = viewModel(factory = NoteEntryViewModelProvider.Factory)
)
{
    val noteUiState by viewModel.noteUiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(
        noteId
    ) {
        if (noteId != null) viewModel.loadNote(noteId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                NotesTopBar(noteId, onNavigateUp)
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text( "Guardar" ) },
                    icon = { Icon(Icons.Default.Check, contentDescription = "Guardar") },
                    onClick = {
                        if (viewModel.isValidNote()) {
                            viewModel.saveNote()
                            navigateBack()
                        }
                    }
                )
            }
        ) { innerPadding ->
            NoteEntryForm(
                noteUiState,
                onValueChange = viewModel::updateUiState,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopBar(
    noteId: Int? = null,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = { Text( if(noteId == null) "Nueva nota" else "Editar nota")  },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        },
        modifier = Modifier
            .statusBarsPadding()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEntryForm (
    noteUiState: NoteUiState,
    onValueChange: (NoteUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TitleCard(
            noteUiState.title,
            onValueChange,
            noteUiState,
            "Titulo",
            "Escribe el título de la nota",
            lines = 1,
            single = true,
            modifier = Modifier
        )

        DescriptionCard(
            noteUiState.description,
            onValueChange,
            noteUiState,
            "Descripción",
            "Escribe la descripción de la nota",
            lines = 5,
            single = false,
        )

        ConvertToTaskCard(noteUiState, onValueChange)

        if (noteUiState.isTask) {
            val date = remember { mutableStateOf("") }
            val time = remember { mutableStateOf("") }

            Row (
                modifier = Modifier.fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        val datePicker = DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                calendar.set(year, month, day)
                                date.value = "$day/${month + 1}/$year"
                                onValueChange(noteUiState.copy(dueDateTime = calendar.timeInMillis))
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        datePicker.show()
                    }
                ) {
                    Text(if (date.value.isEmpty()) "Seleccionar fecha" else "Fecha: ${date.value}")
                }

                Button(
                    onClick = {
                        val timePicker = TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                time.value = "%02d:%02d".format(hour, minute)
                                onValueChange(noteUiState.copy(dueDateTime = calendar.timeInMillis))
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )
                        timePicker.show()
                    }
                ) {
                    Text(if (time.value.isEmpty()) "Seleccionar hora" else "Hora: ${time.value}")
                }
            }
        }
    }
}

@Composable
fun TitleCard(
    value: String,
    onValueChange: (NoteUiState) -> Unit,
    noteUiState: NoteUiState,
    text: String,
    placeholder: String,
    lines: Int,
    single: Boolean,
    modifier: Modifier = Modifier
){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {

        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(16.dp, 16.dp, 0.dp).fillMaxWidth()
            )
            OutlinedTextField(
                value = value,
                onValueChange = { onValueChange(noteUiState.copy(title = it)) },
                placeholder = { Text(placeholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                maxLines = lines,
                singleLine = single,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )
        }
    }
}

@Composable
fun DescriptionCard(
    value: String,
    onValueChange: (NoteUiState) -> Unit,
    noteUiState: NoteUiState,
    text: String,
    placeholder: String,
    lines: Int,
    single: Boolean,
    modifier: Modifier = Modifier
){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {

        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(16.dp, 16.dp, 0.dp).fillMaxWidth()
            )
            OutlinedTextField(
                value = value,
                onValueChange = { onValueChange(noteUiState.copy(description = it)) },
                placeholder = { Text(placeholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                maxLines = lines,
                singleLine = single,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )
        }
    }
}

@Composable
fun ConvertToTaskCard(
    noteUiState: NoteUiState,
    onValueChange: (NoteUiState) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Marcar como tarea", fontWeight = FontWeight.Bold)

            Checkbox(
                checked = noteUiState.isTask,
                onCheckedChange = {
                    onValueChange(noteUiState.copy(isTask = it))
                }
            )
        }
    }
}

@Composable
fun RemindersCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recordatorios", fontWeight = FontWeight.Bold)
                Text(
                    "+ Agregar recordatorio",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {  }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("No se han añadido recordatorios", color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Composable
fun AttachmentsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Archivos adjuntos", fontWeight = FontWeight.Bold)
                Text(
                    "+ Agregar archivo",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("No hay archivos adjuntos añadidos", color = Color.Gray, fontSize = 13.sp)
        }
    }
}

//@Preview
//@Composable
//fun CreateEditPreview(){
//    AppNotesTheme {
//
//    }
//}