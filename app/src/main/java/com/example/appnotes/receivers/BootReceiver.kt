package com.example.appnotes.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.appnotes.NotesApplication
import com.example.appnotes.util.AndroidAlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Dispositivo reiniciado. Reprogramando alarmas...")

            val pendingResult = goAsync()

            val application = context.applicationContext as NotesApplication
            val repository = application.container.notesRepository
            val alarmScheduler = AndroidAlarmScheduler(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val notes = repository.getAllNotes().first()

                    val currentTime = System.currentTimeMillis()

                    notes.forEach { n ->
                        if (n.note.isTask &&
                            n.note.dueDateTime != null &&
                            n.note.dueDateTime > currentTime &&
                            !n.note.isCompleted
                        ) {
                            alarmScheduler.schedule(n.note)
                            Log.d("BootReceiver", "Alarma reprogramada: ${n.note.title}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error al reprogramar alarmas", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}