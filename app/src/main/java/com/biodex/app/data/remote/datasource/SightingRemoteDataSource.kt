package com.biodex.app.data.remote.datasource

import com.biodex.app.data.remote.api.BioDexApiService
import com.biodex.app.data.remote.dto.SightingDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SightingRemoteDataSource @Inject constructor(
    private val api: BioDexApiService,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getSightings(): List<SightingDto> =
        withContext(ioDispatcher) {
            api.getSightings()
        }

    suspend fun createSighting(dto: SightingDto): SightingDto =
        withContext(ioDispatcher) {
            api.createSighting(dto)
        }
}