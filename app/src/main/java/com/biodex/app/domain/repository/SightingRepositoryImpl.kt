package com.biodex.app.data.repository

import com.biodex.app.data.local.dao.SightingDao
import com.biodex.app.data.mapper.SightingMapper
import com.biodex.app.data.remote.datasource.SightingRemoteDataSource
import com.biodex.app.domain.model.Sighting
import com.biodex.app.domain.repository.SightingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class SightingRepositoryImpl @Inject constructor(
    private val dao: SightingDao,
    private val remoteDataSource: SightingRemoteDataSource
) : SightingRepository {

    override fun observeSightings(): Flow<List<Sighting>> =
        dao.observeAll()
            .onStart { syncSightings() }
            .map { list -> list.map(SightingMapper::toDomain) }

    override suspend fun createSighting(sighting: Sighting) {
        try {
            val remoteDto = SightingMapper.domainToDto(sighting)
            val createdRemote = remoteDataSource.createSighting(remoteDto)
            dao.insert(SightingMapper.toEntity(SightingMapper.dtoToDomain(createdRemote)))
        } catch (_: Exception) {
            dao.insert(SightingMapper.toEntity(sighting))
        }
    }

    private suspend fun syncSightings() {
        try {
            val remoteSightings = remoteDataSource.getSightings()
            val entities = remoteSightings
                .map(SightingMapper::dtoToDomain)
                .map(SightingMapper::toEntity)

            dao.insertAll(entities)
        } catch (_: Exception) {
            // Nada por ahora
        }
    }
}