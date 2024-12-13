package com.ruicomp.gpsalarm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GpsLocation(val latitude: Double, val longitude: Double, val addressLine: String? = null): Parcelable
