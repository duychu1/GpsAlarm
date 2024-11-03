package com.ruicomp.gpsalarm.feature.maps

import com.ruicomp.gpsalarm.base_mvi.Reducer

class MapsScreenReducer : Reducer<MapsState, MapsEvent, MapsEffect> {
    override fun reduce(
        previousState: MapsState,
        event: MapsEvent
    ): Pair<MapsState, MapsEffect?> {
        return when (event) {
            is MapsEvent.UpdateLoading -> {
                previousState.copy(isLoading = event.isLoading) to null
            }
            is MapsEvent.UpdateData -> {
                previousState.copy(isLoading = false, gpsAlarm = event.gpsAlarm) to null
            }
            is MapsEvent.UpdateLocation -> TODO()
            is MapsEvent.DeleteAlarm -> {
                previousState.copy(
                    gpsAlarm = null
                ) to null
            }

            is MapsEvent.NewAlarm -> TODO()
            is MapsEvent.SaveAlarm -> TODO()
            is MapsEvent.UpdateAlarmActive -> TODO()
            is MapsEvent.UpdateAlarmActiveDays -> TODO()
            is MapsEvent.UpdateAlarmDuration -> TODO()
            is MapsEvent.UpdateAlarmReminder -> TODO()
            is MapsEvent.UpdateAlarmSound -> TODO()
            is MapsEvent.UpdateLocationName -> TODO()
            is MapsEvent.UpdateLocationRadius -> TODO()
        }
    }
}