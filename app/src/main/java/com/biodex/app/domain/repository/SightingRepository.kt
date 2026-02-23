package com.biodex.app.domain.repository

import com.biodex.app.domain.model.Sighting
import kotlinx.coroutines.flow.Flow

interface SightingRepository {
    fun observeSightings(): Flow<List<Sighting>>
    suspend fun createSighting(sighting: Sighting)
}