package com.ruicomp.gpsalarm.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GpsAlarmDao {
    @Insert
    suspend fun insert(alarm: GpsAlarmEntities)

    @Update
    suspend fun update(alarm: GpsAlarmEntities)

    @Delete
    suspend fun delete(alarm: GpsAlarmEntities)

    @Query("SELECT * FROM gps_alarms")
    fun getAllAlarms(): Flow<List<GpsAlarmEntities>>

    @Query("SELECT * FROM gps_alarms WHERE id = :id")
    fun getAlarmById(id: Int): Flow<GpsAlarmEntities>
}