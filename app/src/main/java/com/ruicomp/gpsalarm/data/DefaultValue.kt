package com.ruicomp.gpsalarm.data

import com.ruicomp.gpsalarm.model.AlarmSettings
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.model.GpsLocation

object DefaultValue {
    val alarmSettings = AlarmSettings(
        name = "Morning Wake Up",
        soundUri = "android.resource://com.example.app/raw/alarm_sound_morning",
        isRepeating = true,
        duration = 60,
        soundVolume = 0.75f,
        vibrationLevel = 0.75f
    )

    val firstGpsAlarm = GpsAlarm(
        id = 0,
        location = GpsLocation(21.035641,105.820285, "123 test default, HCM City"), // San Francisco
        name = "",
        reminder = "",
        isActive = false,
        radius = 300,
        activeDays = listOf(0, 1, 2, 3, 4, 5, 6), // Monday to Friday
        alarmSettings = alarmSettings,
        isPinned = false,
        pinnedAt = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis(),
        lastUpdated = System.currentTimeMillis(),
    )

    val listRadius = listOf(100, 300, 600, 1000, 2500, 5000)

    val firstRadius = 300
}