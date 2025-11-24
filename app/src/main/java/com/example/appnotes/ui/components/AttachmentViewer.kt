package com.example.appnotes.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appnotes.data.Attachment
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView


@Composable
fun AttachmentViewer(attachment: Attachment) {
    when (attachment.type) {
        "image" -> ImageViewer(uri = attachment.uri)
        "video" -> VideoViewer(uri = attachment.uri)
        "audio" -> AudioPlayer(uri = attachment.uri)
    }
}

@Composable
fun ImageViewer(uri: String) {
    AsyncImage(
        model = uri,
        contentDescription = "Imagen adjunta",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
            .background(Color.LightGray)
    )
}

@Composable
fun VideoViewer(uri: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(uri))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                useController = true
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(8.dp)
    )
}

@Composable
fun AudioPlayer(uri: String) {
    val context = LocalContext.current
    // Usamos 'remember' para mantener la instancia, pero mutable para poder anularla
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) } // Opcional: para mostrar carga

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
        isPlaying = false
        isLoading = false
    }

    fun start() {
        // Si ya hay uno reproduciendo, lo limpiamos primero
        stop()

        isLoading = true

        val mp = MediaPlayer()
        try {
            mp.setDataSource(context, Uri.parse(uri))

            // 1. Configurar qué pasa cuando termine de cargar (Asíncrono)
            mp.setOnPreparedListener { player ->
                player.start()
                isLoading = false
                isPlaying = true
                mediaPlayer = player // Guardamos la referencia en el estado
            }

            // 2. Configurar qué pasa cuando termine el audio
            mp.setOnCompletionListener {
                stop() // Reseteamos el botón cuando acaba la canción
            }

            // 3. ¡LA CLAVE! Preparar en segundo plano para no congelar la app
            mp.prepareAsync()

        } catch (e: Exception) {
            // Si la URI es inválida o el archivo no existe
            e.printStackTrace()
            isLoading = false
            stop()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            stop()
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                if (!isPlaying) start() else stop()
            },
            // Deshabilitamos el botón mientras carga para evitar clicks dobles
            enabled = !isLoading
        ) {
            Text(
                when {
                    isLoading -> "⌛ Cargando..."
                    isPlaying -> "⏹ Detener"
                    else -> "▶ Reproducir audio"
                }
            )
        }
    }
}
