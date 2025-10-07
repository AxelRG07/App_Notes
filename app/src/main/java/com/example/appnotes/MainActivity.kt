package com.example.appnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appnotes.ui.theme.AppNotesTheme
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNotesTheme {
                AppNotes()
            }
        }
    }
}

@Composable
fun AppNotes() {
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
                    SearchBar()
                    FilterBar()
                    TaskCard()
                }
            }
        )
    }
}

@Composable
fun NotesTopBar(modifier: Modifier = Modifier){
    Row (
        horizontalArrangement = SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_notes_menu),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))


            Text(
                text = "My Notes",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_notes_conf),
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
        )
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    var texto by remember { mutableStateOf("") }

    TextField(
        value = texto,
        onValueChange = { texto = it }, // etiqueta que se mueve arriba al escribir
        placeholder = { Text("\uD83D\uDD0D  Escribe el título de la nota...") }, // texto guía dentro del campo
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
    )
}

@Composable
fun TaskCard(modifier: Modifier = Modifier) {
    Card (
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        border = BorderStroke(1.dp, Color.LightGray),
    ) {
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row (
                horizontalArrangement = SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Título de la nota",
                    style = MaterialTheme.typography.displayMedium
                )
                Icon(
                    painter = painterResource(R.drawable.ic_notes_conf),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                )
            }

            Text(
                text = "Contenido de la nota",
                style = MaterialTheme.typography.bodyLarge,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            )

            Row (
                modifier = Modifier
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notes_calendar),
                        contentDescription = null,
                        modifier = Modifier
                        .size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "12/6/2024",
                        style = MaterialTheme.typography.bodyLarge
                    )

                }
                Spacer(modifier = Modifier.width(16.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notes_clock),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "12/7/2024",
                        style = MaterialTheme.typography.bodyLarge
                    )

                }
            }
        }
    }
}

@Composable
fun ElementRow(id: Int,  texto: String, modifier: Modifier = Modifier){
    Row (
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            painter = painterResource(id),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge
        )

    }
}

@Composable
fun FilterBar(modifier: Modifier = Modifier){
    Box (
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp,)
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.LightGray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            ElementRow(R.drawable.ic_notes_all, "All")
            ElementRow(R.drawable.ic_notes_note, "Notes")
            ElementRow(R.drawable.ic_notes_task, "Tasks")

        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppNotesTheme {
        AppNotes()
    }
}