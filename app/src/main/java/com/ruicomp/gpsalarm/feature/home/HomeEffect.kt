package com.ruicomp.gpsalarm.feature.home

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer
import com.ruicomp.gpsalarm.model.GpsAlarm

@Immutable
sealed class HomeEffect : Reducer.ViewEffect {
    data class NavigateToDetail(val id: Int) : HomeEffect()
    data class ShowSnackbar(val message: String) : HomeEffect()
    data class NavigateToScreen(val screen: String) : HomeEffect()
    data class ShowToats(val msg: String): HomeEffect()
}