package com.biodex.app.data.repository

import com.biodex.app.data.local.dao.SightingDao
import com.biodex.app.data.mapper.SightingMapper
import com.biodex.app.domain.model.Sighting
import com.biodex.app.domain.repository.SightingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SightingRepositoryImpl @Inject constructor(
    private val dao: SightingDao
) : SightingRepository {

    override fun observeSightings(): Flow<List<Sighting>> =
        dao.observeAll().map { list -> list.map(SightingMapper::toDomain) }

    override suspend fun createSighting(sighting: Sighting) {
        dao.insert(SightingMapper.toEntity(sighting))
    }
}