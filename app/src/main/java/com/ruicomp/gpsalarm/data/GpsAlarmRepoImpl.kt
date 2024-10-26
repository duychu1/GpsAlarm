package com.ruicomp.gpsalarm.data

import com.ruicomp.gpsalarm.common.result.Result
import com.ruicomp.gpsalarm.common.result.asResult
import com.ruicomp.gpsalarm.data.fake.GpsAlarmFakeRepo
import com.ruicomp.gpsalarm.model.GpsAlarm
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GpsAlarmRepoImpl {
    fun getAllGpsAlarms(): Flow<Result<List<GpsAlarm>>> = flow {
        emit(Result.Loading)
        delay(1000)
        emit(Result.Success(GpsAlarmFakeRepo.fakeListGpsAlarms()))
    }

    fun getGpsAlarmById(id: Int): GpsAlarm? = GpsAlarmFakeRepo.fakeListGpsAlarms().firstOrNull { it.id == id }

}