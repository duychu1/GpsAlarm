package com.ruicomp.gpsalarm.feature.detail

import com.ruicomp.gpsalarm.base_mvi.Reducer

class DetailScreenReducer : Reducer<DetailState, DetailEvent, DetailEffect> {
    override fun reduce(
        previousState: DetailState,
        event: DetailEvent
    ): Pair<DetailState, DetailEffect?> {
        return when (event) {
            is DetailEvent.UpdateLoading -> {
                previousState.copy(isLoading = event.isLoading) to null
            }
            is DetailEvent.UpdateData -> {
                previousState.copy(isLoading = false, gpsAlarm = event.gpsAlarm) to null
            }
            is DetailEvent.UpdateLocation -> TODO()
            is DetailEvent.DeleteAlarm -> {
                previousState.copy(
                    gpsAlarm = null
                ) to null
            }

            is DetailEvent.NewAlarm -> TODO()
            is DetailEvent.SaveAlarm -> TODO()
            is DetailEvent.UpdateAlarmActive -> TODO()
            is DetailEvent.UpdateAlarmActiveDays -> TODO()
            is DetailEvent.UpdateAlarmDuration -> TODO()
            is DetailEvent.UpdateAlarmReminder -> TODO()
            is DetailEvent.UpdateAlarmSound -> TODO()
            is DetailEvent.UpdateLocationName -> TODO()
            is DetailEvent.UpdateLocationRadius -> TODO()
            is DetailEvent.UpdateFromMaps -> {
                previousState.copy(
                    gpsAlarm = previousState.gpsAlarm?.copy(location = event.location, radius = event.radius)
                ) to null
            }
        }
    }
}