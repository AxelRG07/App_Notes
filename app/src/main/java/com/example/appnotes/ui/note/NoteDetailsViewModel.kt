package com.example.appnotes.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnotes.data.Attachment
import com.example.appnotes.data.NoteWithDetails
import com.example.appnotes.data.NotesRepository
import com.example.appnotes.util.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteDetailsViewModel (
    private val notesRepository: NotesRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    private val _noteUiState = MutableStateFlow<NoteWithDetails?>(null)
    val noteUiState: StateFlow<NoteWithDetails?> = _noteUiState.asStateFlow()

    fun loadNote(noteId: Int) {
            viewModelScope.launch {
                notesRepository.getNote(noteId).collect { result ->
                    _noteUiState.value = result
                }
            }
    }

    fun deleteNote(onDeleted: () -> Unit) {
        _noteUiState.value?.note?.let { note ->
            viewModelScope.launch {
                notesRepository.deleteNote(note)
                _noteUiState.value = null
                onDeleted()
            }
        }
    }

    fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch {
            notesRepository.deleteAttachment(attachment)
        }
    }


    fun toggleCompleted() {
        // Obtenemos el estado actual completo (Nota + Recordatorios)
        val currentDetails = _noteUiState.value ?: return
        val note = currentDetails.note
        val reminders = currentDetails.reminders

        // Invertimos el valor actual
        val newCompletedStatus = !note.isCompleted
        val updatedNote = note.copy(isCompleted = newCompletedStatus)

        viewModelScope.launch {
            // 1. Si estamos marcando como COMPLETADA (true), cancelamos las alarmas pendientes
            if (newCompletedStatus) {
                reminders.forEach { reminder ->
                    viewModelScope.launch {
                        notesRepository.deleteReminder(reminder)
                        alarmScheduler.cancel(reminder)
                    }
                }
            }

            // Actualizamos la nota en la BD
            notesRepository.updateNote(updatedNote)
        }

        _noteUiState.value = currentDetails.copy(note = updatedNote)
    }

}