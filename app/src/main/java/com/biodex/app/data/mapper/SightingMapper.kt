package com.biodex.app.data.mapper

import com.biodex.app.data.local.entity.SightingEntity
import com.biodex.app.domain.model.Sighting

object SightingMapper {

    fun toDomain(entity: SightingEntity): Sighting =
        Sighting(
            id = entity.id,
            speciesName = entity.speciesName,
            notes = entity.notes
        )

    fun toEntity(domain: Sighting): SightingEntity =
        SightingEntity(
            id = domain.id,
            speciesName = domain.speciesName,
            notes = domain.notes
        )
}