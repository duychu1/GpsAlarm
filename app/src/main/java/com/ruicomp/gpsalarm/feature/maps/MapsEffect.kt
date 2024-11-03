package com.ruicomp.gpsalarm.feature.maps

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer

@Immutable
sealed class MapsEffect : Reducer.ViewEffect {
    data class NavigateToScreen(val screen: String) : MapsEffect()
    data class ShowToast(val msg: String): MapsEffect()
}