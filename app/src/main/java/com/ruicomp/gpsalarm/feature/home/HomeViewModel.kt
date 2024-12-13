package com.ruicomp.gpsalarm.feature.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.ruicomp.gpsalarm.base_mvi.BaseViewModel
import com.ruicomp.gpsalarm.common.result.Result
import com.ruicomp.gpsalarm.common.result.asResult
import com.ruicomp.gpsalarm.data.repository.GpsAlarmRepository
import com.ruicomp.gpsalarm.feature.service.LocationService
import com.ruicomp.gpsalarm.model.GpsAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gpsAlarmRepo: GpsAlarmRepository
) : BaseViewModel<HomeState, HomeEvent, HomeEffect>(
    initialState = HomeState.initial(),
    reducer = HomeScreenReducer()
) {
    private var deleteAlarm: GpsAlarm? = null
    private var indexDeleteAlarm: Int? = null

    init {
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            gpsAlarmRepo.getAllAlarms().asResult().collect { result ->
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

    fun onAlarmClick(gpsAlarm: GpsAlarm) {
        sendEffect(
            HomeEffect.NavigateToDetail(gpsAlarm.id)
        )
    }

    fun onAlarmActiveChange(context: Context, alarm: GpsAlarm, isActive: Boolean) {
        viewModelScope.launch {
            val newAlarm = alarm.copy(isActive = isActive)
            gpsAlarmRepo.update(newAlarm)
            val serviceIntent = Intent(context, LocationService::class.java)

            if (!isActive) {
                try {
                    context.stopService(serviceIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@launch
            }

            serviceIntent.putExtra("target_location", newAlarm)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }


    fun onDbDeleteAlarm() {
        if (deleteAlarm == null) return
        viewModelScope.launch {
            gpsAlarmRepo.delete(deleteAlarm!!)
            deleteAlarm = null
        }
    }

    fun onClickDeleteAlarm(gpsAlarm: GpsAlarm, index: Int) {
        deleteAlarm = gpsAlarm
        indexDeleteAlarm = index
        sendEventForEffect(
            HomeEvent.DeleteAlarm(gpsAlarm.id)
        )
    }

    fun onClickUndoDelete() {
        if (deleteAlarm == null || indexDeleteAlarm == null) {
            sendEffect(HomeEffect.ShowToats("Fail to undo recent item"))
            return
        }
        sendEvent(
            HomeEvent.UndoDelete(deleteAlarm!!, indexDeleteAlarm!!)
        )
        deleteAlarm == null
        indexDeleteAlarm == null
    }
}