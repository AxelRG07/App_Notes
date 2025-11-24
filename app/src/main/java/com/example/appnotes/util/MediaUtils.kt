package com.example.appnotes.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.createMediaFile(extension: String): File {
    val dir = File(getExternalFilesDir(null), "attachments")
    if (!dir.exists()) dir.mkdirs()

    return File.createTempFile(
        "media_${System.currentTimeMillis()}",
        extension,
        dir
    )
}

fun Context.getUriForFile(file: File): Uri =
    FileProvider.getUriForFile(
        this,
        "${applicationContext.packageName}.fileprovider",
        file
    )