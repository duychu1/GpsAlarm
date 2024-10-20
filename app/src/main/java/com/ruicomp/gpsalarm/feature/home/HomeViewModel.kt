package com.ruicomp.gpsalarm.feature.home

import androidx.lifecycle.viewModelScope
import com.ruicomp.gpsalarm.base_mvi.BaseViewModel
import com.ruicomp.gpsalarm.common.result.Result
import com.ruicomp.gpsalarm.data.GpsAlarmRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gpsAlarmRepo: GpsAlarmRepoImpl
) : BaseViewModel<HomeState, HomeEvent, HomeEffect>(
    initialState = HomeState.initial(),
    reducer = HomeScreenReducer()
) {
    fun getData() {
        viewModelScope.launch {
            gpsAlarmRepo.getAllGpsAlarms().collect { result ->
                sendEvent(
                    event = HomeEvent.UpdateLoading(
                        isLoading = result is Result.Loading
                    )
                )

                when (result) {
                    is Result.Loading -> Unit
                    is Result.Error -> {
                        sendEffect(HomeEffect.ShowToats("Error when fetch data"))
                    }
                    is Result.Success -> {
                        sendEvent(
                            HomeEvent.UpdateListGpsAlarms(result.data)
                        )
                    }
                }
            }
        }
    }
}