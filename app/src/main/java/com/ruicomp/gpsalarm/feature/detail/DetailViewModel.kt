package com.ruicomp.gpsalarm.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ruicomp.gpsalarm.base_mvi.BaseViewModel
import com.ruicomp.gpsalarm.data.GpsAlarmRepoImpl
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gpsAlarmRepo: GpsAlarmRepoImpl
) : BaseViewModel<DetailState, DetailEvent, DetailEffect>(
    initialState = DetailState.initial(),
    reducer = DetailScreenReducer()
) {
    init {
        val id = savedStateHandle.toRoute<NavRoutes.Detail>().id
        val gpsAlarm = gpsAlarmRepo.getGpsAlarmById(id)
        sendEvent(
            DetailEvent.UpdateGpsAlarm(gpsAlarm)
        )
    }

    fun initData(gpsAlarm: GpsAlarm) {
        sendEvent(
            DetailEvent.UpdateGpsAlarm(gpsAlarm)
        )
    }

    fun onNavigateToMaps() {
        state.value.gpsAlarm?.let {
            sendEffect(
                DetailEffect.NavigateToMaps(
                    lat = it.location.x,
                    lng = it.location.y,
                    radius = it.radius.toFloat()
                )
            )
        }
    }
}