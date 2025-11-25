package com.example.appnotes

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.appnotes.data.AppContainer
import com.example.appnotes.data.AppDataContainer

class NotesApplication : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "reminder_channel_id"
        const val NOTIFICATION_CHANNEL_NAME = "Recordatorios de Notas"
    }
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        container = AppDataContainer(this)
    }

    private fun createNotificationChannel() {
        // Solo necesario en Android 8.0 (Oreo) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para alarmas de recordatorios"
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
