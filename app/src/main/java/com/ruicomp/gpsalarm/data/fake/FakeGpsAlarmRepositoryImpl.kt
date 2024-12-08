package com.ruicomp.gpsalarm.data.fake

import com.ruicomp.gpsalarm.data.repository.GpsAlarmRepository
import com.ruicomp.gpsalarm.database.GpsAlarmDao
import com.ruicomp.gpsalarm.model.GpsAlarm
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGpsAlarmRepositoryImpl @Inject constructor(
    private val gpsAlarmDao: GpsAlarmDao
): GpsAlarmRepository {
    override fun getAllAlarms(): Flow<List<GpsAlarm>> = flowOf(GpsAlarmFakeRepo.fakeListGpsAlarms())

    override fun getAlarmById(id: Int): Flow<GpsAlarm> = flowOf(GpsAlarmFakeRepo.fakeListGpsAlarms().get(id))

    override suspend fun insert(alarm: GpsAlarm) {
        gpsAlarmDao.insert(alarm.asEntities())
    }

    override suspend fun update(alarm: GpsAlarm) {
        gpsAlarmDao.update(alarm.asEntities())
    }

    override suspend fun delete(alarm: GpsAlarm) {
        gpsAlarmDao.delete(alarm.asEntities())
    }

    override fun getDefaultGpsAlarm(): GpsAlarm = GpsAlarmFakeRepo.fakeListGpsAlarms().first()
}