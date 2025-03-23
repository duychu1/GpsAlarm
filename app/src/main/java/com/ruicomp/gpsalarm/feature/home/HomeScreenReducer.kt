package com.ruicomp.gpsalarm.feature.home

import com.ruicomp.gpsalarm.base_mvi.Reducer

class HomeScreenReducer : Reducer<HomeState, HomeEvent, HomeEffect> {
    override fun reduce(
        previousState: HomeState,
        event: HomeEvent
    ): Pair<HomeState, HomeEffect?> {
        return when (event) {
            is HomeEvent.UpdateLoading -> {
                previousState.copy(isLoading = event.isLoading) to null
            }
            is HomeEvent.UpdateListGpsAlarms -> {
                previousState.copy(gpsAlarms = event.listGpsAlarms) to null
            }
            is HomeEvent.UpdateAlarmActive -> {
                previousState.copy(gpsAlarms = previousState.gpsAlarms.map {
                    if (it.id == event.id) {
                        it.copy(isActive = event.isActive)
                    } else {
                        it
                    }
                }) to null
            }
            is HomeEvent.UpdateLocation -> TODO()
            is HomeEvent.DeleteAlarm -> {
                previousState.copy(
                    gpsAlarms = previousState.gpsAlarms.filterNot { it.id == event.gpsAlarm.id }
                ) to HomeEffect.ShowSnackbar("Item ${event.gpsAlarm.name} deleted!")
            }
            is HomeEvent.UndoDelete -> {
                previousState.copy(
                    gpsAlarms = previousState.gpsAlarms.toMutableList().also {
                        it.add(event.index, event.alarm)
                    }.toList()
                ) to null
            }
        }
    }
}