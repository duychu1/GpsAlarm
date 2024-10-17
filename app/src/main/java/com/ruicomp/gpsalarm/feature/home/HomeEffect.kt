package com.ruicomp.gpsalarm.feature.home

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer

@Immutable
sealed class HomeEffect : Reducer.ViewEffect {
    data class NavigateToTopic(val topicId: String) : HomeEffect()
    data class ShowSnackbar(val message: String) : HomeEffect()
    data class NavigateToScreen(val screen: String) : HomeEffect()
    data class ShowToats(val msg: String): HomeEffect()
}