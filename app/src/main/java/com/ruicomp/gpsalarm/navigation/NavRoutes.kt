package com.ruicomp.gpsalarm.navigation

import com.ruicomp.gpsalarm.model.GpsAlarm
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {
    @Serializable
    data object Home: NavRoutes()

    @Serializable
    data class Detail(val gpsAlarm: GpsAlarm): NavRoutes()

}