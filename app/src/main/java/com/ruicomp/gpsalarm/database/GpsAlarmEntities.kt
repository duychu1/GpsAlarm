package com.ruicomp.gpsalarm.database

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "gps_alarms")
@Parcelize
data class GpsAlarmEntities(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Embedded val location: GpsLocation, // Embedded to store the object as a part of the table
    val name: String,
    val reminder: String,
    val isActive: Boolean,
    val radius: Int,  // Radius in meters
    @TypeConverters(DaysConverter::class) val activeDays: List<Int>,  // List of active days
    @Embedded val alarmSettings: AlarmSettings // Embedded to store the object as a part of the table
) : Parcelable

@Entity(tableName = "gps_locations")
@Parcelize
data class GpsLocation(
    val latitude: Double,
    val longitude: Double
) : Parcelable

@Entity(tableName = "alarm_settings")
@Parcelize
data class AlarmSettings(
    val soundPath: String
) : Parcelable

