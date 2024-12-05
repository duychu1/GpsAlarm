package com.ruicomp.gpsalarm.feature.detail

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer
import com.ruicomp.gpsalarm.data.fake.GpsAlarmFakeRepo
import com.ruicomp.gpsalarm.model.GpsAlarm

@Immutable
data class DetailState (
    val isLoading: Boolean,
    val isActive: Boolean,
    val isRepeating: Boolean,
    val gpsAlarm: GpsAlarm?
) : Reducer.ViewState {
    companion object {
        fun initial(): DetailState {
            return DetailState(
                isLoading = true,
                isActive = true,
                isRepeating = false,
                gpsAlarm = null
            )
        }
    }
}