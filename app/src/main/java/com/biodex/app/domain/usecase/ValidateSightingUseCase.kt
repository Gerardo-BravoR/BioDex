package com.biodex.app.domain.usecase

import com.biodex.app.domain.model.Sighting

data class ValidationResult(
    val ok: Boolean,
    val errorMessage: String? = null
)

class ValidateSightingUseCase {

    fun execute(sighting: Sighting): ValidationResult {
        if (sighting.speciesName.isBlank()) {
            return ValidationResult(
                ok = false,
                errorMessage = "El nombre de la especie no puede estar vacío"
            )
        }
        return ValidationResult(ok = true)
    }
}