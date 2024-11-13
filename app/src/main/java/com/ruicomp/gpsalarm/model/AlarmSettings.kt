package com.ruicomp.gpsalarm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class AlarmSettings(
    val name: String,
    val soundUri: String,
    val isRepeating: Boolean,
    val duration: Int,
    val soundVolume: Float,
    val vibrationLevel: Float,
): Parcelable
