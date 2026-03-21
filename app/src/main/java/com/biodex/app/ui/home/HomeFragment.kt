package com.biodex.app.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.biodex.app.R
import com.biodex.app.core.BaseFragment
import com.biodex.app.core.collectFlow
import com.biodex.app.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val vm: HomeViewModel by viewModels()
    private val adapter = SightingAdapter { sighting ->
        showSightingDetail(sighting)
    }

    private fun showSightingDetail(sighting: com.biodex.app.domain.model.Sighting) {
        val detail = buildString {
            appendLine("Especie: ${sighting.speciesName}")
            appendLine()
            appendLine("Notas: ${sighting.notes ?: "Sin notas"}")
            appendLine()
            appendLine("Dirección: ${sighting.address ?: "Sin dirección"}")
            appendLine()
            appendLine("Latitud: ${sighting.latitude ?: "N/D"}")
            appendLine("Longitud: ${sighting.longitude ?: "N/D"}")
            appendLine()
            appendLine(
                "Estado de sincronización: " +
                        if (sighting.isSynced) "Sincronizado" else "Pendiente"
            )
        }

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Detalle del avistamiento")
            .setMessage(detail)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSightings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSightings.adapter = adapter

        binding.btnSyncPending.setOnClickListener {
            vm.syncPendingSightings()
        }

        binding.btnCreateSighting.setOnClickListener {
            findNavController().navigate(R.id.createSightingFragment)
        }

        collectFlow(vm.uiState) { state ->
            binding.tvHomeError.isGone = state.error == null
            binding.tvHomeError.text = state.error.orEmpty()
            adapter.submitList(state.items)
        }
    }
}