package com.example.appnotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.example.appnotes.ui.theme.AppNotesTheme
import kotlin.math.sin

@Composable
fun CreateEditApp(){
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold (
            topBar = {
                NotesTopBar()
            },
            content = {
                Column (
                    modifier = Modifier
                        .padding(it)
                ) {
                    InfoTaskCard("Titulo", "Escribe el titulo de la tarea", 1, true)
                    InfoTaskCard("Descripcion", "Escribe La descripcion de la tarea", 5, false)
                }
            }
        )
    }
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
fun ConvertToTaskCard(){

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