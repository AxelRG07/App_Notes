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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fullscreen
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.min
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.request.ImageRequest


@Composable
fun AttachmentViewer(attachment: Attachment, onDelete: () -> Unit) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        when (attachment.type) {
            "image" -> ImageViewer(uri = attachment.uri, onDelete = onDelete)
            "video" -> VideoViewer(uri = attachment.uri, onDelete = onDelete)
            "audio" -> AudioPlayer(uri = attachment.uri, onDelete = onDelete)
        }
    }
}

@Composable
fun DeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(4.dp)
            .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
            .size(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Eliminar archivo",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun FullScreenButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(4.dp)
            .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
            .size(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Fullscreen,
            contentDescription = "Pantalla completa",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun FullScreenMediaDialog(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                content()

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ImageViewer(uri: String, onDelete: () -> Unit) {
    var showFullScreen by remember { mutableStateOf(false) }

    if (showFullScreen) {
        FullScreenMediaDialog(onDismiss = { showFullScreen = false }) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen completa",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Box (modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = uri,
            contentDescription = "Imagen adjunta",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable { showFullScreen = true }
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        DeleteButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

@Composable
fun VideoViewer(uri: String, onDelete: () -> Unit) {
    val context = LocalContext.current
    var showFullScreen by remember { mutableStateOf(false) }

    if (showFullScreen) {
        FullScreenMediaDialog(onDismiss = { showFullScreen = false }) {
            val fullScreenPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
                    prepare()
                    playWhenReady = true
                }
            }

            DisposableEffect(Unit) {
                onDispose { fullScreenPlayer.release() }
            }

            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = fullScreenPlayer
                        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
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

    Box (modifier = Modifier.fillMaxWidth().height(250.dp).background(Color.Black).padding(16.dp)) {
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

        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        ) {
            FullScreenButton(onClick = { showFullScreen = true })
            Spacer(modifier = Modifier.width(8.dp))
            DeleteButton(onClick = onDelete)
        }
    }
}

@Composable
fun AudioPlayer(uri: String, onDelete: () -> Unit) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

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
        stop()

        isLoading = true

        val mp = MediaPlayer()
        try {
            mp.setDataSource(context, Uri.parse(uri))

            mp.setOnPreparedListener { player ->
                player.start()
                isLoading = false
                isPlaying = true
                mediaPlayer = player // Guardamos la referencia en el estado
            }

            // 2. Configurar qué pasa cuando termine el audio
            mp.setOnCompletionListener {
                stop()
            }

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

        DeleteButton(
            onClick = onDelete,
            modifier = Modifier
        )
    }
}
