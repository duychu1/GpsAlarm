package com.ruicomp.gpsalarm.feature.maps

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer
import com.ruicomp.gpsalarm.data.fake.AlarmSettingFakeRepo
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.model.GpsLocation

@Immutable
data class MapsState (
    val isLoading: Boolean,
    val gpsAlarm: GpsAlarm?
) : Reducer.ViewState {
    companion object {
        fun initial(): MapsState {
            return MapsState(
                isLoading = false,
                gpsAlarm = GpsAlarm(
                    id = 1,
                    location = GpsLocation(34.0522, -118.2437), // Los Angeles
                    name = "Evening Alarm",
                    reminder = "Time to wrap up work",
                    isActive = false,
                    radius = 50,
                    activeDays = listOf(1, 3, 5), // Monday, Wednesday, Friday
                    alarmSettings = AlarmSettingFakeRepo.alarmSettingsList.get(1)
                ),
            )
        }
    }
}