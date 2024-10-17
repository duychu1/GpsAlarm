package com.ruicomp.gpsalarm.data.fake

import com.ruicomp.gpsalarm.model.GpsAlarm

object GpsAlarmFakeRepo {
    fun fakeListGpsAlarms(): List<GpsAlarm> {
        return listOf(
            GpsAlarm(
                id = 0,
                location = Pair(37.7749, -122.4194), // San Francisco
                name = "Morning Alarm",
                reminder = "Wake up for work",
                isActive = true,
                radius = 100,
                isRepeating = true,
                durationAlarm = 30, // 30 minutes
                activeDays = listOf(1, 2, 3, 4, 5), // Monday to Friday
                alarmSound = "sounds/morning_alarm.mp3"
            ),
            GpsAlarm(
                id = 1,
                location = Pair(34.0522, -118.2437), // Los Angeles
                name = "Evening Alarm",
                reminder = "Time to wrap up work",
                isActive = false,
                radius = 50,
                isRepeating = false,
                durationAlarm = 15, // 15 minutes
                activeDays = listOf(1, 3, 5), // Monday, Wednesday, Friday
                alarmSound = "sounds/evening_alarm.mp3"
            ),
            GpsAlarm(
                id = 2,
                location = Pair(40.7128, -74.0060), // New York
                name = "Weekend Alarm",
                reminder = "Get ready for brunch",
                isActive = true,
                radius = 200,
                isRepeating = true,
                durationAlarm = 45, // 45 minutes
                activeDays = listOf(0, 6), // Sunday and Saturday
                alarmSound = "sounds/weekend_alarm.mp3"
            ),
            GpsAlarm(
                id = 3,
                location = Pair(51.5074, -0.1278), // London
                name = "Workout Alarm",
                reminder = "Time to hit the gym",
                isActive = true,
                radius = 300,
                isRepeating = true,
                durationAlarm = 60, // 60 minutes
                activeDays = listOf(1, 2, 3, 4, 5, 6), // Monday to Saturday
                alarmSound = "sounds/workout_alarm.mp3"
            )
        )
    }
}