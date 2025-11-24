package com.example.appnotes.util

import android.media.MediaRecorder
import androidx.compose.ui.platform.LocalContext
import java.io.File

class AudioRecorder {
    private var recorder: MediaRecorder? = null

    fun start(file: File) {
        if (recorder != null) return

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
    }

    fun stop() {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null
    }
}