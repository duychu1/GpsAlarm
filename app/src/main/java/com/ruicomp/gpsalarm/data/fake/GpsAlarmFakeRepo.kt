package com.ruicomp.gpsalarm.data.fake

import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.model.GpsLocation

object GpsAlarmFakeRepo {
    fun fakeListGpsAlarms(): List<GpsAlarm> {
        return listOf(
            GpsAlarm(
                id = 0,
                location = GpsLocation(20.99026, 104.8487278, "123 test default, HCM City"), // San Francisco
                name = "Morning Alarm test default",
                reminder = "Wake up for work",
                isActive = true,
                radius = 100,
                activeDays = listOf(1, 2, 3, 4, 5), // Monday to Friday
                alarmSettings = AlarmSettingFakeRepo.alarmSettingsList.get(0)
            ),
            GpsAlarm(
                id = 1,
                location = GpsLocation(34.0522, -118.2437), // Los Angeles
                name = "Evening Alarm",
                reminder = "Time to wrap up work",
                isActive = false,
                radius = 50,
                activeDays = listOf(1, 3, 5), // Monday, Wednesday, Friday
                alarmSettings = AlarmSettingFakeRepo.alarmSettingsList.get(1)
            ),
            GpsAlarm(
                id = 2,
                location = GpsLocation(40.7128, -74.0060), // New York
                name = "Weekend Alarm",
                reminder = "Get ready for brunch",
                isActive = true,
                radius = 250,
                activeDays = listOf(0, 6), // Sunday and Saturday
                alarmSettings = AlarmSettingFakeRepo.alarmSettingsList.get(2)
            ),
            GpsAlarm(
                id = 3,
                location = GpsLocation(51.5074, -0.1278), // London
                name = "Workout Alarm",
                reminder = "Time to hit the gym",
                isActive = true,
                radius = 300,
                activeDays = listOf(1, 2, 3, 4, 5, 6), // Monday to Saturday
                alarmSettings = AlarmSettingFakeRepo.alarmSettingsList.get(3)
            ),
            GpsAlarm(
                id = 4,
                location = GpsLocation(20.99026, 105.8487278, "123 Nguyen Hue, HCM City"),
                name = "Workout Alarmdddd",
                reminder = "Time to hit the gymasdfaf",
                isActive = true,
                radius = 250,
                activeDays = listOf(1, 2, 3, 4, 5, 6), // Monday to Saturday
                alarmSettings = AlarmSettingFakeRepo.alarmSettingsList.get(4)

            )
        )
    }
}