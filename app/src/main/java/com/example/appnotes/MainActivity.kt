package com.example.appnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.appnotes.ui.navigation.NotesNavGraph
import com.example.appnotes.ui.theme.AppNotesTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNotesTheme {
                val windowSizeClass = calculateWindowSizeClass(activity = this)
                NotesNavGraph(windowSizeClass = windowSizeClass)
            }
        }
    }
}
