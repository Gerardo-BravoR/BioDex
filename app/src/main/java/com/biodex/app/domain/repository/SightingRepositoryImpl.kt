package com.biodex.app.data.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.biodex.app.data.local.dao.SightingDao
import com.biodex.app.data.mapper.SightingMapper
import com.biodex.app.data.remote.datasource.SightingRemoteDataSource
import com.biodex.app.domain.model.Sighting
import com.biodex.app.domain.repository.SightingRepository
import com.biodex.app.work.SightingSyncWorker.SightingSyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class SightingRepositoryImpl @Inject constructor(
    private val dao: SightingDao,
    private val remoteDataSource: SightingRemoteDataSource,
    private val workManager: WorkManager
) : SightingRepository {

    override fun observeSightings(): Flow<List<Sighting>> =
        dao.observeAll()
            .onStart { syncSightings() }
            .map { list -> list.map(SightingMapper::toDomain) }

    override suspend fun createSighting(sighting: Sighting) {
        try {
            val remoteDto = SightingMapper.domainToDto(sighting)
            val createdRemote = remoteDataSource.createSighting(remoteDto)

            dao.insert(
                SightingMapper.toEntity(
                    SightingMapper.dtoToDomain(createdRemote)
                )
            )
        } catch (_: Exception) {
            dao.insert(
                SightingMapper.toEntity(
                    sighting.copy(
                        localId = 0L,
                        remoteId = null,
                        isSynced = false
                    )
                )
            )
            enqueueSyncWork()
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

    private fun enqueueSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SightingSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "sighting_sync",
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    override suspend fun syncPendingSightings() {
        val pending = dao.getPendingSyncSightings()

        pending.forEach { entity ->
            try {
                val dto = SightingMapper.domainToDto(SightingMapper.toDomain(entity))
                val createdRemote = remoteDataSource.createSighting(dto)

                dao.markAsSynced(
                    localId = entity.localId,
                    remoteId = createdRemote.id ?: ""
                )
            } catch (_: Exception) {
                // seguimos con el resto; no detenemos todo por uno
            }
        }
    }
}