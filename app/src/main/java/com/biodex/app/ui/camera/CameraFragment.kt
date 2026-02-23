package com.biodex.app.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.biodex.app.core.BaseFragment
import com.biodex.app.core.PhotoStorage
import com.biodex.app.databinding.FragmentCameraBinding
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.Executor

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::inflate) {

    companion object {
        const val RESULT_KEY = "camera_result"
        const val URI_KEY = "photo_uri"
    }

    private var imageCapture: ImageCapture? = null

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else findNavController().popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val granted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) startCamera() else requestPermission.launch(Manifest.permission.CAMERA)

        binding.btnCapture.setOnClickListener { takePhoto() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val selector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                selector,
                preview,
                imageCapture
            )
        }, mainExecutor())
    }

    private fun takePhoto() {
        val capture = imageCapture ?: return

        // Persistencia DIRECTA: archivo final en BioDex_Photos
        val file: File = PhotoStorage.createNewPhotoFile(requireContext())
        val output = ImageCapture.OutputFileOptions.Builder(file).build()

        capture.takePicture(
            output,
            mainExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    val uri: Uri = PhotoStorage.fileToContentUri(requireContext(), file)

                    setFragmentResult(RESULT_KEY, Bundle().apply {
                        putParcelable(URI_KEY, uri)
                    })
                    findNavController().popBackStack()
                }

                override fun onError(exception: ImageCaptureException) {
                }
            }
        )
    }

    private fun mainExecutor(): Executor = ContextCompat.getMainExecutor(requireContext())
}