package com.biodex.app.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

    private val vm: CameraViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val granted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) startCamera() else requestPermission.launch(Manifest.permission.CAMERA)

        binding.btnCapture.setOnClickListener { takePhoto() }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.isLoading.collect { loading ->
                        binding.progressIndicator.visibility = if (loading) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    vm.processedUri.collect { uri ->
                        if (uri != null) {
                            setFragmentResult(RESULT_KEY, Bundle().apply { putParcelable(URI_KEY, uri) })
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
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
        val capture = imageCapture
        if (capture == null) {
            Log.e("CameraFragment", "imageCapture == null (camara no inicializada)")
            Toast.makeText(requireContext(), "Cámara no lista aún", Toast.LENGTH_SHORT).show()
            return
        }

        val file: File = PhotoStorage.createNewPhotoFile(requireContext())
        val output = ImageCapture.OutputFileOptions.Builder(file).build()

        Log.d("CameraFragment", "Intentando tomar foto... ${file.absolutePath}")

        capture.takePicture(
            output,
            mainExecutor(),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    Log.d("CameraFragment", "Foto guardada OK")
                    val uri: Uri = PhotoStorage.fileToContentUri(requireContext(), file)
                    vm.processToMax1000(uri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "Error takePicture: ${exception.message}", exception)
                    Toast.makeText(
                        requireContext(),
                        "Error cámara: ${exception.imageCaptureError} - ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    private fun mainExecutor(): Executor = ContextCompat.getMainExecutor(requireContext())
}