package com.ruicomp.gpsalarm.feature.home

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer
import com.ruicomp.gpsalarm.model.GpsAlarm

@Immutable
sealed class HomeEvent : Reducer.ViewEvent {
    data class UpdateLoading(val isLoading: Boolean) : HomeEvent()
    data class UpdateListGpsAlarms(val listGpsAlarms: List<GpsAlarm>) : HomeEvent()
    data class UpdateLocation(val location: Pair<Double, Double>) : HomeEvent()
    data class UpdateAlarmActive(val id: Int, val isActive: Boolean) : HomeEvent()
    data class DeleteAlarm(val id: Int) : HomeEvent()
    data class UndoDelete(val alarm: GpsAlarm, val index: Int) : HomeEvent()

}