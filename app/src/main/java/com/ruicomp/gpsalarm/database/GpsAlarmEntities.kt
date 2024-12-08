package com.ruicomp.gpsalarm.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ruicomp.gpsalarm.model.AlarmSettings
import com.ruicomp.gpsalarm.model.GpsLocation
import kotlinx.parcelize.Parcelize

@Entity(tableName = "gps_alarms")
@Parcelize
data class GpsAlarmEntities(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generate primary key
    val location: GpsLocation,
    val name: String,
    val reminder: String,
    val isActive: Boolean,
    val radius: Int,
    val activeDays: List<Int>,
    val alarmSettings: AlarmSettings,
    val isPinned: Boolean,
    val pinnedAt: Long,
    val createdAt: Long,
    val lastUpdated: Long,
) : Parcelable