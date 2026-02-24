package com.biodex.app.ui.sighting

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.biodex.app.R
import com.biodex.app.core.BaseFragment
import com.biodex.app.core.PhotoStorage
import com.biodex.app.core.collectFlow
import com.biodex.app.databinding.FragmentCreateSightingBinding
import com.biodex.app.ui.camera.CameraFragment
import dagger.hilt.android.AndroidEntryPoint

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

        binding.btnSubmitSighting.setOnClickListener { vm.onSaveClicked() }

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
                findNavController().popBackStack() //limpio y vuelvo a home
            }
        }
    }
}