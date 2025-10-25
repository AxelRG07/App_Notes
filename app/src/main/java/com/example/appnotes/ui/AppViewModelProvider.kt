package com.example.appnotes.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appnotes.NotesApplication
import com.example.appnotes.ui.home.HomeViewModel

object HomeViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NotesApplication
            val repository = application.container.notesRepository
            HomeViewModel(repository)
        }
    }
}