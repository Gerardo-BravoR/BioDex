package com.biodex.app.data.remote.api

import com.biodex.app.data.remote.dto.SightingDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BioDexApiService {

    @GET("sightings")
    suspend fun getSightings(): List<SightingDto>

    @POST("sightings")
    suspend fun createSighting(@Body sighting: SightingDto): SightingDto
}