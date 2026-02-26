package com.biodex.app.ui.sighting

import android.net.Uri
data class CreateSightingUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,

    val speciesName: String = "",
    val notes: String = "",

    val photoUri: Uri? = null,

    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,

    val canSubmit: Boolean = false
)
