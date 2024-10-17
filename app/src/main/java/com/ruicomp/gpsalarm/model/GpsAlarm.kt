package com.ruicomp.gpsalarm.model

data class GpsAlarm(
    val id: Int,
    val location: Pair<Double, Double>, // A data class to hold GPS coordinates
    val name: String,
    val reminder: String,
    val isActive: Boolean,
    val radius: Int,  // Radius in meters
    val isRepeating: Boolean,
    val durationAlarm: Int,  // Duration in minutes or seconds, specify unit as needed
    val activeDays: List<Int>,  // List of active days (0 = Sunday, 1 = Monday, etc.)
    val alarmSound: String  // Path or URI of the sound file
)
