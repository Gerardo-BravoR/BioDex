package com.biodex.app.ui.sighting

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.biodex.app.R
import com.biodex.app.core.BaseFragment
import com.biodex.app.core.PhotoStorage
import com.biodex.app.core.collectFlow
import com.biodex.app.databinding.FragmentCreateSightingBinding
import com.biodex.app.ui.camera.CameraFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CreateSightingFragment :
    BaseFragment<FragmentCreateSightingBinding>(FragmentCreateSightingBinding::inflate) {

    private val vm: CreateSightingViewModel by viewModels()

    private val pickPhoto = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val persisted = PhotoStorage.copyPickedPhotoToAppFolder(requireContext(), uri)
            vm.onPhotoReady(persisted)
        }
    }

    private val requestLocationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val fine = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarse = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fine || coarse) fetchLocationAndAddress()
            else vm.setError("Se requiere ubicación (COARSE o FINE) para guardar el avistamiento.")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            CameraFragment.RESULT_KEY,
            this
        ) { _, bundle ->
            val uri = bundle.getParcelable<Uri>(CameraFragment.URI_KEY)
            if (uri != null) vm.onPhotoReady(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etSpeciesName.doAfterTextChanged { vm.onSpeciesNameChanged(it?.toString().orEmpty()) }
        binding.etNotes.doAfterTextChanged { vm.onNotesChanged(it?.toString().orEmpty()) }

        binding.btnPickFromGallery.setOnClickListener {
            pickPhoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnTakePhoto.setOnClickListener {
            findNavController().navigate(R.id.cameraFragment)
        }

        binding.btnDeletePhoto.setOnClickListener {
            vm.onDeletePhoto()
        }

        binding.btnSubmitSighting.setOnClickListener {
            vm.onSaveClicked()
        }

        ensureLocationPermissionsAndFetch()

        collectFlow(vm.uiState) { state ->
            binding.btnSubmitSighting.isEnabled = state.canSubmit && !state.loading
            binding.tvError.isGone = state.error == null
            binding.tvError.text = state.error.orEmpty()

            val hasPhoto = state.photoUri != null
            binding.btnDeletePhoto.visibility = if (hasPhoto) View.VISIBLE else View.GONE
            if (hasPhoto) binding.imgPhoto.setImageURI(state.photoUri)
            else binding.imgPhoto.setImageDrawable(null)

            if (state.saved) {
                Toast.makeText(requireContext(), "Avistamiento guardado", Toast.LENGTH_SHORT).show()
                vm.consumeSaved()
                findNavController().popBackStack()
            }

            val hasCoords = state.latitude != null && state.longitude != null

            binding.tvLocationStatus.isGone = hasCoords || state.error != null
            binding.tvLocationStatus.text = "Obteniendo ubicación…"

            if (hasCoords) {
                binding.tvAddress.isGone = state.address.isNullOrBlank()
                binding.tvAddress.text = state.address.orEmpty()

                binding.tvLatLon.isGone = false
                binding.tvLatLon.text = "Lat: ${state.latitude}, Lon: ${state.longitude}"
            } else {
                binding.tvAddress.isGone = true
                binding.tvLatLon.isGone = true
            }
        }
    }

    private fun ensureLocationPermissionsAndFetch() {
        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            fetchLocationAndAddress()
        } else {
            requestLocationPermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    @androidx.annotation.RequiresPermission(
        anyOf = [
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    private fun fetchLocationAndAddress() {
        val client = LocationServices.getFusedLocationProviderClient(requireContext())

        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (location == null) {
                vm.setError("No pude obtener ubicación. Activa GPS e inténtalo otra vez.")
                return@addOnSuccessListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val address = reverseGeocode(location.latitude, location.longitude)
                vm.onLocationReady(location.latitude, location.longitude, address)
            }
        }.addOnFailureListener {
            vm.setError("Error obteniendo ubicación: ${it.message}")
        }
    }

    private suspend fun reverseGeocode(lat: Double, lon: Double): String? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val geocoder = Geocoder(requireContext())

                if (Build.VERSION.SDK_INT >= 33) {
                    var result: String? = null
                    val lock = Object()

                    geocoder.getFromLocation(lat, lon, 1) { list ->
                        result = list.firstOrNull()?.getAddressLine(0)
                        synchronized(lock) { lock.notify() }
                    }

                    synchronized(lock) { lock.wait(1500) } // timeout suave
                    result
                } else {
                    val list = geocoder.getFromLocation(lat, lon, 1)
                    list?.firstOrNull()?.getAddressLine(0)
                }
            }.getOrNull()
        }
    }
}