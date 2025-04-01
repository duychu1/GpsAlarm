package com.ruicomp.gpsalarm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class AlarmSettings(
    val name: String,
    val soundUri: String,
    val isRepeating: Boolean,
    val duration: Int,
    val soundVolume: Float,
    val vibrationLevel: Float,
): Parcelable {
    fun getAlarmDescription(): String =
        if (soundVolume > 0 && vibrationLevel > 0) {
            "Sound & Vibration"
        } else if (soundVolume > 0) {
            "Sound"
        } else if (vibrationLevel > 0) {
            "Vibration"
        } else {
            "Silent"
        }

    fun getAlarmDuration(): String =
        if (isRepeating) "Repeating" else "${duration}s"
}
