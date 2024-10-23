package com.ruicomp.gpsalarm.feature.detail

import androidx.compose.runtime.Immutable
import com.ruicomp.gpsalarm.base_mvi.Reducer

@Immutable
sealed class DetailEffect : Reducer.ViewEffect {
    data class NavigateToTopic(val topicId: String) : DetailEffect()
    data class NavigateToScreen(val screen: String) : DetailEffect()
    data class ShowToats(val msg: String): DetailEffect()
}