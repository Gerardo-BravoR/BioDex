package com.biodex.app.core

import android.content.Context
import android.net.Uri
import java.io.File

class SightingFileManager(private val context: Context) {

    fun createNewJpegFile(): File = PhotoStorage.createNewPhotoFile(context)

    fun fileToUri(file: File): Uri = PhotoStorage.fileToContentUri(context, file)

    fun openInput(uri: Uri) = context.contentResolver.openInputStream(uri)

    fun openOutput(file: File) = file.outputStream()
}