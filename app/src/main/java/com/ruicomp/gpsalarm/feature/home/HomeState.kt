package com.ruicomp.gpsalarm.feature.home

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer
import com.ruicomp.gpsalarm.data.fake.GpsAlarmFakeRepo
import com.ruicomp.gpsalarm.model.GpsAlarm

@Immutable
data class HomeState (
    val isLoading: Boolean,
    val gpsAlarms: List<GpsAlarm>
) : Reducer.ViewState {
    companion object {
        fun initial(): HomeState {
            return HomeState(
                isLoading = true,
                gpsAlarms = emptyList()
            )
        }
    }
}