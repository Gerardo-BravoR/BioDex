package com.biodex.app.data.remote.dto

data class SightingDto(
    val id: String? = null,
    val speciesName: String,
    val notes: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null
)