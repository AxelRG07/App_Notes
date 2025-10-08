package com.example.appnotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.example.appnotes.ui.theme.AppNotesTheme
import kotlin.math.sin
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.sp

@Composable
fun CreateEditApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                NotesTopBar()
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    InfoTaskCard("Titulo", "Ecribe el titulo de la tarea", 1, true)
                    InfoTaskCard("Descripcion", "Escribe la descripcion  de la tarea", 5, false)
                    ConvertToTaskCard()
                    RemindersCard()
                    AttachmentsCard()
                }
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopBar() {
    TopAppBar(
        title = { Text("Nueva Nota") },
        actions = {
            Button(
                onClick = { /* Guardar nota */ },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Guardar")
            }
        }
    )
}

@Composable
fun InfoTaskCard(text: String, placeholder: String, lines: Int, single: Boolean, modifier: Modifier = Modifier){
    var texto by remember { mutableStateOf("") }
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
                value = texto,
                onValueChange = { texto = it },
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
                singleLine = single
            )
        }
    }
}

@Composable
fun DescriptionCard(){

}

@Composable
fun ConvertToTaskCard() {
    var isTask by remember { mutableStateOf(false) }

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
            Column {
                Text("Tarea", fontWeight = FontWeight.Bold)
                Text("Convertir en tarea", color = Color.Gray, fontSize = 13.sp)
            }
            Switch(checked = isTask, onCheckedChange = { isTask = it })
        }
    }
}

@Composable
fun RemindersCard(){

}

@Composable
fun AttachmentsCard(){

}


@Preview
@Composable
fun CreateEditPreview(){
    AppNotesTheme {
        CreateEditApp()
    }
}