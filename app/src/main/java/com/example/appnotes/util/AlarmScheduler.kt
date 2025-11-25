package com.example.appnotes.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.appnotes.data.Note
import com.example.appnotes.receivers.AlarmReceiver
import java.time.Instant
import java.time.ZoneId

interface AlarmScheduler {
    fun schedule(note: Note)
    fun cancel(note: Note)
}

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(note: Note) {
        // 1. Validamos que haya una fecha futura
        val dueTime = note.dueDateTime ?: return
        if (dueTime <= System.currentTimeMillis()) {
            Log.w("AlarmScheduler", "La fecha ya pasó, no se puede programar alarma")
            return
        }

        // 2. Creamos el Intent que irá al Receiver
        // Usamos el ID de la nota para que sea único y podamos cancelarlo/actualizarlo después
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_MESSAGE", note.title) // Pasamos el título como mensaje
            putExtra("EXTRA_NOTE_ID", note.id)
        }

        // 3. Convertimos el Intent en PendingIntent
        // FLAG_UPDATE_CURRENT: Si ya existe una alarma para esta nota, actualiza los datos
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id, // ID único por nota
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("AlarmScheduler", "Programando alarma para nota ${note.id} a las $dueTime")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // permisos
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dueTime,
            pendingIntent
        )
    }

    override fun cancel(note: Note) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}