package com.example.appnotes.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.appnotes.MainActivity
import com.example.appnotes.NotesApplication
import com.example.appnotes.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 1. Recuperar datos
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Tienes un recordatorio"
        val noteId = intent.getIntExtra("EXTRA_NOTE_ID", -1)

        Log.d("AlarmReceiver", "¡Alarma recibida! Mensaje: $message")

        val tapResultIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("noteId", noteId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            noteId, // Usamos el ID de la nota como request code para que sea único
            tapResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Crear la notificación
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, NotesApplication.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Recordatorio de Nota")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // 3. Mostrarla
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}