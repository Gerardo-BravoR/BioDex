package com.biodex.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.biodex.app.data.local.dao.SightingDao
import com.biodex.app.data.local.entity.SightingEntity

@Database(
    entities = [SightingEntity::class],
    version = 3
)
abstract class BioDexDatabase : RoomDatabase() {
    abstract fun sightingDao(): SightingDao
}