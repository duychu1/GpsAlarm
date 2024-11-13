package com.ruicomp.gpsalarm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GpsAlarm(
    val id: Int,
    val location: GpsLocation, // A data class to hold GPS coordinates
    val name: String,
    val reminder: String,
    val isActive: Boolean,
    val radius: Int,  // Radius in meters
    val activeDays: List<Int>,  // List of active days (0 = Sunday, 1 = Monday, etc.)
    val alarmSettings: AlarmSettings  // Path or URI of the sound file
): Parcelable
