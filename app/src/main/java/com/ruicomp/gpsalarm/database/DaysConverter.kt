package com.ruicomp.gpsalarm.database

import androidx.room.TypeConverter

class DaysConverter {

    @TypeConverter
    fun fromActiveDays(value: List<Int>): String {
        return value.joinToString(",") // Converts the list to a comma-separated string
    }

    @TypeConverter
    fun toActiveDays(value: String): List<Int> {
        return value.split(",").map { it.toInt() } // Converts the string back to a list of integers
    }
}
