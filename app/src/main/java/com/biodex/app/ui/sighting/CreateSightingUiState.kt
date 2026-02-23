package com.biodex.app.ui.sighting

data class CreateSightingUiState(
    val loading: Boolean = false,
    val speciesName: String = "",
    val notes: String = "",
    val error: String? = null,
    val saved: Boolean = false
)