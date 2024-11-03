package com.ruicomp.gpsalarm.feature.maps

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer
import com.ruicomp.gpsalarm.model.GpsAlarm

@Immutable
sealed class MapsEvent : Reducer.ViewEvent {
    data class UpdateLoading(val isLoading: Boolean) : MapsEvent()
    data class UpdateData(val gpsAlarm: GpsAlarm) : MapsEvent()
    data class UpdateLocation(val location: Pair<Double, Double>) : MapsEvent()
    data class UpdateLocationName(val id: Int, val name: String) : MapsEvent()
    data class UpdateLocationRadius(val id: Int, val radius: Int) : MapsEvent()
    data class UpdateAlarmReminder(val id: Int, val reminder: String) : MapsEvent()
    data class UpdateAlarmDuration(val id: Int, val durationAlarm: Int) : MapsEvent()
    data class UpdateAlarmActiveDays(val id: Int, val activeDays: List<Int>) : MapsEvent()
    data class UpdateAlarmSound(val id: Int, val alarmSound: String) : MapsEvent()
    data class UpdateAlarmActive(val id: Int, val isActive: Boolean) : MapsEvent()
    data class DeleteAlarm(val id: Int) : MapsEvent()
    data class NewAlarm(val alarm: GpsAlarm) : MapsEvent()
    data class SaveAlarm(val alarm: GpsAlarm) : MapsEvent()
}