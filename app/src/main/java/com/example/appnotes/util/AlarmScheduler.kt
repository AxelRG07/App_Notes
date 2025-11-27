package com.example.appnotes.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.appnotes.data.Note
import com.example.appnotes.data.Reminder
import com.example.appnotes.receivers.AlarmReceiver
import java.time.Instant
import java.time.ZoneId

interface AlarmScheduler {
    fun schedule(reminder: Reminder, noteTitle: String)
    fun cancel(reminder: Reminder)
}

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(reminder: Reminder, noteTitle: String) {
        val dueTime = reminder.remindAt

        Log.d("AlarmScheduler", "--> Intentando programar alarma ID ${reminder.id}. Fecha: $dueTime vs Ahora: ${System.currentTimeMillis()}")

        if (dueTime <= System.currentTimeMillis()) {
            Log.w("AlarmScheduler", "La fecha ya pasó, no se puede programar alarma")
            return
        }

        // creamos el Intent que irá al Receiver
        // Usamos el ID de la nota para que sea único para cancelarlo/actualizarlo después
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_MESSAGE", noteTitle) // Pasamos el título como mensaje
            putExtra("EXTRA_NOTE_ID", reminder.noteId)
        }

        //convertir el Intent en PendingIntent
        // FLAG_UPDATE_CURRENT: Si ya existe una alarma para esta nota, actualiza los datos
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("AlarmScheduler", "Programando alarma para nota ${reminder.id} a las $dueTime")

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dueTime,
            pendingIntent
        )
    }

    override fun cancel(reminder: Reminder) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}