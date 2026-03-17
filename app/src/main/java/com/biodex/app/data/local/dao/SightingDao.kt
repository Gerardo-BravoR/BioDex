package com.biodex.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.biodex.app.data.local.entity.SightingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SightingDao {

    @Query("SELECT * FROM sightings ORDER BY id DESC")
    fun observeAll(): Flow<List<SightingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SightingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SightingEntity>)
}