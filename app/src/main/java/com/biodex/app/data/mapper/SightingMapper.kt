package com.biodex.app.data.mapper

import com.biodex.app.data.local.entity.SightingEntity
import com.biodex.app.data.remote.dto.SightingDto
import com.biodex.app.domain.model.Sighting

object SightingMapper {

    fun toDomain(entity: SightingEntity): Sighting =
        Sighting(
            localId = entity.localId,
            remoteId = entity.remoteId,
            speciesName = entity.speciesName,
            notes = entity.notes,
            latitude = entity.latitude,
            longitude = entity.longitude,
            address = entity.address,
            isSynced = entity.isSynced
        )

    fun toEntity(domain: Sighting): SightingEntity =
        SightingEntity(
            localId = domain.localId,
            remoteId = domain.remoteId,
            speciesName = domain.speciesName,
            notes = domain.notes,
            latitude = domain.latitude,
            longitude = domain.longitude,
            address = domain.address,
            isSynced = domain.isSynced
        )

    fun dtoToDomain(dto: SightingDto): Sighting =
        Sighting(
            localId = 0L,
            remoteId = dto.id,
            speciesName = dto.speciesName,
            notes = dto.notes,
            latitude = dto.latitude,
            longitude = dto.longitude,
            address = dto.address,
            isSynced = true
        )

    fun domainToDto(domain: Sighting): SightingDto =
        SightingDto(
            id = domain.remoteId,
            speciesName = domain.speciesName,
            notes = domain.notes,
            latitude = domain.latitude,
            longitude = domain.longitude,
            address = domain.address
        )
}