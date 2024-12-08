package com.ruicomp.gpsalarm.database

import androidx.room.TypeConverter
import com.ruicomp.gpsalarm.model.AlarmSettings
import com.ruicomp.gpsalarm.model.GpsLocation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromGpsLocation(location: GpsLocation): String {
        return Json.encodeToString(location)
    }

    @TypeConverter
    fun toGpsLocation(locationString: String): GpsLocation {
        return Json.decodeFromString(locationString)
    }

    @TypeConverter
    fun fromAlarmSettings(settings: AlarmSettings): String {
        return Json.encodeToString(settings)
    }

    @TypeConverter
    fun toAlarmSettings(settingsString: String): AlarmSettings {
        return Json.decodeFromString(settingsString)
    }

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toIntList(string: String): List<Int> {
        return string.split(",").map { it.toInt() }
    }
}