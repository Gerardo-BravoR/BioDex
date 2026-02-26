package com.biodex.app.data.mapper

import com.biodex.app.data.local.entity.SightingEntity
import com.biodex.app.domain.model.Sighting

object SightingMapper {

    fun toDomain(entity: SightingEntity): Sighting =
        Sighting(
            id = entity.id,
            speciesName = entity.speciesName,
            notes = entity.notes,
            latitude = entity.latitude,
            longitude = entity.longitude,
            address = entity.address
        )

    fun toEntity(domain: Sighting): SightingEntity =
        SightingEntity(
            id = domain.id,
            speciesName = domain.speciesName,
            notes = domain.notes,
            latitude = domain.latitude,
            longitude = domain.longitude,
            address = domain.address
        )
}