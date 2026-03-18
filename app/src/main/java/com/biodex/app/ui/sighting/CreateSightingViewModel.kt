package com.biodex.app.ui.sighting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biodex.app.domain.model.Sighting
import com.biodex.app.domain.usecase.ValidateSightingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.net.Uri
import com.biodex.app.domain.repository.SightingRepository

@HiltViewModel
class CreateSightingViewModel @Inject constructor(private val validateSightingUseCase: ValidateSightingUseCase, private val repo: SightingRepository) : ViewModel(){

    private val _uiState = MutableStateFlow(CreateSightingUiState())
    val uiState: StateFlow<CreateSightingUiState> = _uiState.asStateFlow()

    fun setError(msg: String) {
        _uiState.update { it.copy(error = msg, loading = false) }
    }

    fun onSpeciesNameChanged(value: String) {
        _uiState.update { it.copy(speciesName = value, error = null, saved = false) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value, error = null, saved = false) }
    }

    fun onPhotoReady(uri: Uri) {
        _uiState.update { current ->
            val updated = current.copy(photoUri = uri)
            updated.copy(canSubmit = computeCanSubmit(updated))
        }
    }

    fun onDeletePhoto() {
        _uiState.update { current ->
            val updated = current.copy(photoUri = null)
            updated.copy(canSubmit = computeCanSubmit(updated))
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }

    fun onLocationReady(lat: Double, lon: Double, address: String?) {
        _uiState.update { current ->
            val updated = current.copy(
                latitude = lat,
                longitude = lon,
                address = address
            )
            updated.copy(canSubmit = computeCanSubmit(updated))
        }
    }

    fun onSaveClicked() {
        val s = _uiState.value

        val sighting = Sighting(
            speciesName = s.speciesName,
            notes = s.notes.ifBlank { null },
            latitude = s.latitude,
            longitude = s.longitude,
            address = s.address
        )

        val validation = validateSightingUseCase.execute(sighting)
        if (!validation.ok) {
            setError(validation.errorMessage ?: "Error de validación")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, saved = false) }

            repo.createSighting(sighting)

            _uiState.update { it.copy(loading = false, saved = true) }
        }
    }

    private fun computeCanSubmit(s: CreateSightingUiState): Boolean {
        val hasPhoto = s.photoUri != null
        val hasCoords = s.latitude != null && s.longitude != null
        return hasPhoto && hasCoords
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }
}