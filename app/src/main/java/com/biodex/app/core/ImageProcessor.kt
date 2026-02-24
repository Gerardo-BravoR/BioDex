package com.biodex.app.core

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class ImageProcessor(private val resolver: ContentResolver) {

    suspend fun loadResizeToMax1000(uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        // 1) bounds
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "No se pudo abrir input stream" }
            BitmapFactory.decodeStream(input, null, bounds)
        }

        val srcW = bounds.outWidth
        val srcH = bounds.outHeight
        require(srcW > 0 && srcH > 0) { "Imagen inválida" }

        // 2) sample para gama baja
        val maxSize = 1000
        val sampleSize = calculateInSampleSize(srcW, srcH, maxSize)

        val opts = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.RGB_565
        }

        val decoded = resolver.openInputStream(uri).use { input ->
            requireNotNull(input)
            BitmapFactory.decodeStream(input, null, opts)
        } ?: error("No se pudo decodificar")

        // 3) escala final exacta
        scaleToMax(decoded, maxSize)
    }

    suspend fun saveJpeg(bitmap: Bitmap, outFile: java.io.File, quality: Int = 85) =
        withContext(Dispatchers.IO) {
            outFile.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
        }

    private fun calculateInSampleSize(w: Int, h: Int, max: Int): Int {
        val maxDim = maxOf(w, h)
        var s = 1
        while (maxDim / s > max * 1.5) s *= 2
        return s
    }

    private fun scaleToMax(src: Bitmap, max: Int): Bitmap {
        val w = src.width
        val h = src.height
        val maxDim = maxOf(w, h)
        if (maxDim <= max) return src

        val ratio = max.toFloat() / maxDim.toFloat()
        val nw = (w * ratio).roundToInt()
        val nh = (h * ratio).roundToInt()

        return Bitmap.createScaledBitmap(src, nw, nh, true).also {
            if (it != src) src.recycle()
        }
    }
}