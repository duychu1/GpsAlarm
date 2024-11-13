package com.ruicomp.gpsalarm.data.fake

import com.ruicomp.gpsalarm.model.AlarmSettings

object AlarmSettingFakeRepo {
    val alarmSettingsList: List<AlarmSettings> = listOf(
        AlarmSettings(
            name = "Morning Wake Up",
            soundUri = "android.resource://com.example.app/raw/alarm_sound_morning",
            isRepeating = true,
            duration = 60,  // 10 minutes in seconds
            soundVolume = 0.8f,   // 80% volume
            vibrationLevel = 0.5f // Medium vibration level
        ),
        AlarmSettings(
            name = "Quick Nap",
            soundUri = "android.resource://com.example.app/raw/alarm_sound_nap",
            isRepeating = false,   // Not repeating
            duration = 120,   // 3 minutes in seconds
            soundVolume = 0.3f,    // 30% volume (softer sound)
            vibrationLevel = 0.2f  // Light vibration
        ),
        AlarmSettings(
            name = "Workout Reminder",
            soundUri = "android.resource://com.example.app/raw/alarm_sound_workout",
            isRepeating = true,     // Repeating daily
            duration = 30,    // 5 minutes in seconds
            soundVolume = 1.0f,     // Full volume (loud)
            vibrationLevel = 0.7f   // Strong vibration
        ),
        AlarmSettings(
            name = "Bedtime Alarm",
            soundUri = "android.resource://com.example.app/raw/alarm_sound_bedtime",
            isRepeating = true,     // Repeating every night
            duration = 30,    // 6 minutes in seconds
            soundVolume = 0.4f,     // Low volume for a gentle reminder
            vibrationLevel = 0.1f   // Very light vibration
        ),
        AlarmSettings(
            name = "Work Start",
            soundUri = "android.resource://com.example.app/raw/alarm_sound_workstart",
            isRepeating = true,     // Repeating on weekdays
            duration = 10,    // 10 minutes in seconds
            soundVolume = 0.9f,     // High volume
            vibrationLevel = 0.6f   // Moderate vibration
        )
    )

}