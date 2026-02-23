package com.biodex.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sightings")
data class SightingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val speciesName: String,
    val notes: String?
)