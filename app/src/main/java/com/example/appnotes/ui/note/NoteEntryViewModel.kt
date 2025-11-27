package com.example.appnotes.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnotes.data.Attachment
import com.example.appnotes.data.Note
import com.example.appnotes.data.NotesRepository
import com.example.appnotes.data.Reminder
import com.example.appnotes.util.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteEntryViewModel(
    private val notesRepository: NotesRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    private val _noteUiState = MutableStateFlow(NoteUiState())
    val noteUiState: StateFlow<NoteUiState> = _noteUiState.asStateFlow()

    // Estado separado para la lista de archivos adjuntos (imágenes, videos, audios)
    private val _attachments = MutableStateFlow<List<Attachment>>(emptyList())
    val attachments: StateFlow<List<Attachment>> = _attachments.asStateFlow()

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private var isEditMode = false
    private var isDataLoaded = false
    fun loadNote(noteId: Int) {

        if (isDataLoaded) return

        viewModelScope.launch {
            notesRepository.getNote(noteId).collect { noteWithDetails ->
                noteWithDetails?.let { safeNoteWithDetais ->
                    val note = noteWithDetails.note
                    _noteUiState.value = NoteUiState(
                        id = note.id,
                        title = note.title,
                        description = note.description,
                        isTask = note.isTask,
                        dueDateTime = note.dueDateTime,
                        completed = note.isCompleted,
                        createdAt = note.createdAt
                    )
                    _attachments.value = noteWithDetails.attachments

                    _reminders.value = noteWithDetails.reminders

                    isEditMode = true
                    isDataLoaded = true
                }
            }
        }
    }

    fun updateUiState(newState: NoteUiState) {
        _noteUiState.value = newState
    }

    fun updateAttachments(newAttachments: List<Attachment>) {
        _attachments.value = newAttachments
    }

    fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch {
            if (attachment.id != 0) {
                notesRepository.deleteAttachment(attachment)
            }

            val currentList = _attachments.value.toMutableList()
            currentList.remove(attachment)
            _attachments.value = currentList
        }
    }

    fun addReminder(timestamp: Long) {
        val newReminder = Reminder(
            noteId = 0,
            remindAt = timestamp,
            status = "PENDING"
        )
        _reminders.value += newReminder
    }

    // Eliminar recordatorio
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            // Si ya tiene ID, existe en la BD: hay que borrarlo y cancelar la alarma
            if (reminder.id != 0) {
                notesRepository.deleteReminder(reminder)
                alarmScheduler.cancel(reminder)
            }
            // Lo quitamos de la lista visual
            val currentList = _reminders.value.toMutableList()
            currentList.remove(reminder)
            _reminders.value = currentList
        }
    }

    fun saveNote() {
        viewModelScope.launch {
            val note = Note(
                id = noteUiState.value.id,
                title = noteUiState.value.title,
                description = noteUiState.value.description,
                isTask = noteUiState.value.isTask,
                dueDateTime = noteUiState.value.dueDateTime,
                isCompleted = noteUiState.value.completed,
                createdAt = noteUiState.value.createdAt
            )
            val noteId = if (isEditMode) {
                notesRepository.updateNote(note)
                note.id.toLong()
            } else {
                notesRepository.insertNote(note)
            }

            _attachments.value.forEach { att ->
                if (att.id == 0) {
                    notesRepository.addAttachment(att.copy(noteId = noteId.toInt()))
                }
            }

            _reminders.value.forEach { reminder ->
                if (reminder.id == 0) {
                    val newReminderId = notesRepository.addReminder(
                        reminder.copy(noteId = noteId.toInt())
                    )

                    note.isCompleted = false
                    notesRepository.updateNote(note)


                    // b) Programar la alarma usando el ID generado
                    val savedReminder = reminder.copy(
                        id = newReminderId.toInt(),
                        noteId = noteId.toInt()
                    )

                    alarmScheduler.schedule(savedReminder, note.title)
                }
            }
        }
    }

    fun isValidNote(): Boolean {
        val note = noteUiState.value
        return note.title.isNotBlank() && note.description.isNotBlank()
    }
}

data class NoteUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val isTask: Boolean = false,
    val dueDateTime: Long? = null,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)