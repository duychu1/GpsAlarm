package com.ruicomp.gpsalarm.data.repository

import com.ruicomp.gpsalarm.data.fake.GpsAlarmFakeRepo
import com.ruicomp.gpsalarm.database.GpsAlarmDao
import com.ruicomp.gpsalarm.model.GpsAlarm
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GpsAlarmRepositoryImpl @Inject constructor(
    private val gpsAlarmDao: GpsAlarmDao
): GpsAlarmRepository {
    override fun getAllAlarms(): Flow<List<GpsAlarm>> {
        return gpsAlarmDao.getAllAlarms().map { entities ->
            entities.map { it.asModel() }
        }
    }

    override fun getAlarmById(id: Int): Flow<GpsAlarm> {
        return gpsAlarmDao.getAlarmById(id).map { it.asModel() }
    }

    override suspend fun insert(alarm: GpsAlarm) {
        if (alarm.id == -1) {
            gpsAlarmDao.insert(alarm.copy(id = 0).asEntities())
        } else {
            gpsAlarmDao.insert(alarm.asEntities())
        }
    }

    override suspend fun update(alarm: GpsAlarm) {
        gpsAlarmDao.update(alarm.asEntities())
    }

    override suspend fun updateIsActiveById(id: Int, isActive: Boolean) {
        gpsAlarmDao.updateIsActiveById(id, isActive)
    }

    override suspend fun delete(alarm: GpsAlarm) {
        gpsAlarmDao.delete(alarm.asEntities())
    }

    override fun getDefaultGpsAlarm(): GpsAlarm = GpsAlarmFakeRepo.fakeListGpsAlarms().first()
}