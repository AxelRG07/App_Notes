package com.example.appnotes.ui.components

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appnotes.util.AudioRecorder
import com.example.appnotes.util.createMediaFile
import com.example.appnotes.util.getUriForFile
import java.io.File

@Composable
fun CameraCaptureButton(onMediaCaptured: (Uri) -> Unit) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) onMediaCaptured(imageUri!!)
        }

    LaunchedEffect(Unit) {
        val file = context.createMediaFile(".jpg")
        imageUri = context.getUriForFile(file)
    }

    Button(onClick = {
        imageUri?.let {
            uri -> takePictureLauncher.launch(uri)
        }

    }) {
        Text("Tomar foto")
    }
}

@Composable
fun VideoCaptureButton(onMediaCaptured: (Uri) -> Unit) {
    val context = LocalContext.current
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    val videoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            if (success && videoUri != null) onMediaCaptured(videoUri!!)
        }

    LaunchedEffect(Unit) {
        val file = context.createMediaFile(".mp4")
        videoUri = context.getUriForFile(file)
    }

    Button(onClick = {
        videoUri?.let { uri ->
            videoLauncher.launch(uri)
        }
    }) {
        Text("Grabar video")
    }
}

@Composable
fun AudioRecorderButton(
    onAudioRecorded: (Uri) -> Unit
) {
    val context = LocalContext.current
    val recorder = remember { AudioRecorder() }

    var audioFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    fun startRecording() {
        val file = context.createMediaFile(".m4a")
        audioFile = file
        recorder.start(file)
        isRecording = true
    }

    // Permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startRecording()
        }
    }

    fun stopRecording() {
        recorder.stop()
        isRecording = false

        audioFile?.let { file ->
            val uri = context.getUriForFile(file)
            onAudioRecorded(uri)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (!isRecording) {
            Button(onClick = {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }) {
                Text("🎤 Iniciar grabación")
            }
        } else {
            Button(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                onClick = { stopRecording() }
            ) {
                Text("⏹ Detener y adjuntar")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Grabando…")
        }
    }
}

@Composable
fun RequestMediaPermissions(onGranted: () -> Unit) {
    val context = LocalContext.current

    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }
        if (granted) onGranted() else {
            Toast.makeText(context, "Permisos requeridos", Toast.LENGTH_LONG).show()
        }
    }

    Button(onClick = { launcher.launch(permissions) }) {
        Text("Permitir cámara y micrófono")
    }
}