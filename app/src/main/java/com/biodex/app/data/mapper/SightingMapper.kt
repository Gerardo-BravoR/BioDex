package com.biodex.app.data.mapper

import com.biodex.app.data.local.entity.SightingEntity
import com.biodex.app.data.remote.dto.SightingDto
import com.biodex.app.domain.model.Sighting

object SightingMapper {

    fun toDomain(entity: SightingEntity): Sighting =
        Sighting(
            id = entity.id,
            speciesName = entity.speciesName,
            notes = entity.notes,
            latitude = entity.latitude,
            longitude = entity.longitude,
            address = entity.address,
            isSynced = entity.isSynced
        )

    fun toEntity(domain: Sighting): SightingEntity =
        SightingEntity(
            id = domain.id,
            speciesName = domain.speciesName,
            notes = domain.notes,
            latitude = domain.latitude,
            longitude = domain.longitude,
            address = domain.address,
            isSynced = domain.isSynced
        )

    fun dtoToDomain(dto: SightingDto): Sighting =
        Sighting(
            id = dto.id?.toLongOrNull() ?: 0L,
            speciesName = dto.speciesName,
            notes = dto.notes,
            latitude = dto.latitude,
            longitude = dto.longitude,
            address = dto.address,
            isSynced = true
        )

    fun domainToDto(domain: Sighting): SightingDto =
        SightingDto(
            id = if (domain.id == 0L) null else domain.id.toString(),
            speciesName = domain.speciesName,
            notes = domain.notes,
            latitude = domain.latitude,
            longitude = domain.longitude,
            address = domain.address
        )
}