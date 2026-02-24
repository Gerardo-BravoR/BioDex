package com.biodex.app.ui.camera

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biodex.app.core.ImageProcessor
import com.biodex.app.core.SightingFileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val fileManager: SightingFileManager, resolver: ContentResolver) : ViewModel()
{
    private val processor = ImageProcessor(resolver)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _processedUri = MutableStateFlow<Uri?>(null)
    val processedUri: StateFlow<Uri?> = _processedUri

    fun processToMax1000(inputUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bmp = processor.loadResizeToMax1000(inputUri)
                val outFile = fileManager.createNewJpegFile()
                processor.saveJpeg(bmp, outFile)
                _processedUri.value = fileManager.fileToUri(outFile)
            } finally {
                _isLoading.value = false
            }
        }
    }
}