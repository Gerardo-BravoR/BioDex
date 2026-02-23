package com.biodex.app.ui.sighting

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.biodex.app.core.BaseFragment
import com.biodex.app.core.collectFlow
import com.biodex.app.databinding.FragmentCreateSightingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateSightingFragment :
    BaseFragment<FragmentCreateSightingBinding>(FragmentCreateSightingBinding::inflate) {

    private val vm: CreateSightingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etSpeciesName.doAfterTextChanged { vm.onSpeciesNameChanged(it?.toString().orEmpty()) }
        binding.etNotes.doAfterTextChanged { vm.onNotesChanged(it?.toString().orEmpty()) }

        binding.btnSave.setOnClickListener { vm.onSaveClicked() }

        collectFlow(vm.uiState) { state ->
            binding.btnSave.isEnabled = !state.loading
            binding.tvError.isGone = state.error == null
            binding.tvError.text = state.error.orEmpty()

            if (state.saved) {
                binding.tvError.isGone = false
                binding.tvError.text = "✅ Guardado (simulado)"
            }
        }
    }
}