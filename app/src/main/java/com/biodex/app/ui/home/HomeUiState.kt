package com.biodex.app.ui.home

import com.biodex.app.domain.model.Sighting

data class HomeUiState(
    val loading: Boolean = true,
    val items: List<Sighting> = emptyList(),
    val error: String? = null
)