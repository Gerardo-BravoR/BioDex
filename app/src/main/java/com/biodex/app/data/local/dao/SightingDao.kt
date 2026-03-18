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

    @Query("SELECT * FROM sightings WHERE isSynced = 0 ORDER BY id ASC")
    suspend fun getPendingSyncSightings(): List<SightingEntity>

    @Query("UPDATE sightings SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SightingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SightingEntity>)
}