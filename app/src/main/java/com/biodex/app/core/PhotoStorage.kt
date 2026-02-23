package com.biodex.app.core

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoStorage {

    private fun photosDir(context: Context): File {
        val base = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: context.filesDir // fallback por si algo raro pasa
        return File(base, "BioDex_Photos").apply { mkdirs() }
    }

    fun createNewPhotoFile(context: Context): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(photosDir(context), "BIODEX_$ts.jpg")
    }

    fun fileToContentUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

    fun copyPickedPhotoToAppFolder(context: Context, picked: Uri): Uri {
        val outFile = createNewPhotoFile(context)

        context.contentResolver.openInputStream(picked).use { input ->
            requireNotNull(input) { "No se pudo abrir el URI seleccionado" }
            outFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return fileToContentUri(context, outFile)
    }
}