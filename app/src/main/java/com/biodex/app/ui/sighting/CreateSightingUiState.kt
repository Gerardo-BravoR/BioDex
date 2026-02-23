package com.biodex.app.ui.sighting

import android.net.Uri
data class CreateSightingUiState(
    val loading: Boolean = false,
    val speciesName: String = "",
    val notes: String = "",
    val error: String? = null,
    val saved: Boolean = false,
    val photoUri: Uri? = null
)
{
    val canSubmit: Boolean get() = photoUri != null
}