package com.biodex.app.work.SightingSyncWorker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.biodex.app.data.local.dao.SightingDao
import com.biodex.app.data.mapper.SightingMapper
import com.biodex.app.data.remote.datasource.SightingRemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SightingSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: SightingDao,
    private val remoteDataSource: SightingRemoteDataSource
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val pending = dao.getPendingSyncSightings()

            pending.forEach { entity ->
                val dto = SightingMapper.domainToDto(SightingMapper.toDomain(entity))
                val createdRemote = remoteDataSource.createSighting(dto)

                dao.markAsSynced(
                    localId = entity.localId,
                    remoteId = createdRemote.id ?: ""
                )
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}