package com.ruicomp.gpsalarm.data

import com.ruicomp.gpsalarm.data.fake.AlarmSettingFakeRepo
import com.ruicomp.gpsalarm.model.AlarmSettings
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.model.GpsLocation

object DefaultValue {
    val alarmSettings = AlarmSettings(
        name = "Morning Wake Up",
        soundUri = "android.resource://com.example.app/raw/alarm_sound_morning",
        isRepeating = true,
        duration = 60,  // 10 minutes in seconds
        soundVolume = 0.8f,   // 80% volume
        vibrationLevel = 0.5f // Medium vibration level
    )

    val firstGpsAlarm = GpsAlarm(
        id = 0,
        location = GpsLocation(21.035641,105.820285, "123 test default, HCM City"), // San Francisco
        name = "Alarm",
        reminder = "Reminder",
        isActive = false,
        radius = 100,
        activeDays = listOf(0, 1, 2, 3, 4, 5, 6), // Monday to Friday
        alarmSettings = alarmSettings,
        isPinned = false,
        pinnedAt = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis(),
        lastUpdated = System.currentTimeMillis(),
    )
}