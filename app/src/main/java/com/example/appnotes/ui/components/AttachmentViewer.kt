package com.example.appnotes.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
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


@Composable
fun AttachmentViewer(attachment: Attachment) {
    when (attachment.type) {
        "image" -> ImageViewer(uri = attachment.uri)
        //"video" -> VideoViewer(uri = attachment.uri)
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

/*@Composable
fun VideoViewer(uri: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(uri))
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            StyledPlayerView(it).apply {
                player = exoPlayer
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    350.dp.roundToPx()
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(8.dp)
    )
}*/

@Composable
fun AudioPlayer(uri: String) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    fun start() {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, Uri.parse(uri))
            prepare()
            start()
        }
        isPlaying = true
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
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
        Button(onClick = {
            if (!isPlaying) start() else stop()
        }) {
            Text(if (!isPlaying) "▶ Reproducir audio" else "⏹ Detener")
        }
    }
}
