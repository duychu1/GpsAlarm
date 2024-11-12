package com.ruicomp.gpsalarm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapsToDetailResult(
    val lat: Double,
    val lng: Double,
    val radius: Int,
    val addressLine: String? = null,
): Parcelable
