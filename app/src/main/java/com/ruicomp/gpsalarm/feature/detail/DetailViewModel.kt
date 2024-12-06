package com.ruicomp.gpsalarm.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ruicomp.gpsalarm.base_mvi.BaseViewModel
import com.ruicomp.gpsalarm.data.GpsAlarmRepoImpl
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.model.GpsLocation
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
    private var id: Int? = null

    init {
        val (id, lat, lng, radius, addressLine) = savedStateHandle.toRoute<NavRoutes.Detail>()
        if (id == null) {
            val defaultGpsAlarm = gpsAlarmRepo.getDefaultGpsAlarm()
            sendEvent(
                DetailEvent.UpdateGpsAlarm(defaultGpsAlarm.copy(
                    id = -1,
                    radius = radius,
                    location = GpsLocation(
                        x = lat!!,
                        y = lng!!,
                        addressLine = addressLine
                    )
                ))
            )
        } else {
            val gpsAlarm = gpsAlarmRepo.getGpsAlarmById(id)
            sendEvent(
                DetailEvent.UpdateGpsAlarm(gpsAlarm)
            )
        }
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
                    id = it.id,
                    lat = it.location.x,
                    lng = it.location.y,
                    radius = it.radius
                )
            )
        }
    }

    fun onSave(gpsAlarm: GpsAlarm) {
        //save to database

    }
}