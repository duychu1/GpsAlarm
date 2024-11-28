package com.ruicomp.gpsalarm.feature.detail

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.model.GpsLocation

@Immutable
sealed class DetailEvent : Reducer.ViewEvent {
    data class UpdateLoading(val isLoading: Boolean) : DetailEvent()
    data class UpdateGpsAlarm(val gpsAlarm: GpsAlarm?) : DetailEvent()
    data class UpdateLocation(val location: Pair<Double, Double>) : DetailEvent()
    data class UpdateFromMaps(val location: GpsLocation, val radius: Int) : DetailEvent()
    data class UpdateLocationName(val id: Int, val name: String) : DetailEvent()
    data class UpdateLocationRadius(val id: Int, val radius: Int) : DetailEvent()
    data class UpdateAlarmReminder(val id: Int, val reminder: String) : DetailEvent()
    data class UpdateAlarmDuration(val id: Int, val durationAlarm: Int) : DetailEvent()
    data class UpdateAlarmActiveDays(val id: Int, val activeDays: List<Int>) : DetailEvent()
    data class UpdateAlarmSound(val id: Int, val alarmSound: String) : DetailEvent()
    data class UpdateAlarmActive(val id: Int, val isActive: Boolean) : DetailEvent()
    data class DeleteAlarm(val id: Int) : DetailEvent()
    data class NewAlarm(val alarm: GpsAlarm) : DetailEvent()
    data class SaveAlarm(val alarm: GpsAlarm) : DetailEvent()
}