package com.biodex.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.biodex.app.data.local.entity.SightingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SightingDao {

    @Query("SELECT * FROM sightings ORDER BY localId DESC")
    fun observeAll(): Flow<List<SightingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SightingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SightingEntity>)

    @Query("SELECT * FROM sightings WHERE isSynced = 0 ORDER BY localId ASC")
    suspend fun getPendingSyncSightings(): List<SightingEntity>

    @Query("UPDATE sightings SET isSynced = 1, remoteId = :remoteId WHERE localId = :localId")
    suspend fun markAsSynced(localId: Long, remoteId: String)

    @Query("SELECT * FROM sightings WHERE remoteId = :remoteId LIMIT 1")
    suspend fun findByRemoteId(remoteId: String): SightingEntity?
}