package com.ruicomp.gpsalarm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GpsLocation(val x: Double, val y: Double, val addressLine: String? = null): Parcelable
