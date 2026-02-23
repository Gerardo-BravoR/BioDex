package com.biodex.app.domain.repository

import com.biodex.app.domain.model.Sighting

interface SightingRepository {
    suspend fun createSighting(sighting: Sighting)
}