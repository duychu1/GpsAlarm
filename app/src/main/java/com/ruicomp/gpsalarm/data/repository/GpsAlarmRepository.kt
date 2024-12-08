package com.ruicomp.gpsalarm.data.repository

import com.ruicomp.gpsalarm.model.GpsAlarm
import kotlinx.coroutines.flow.Flow

interface GpsAlarmRepository {
    fun getAllAlarms(): Flow<List<GpsAlarm>>
    fun getAlarmById(id: Int): Flow<GpsAlarm>

    suspend fun insert(alarm: GpsAlarm)
    suspend fun update(alarm: GpsAlarm)
    suspend fun delete(alarm: GpsAlarm)

    fun getDefaultGpsAlarm(): GpsAlarm
}