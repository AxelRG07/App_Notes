package com.example.appnotes.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.appnotes.NotesApplication
import com.example.appnotes.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 1. Recuperar datos
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Tienes un recordatorio"

        Log.d("AlarmReceiver", "¡Alarma recibida! Mensaje: $message")

        // 2. Crear la notificación
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, NotesApplication.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Asegúrate de tener un icono válido aquí
            .setContentTitle("Recordatorio de Nota")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // 3. Mostrarla
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}