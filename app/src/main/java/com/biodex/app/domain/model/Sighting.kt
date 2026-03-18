package com.biodex.app.domain.model

data class Sighting(
    val localId: Long = 0,
    val remoteId: String? = null,
    val speciesName: String,
    val notes: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val isSynced: Boolean = true
)