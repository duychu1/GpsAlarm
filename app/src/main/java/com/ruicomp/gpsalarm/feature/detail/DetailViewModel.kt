package com.ruicomp.gpsalarm.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ruicomp.gpsalarm.base_mvi.BaseViewModel
import com.ruicomp.gpsalarm.common.result.Result
import com.ruicomp.gpsalarm.data.GpsAlarmRepoImpl
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<DetailState, DetailEvent, DetailEffect>(
    initialState = DetailState(
        isLoading = false,
        gpsAlarm = savedStateHandle.toRoute<NavRoutes.Detail>().gpsAlarm
    ),
    reducer = DetailScreenReducer()
) {

    fun initData(gpsAlarm: GpsAlarm) {
        sendEvent(
            DetailEvent.UpdateData(gpsAlarm)
        )
    }
}