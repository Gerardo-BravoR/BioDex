package com.biodex.app.domain.model

data class Sighting(
    val id: Long = 0,
    val speciesName: String,
    val notes: String? = null
)