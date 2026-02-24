package com.biodex.app.ui.sighting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biodex.app.domain.model.Sighting
import com.biodex.app.domain.usecase.ValidateSightingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.net.Uri
import kotlinx.coroutines.flow.update

@HiltViewModel
class CreateSightingViewModel @Inject constructor(private val validateSightingUseCase: ValidateSightingUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateSightingUiState())
    val uiState: StateFlow<CreateSightingUiState> = _uiState.asStateFlow()

    fun onSpeciesNameChanged(value: String) {
        _uiState.update { it.copy(speciesName = value, error = null, saved = false) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value, error = null, saved = false) }
    }

    fun onPhotoReady(uri: Uri) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun onDeletePhoto() {
        _uiState.update { it.copy(photoUri = null) }
    }

    fun onSaveClicked() {
        val sighting = Sighting(
            speciesName = _uiState.value.speciesName,
            notes = _uiState.value.notes.ifBlank { null }
        )

        val validation = validateSightingUseCase.execute(sighting)
        if (!validation.ok) {
            _uiState.update { it.copy(error = validation.errorMessage, loading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }

            // Simulación de guardado
            delay(400)

            _uiState.update { it.copy(loading = false, saved = true) }
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }
}