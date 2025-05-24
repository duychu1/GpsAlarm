package com.ruicomp.gpsalarm

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.Size
import com.google.firebase.analytics.FirebaseAnalytics
import java.text.SimpleDateFormat
import java.util.*

class EventLogger private constructor(val context: Context) {
    companion object {
        private var instance: EventLogger? = null
        fun getInstance(): EventLogger? {
            if (instance == null) {
                Log.d("EventLogger", "getInstance: EventLogger - NULL")
            }
            return instance
        }

        fun init(context: Context) {
            if (instance == null) {
                instance = EventLogger(context)
            }
        }
    }

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun log(@Size(min = 1L, max = 32L) eventName: String, eventBundle: Bundle?) {
        firebaseAnalytics.logEvent(eventName, eventBundle)
    }

    fun log(@Size(min = 1L, max = 32L) eventName: String) {
        val bundle = Bundle()
        val time = System.currentTimeMillis()
        bundle.putLong("time_long", time)
        bundle.putString(
            "time_string", SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault()).format(
                Date(time)
            )
        )
        log(eventName, bundle)
    }

    fun logEvent(value: String) {
        try {
            Log.d("android_log", "logEvent: $value")
            val bundle = Bundle()
            bundle.putString("EVENT", value)
            val mFirebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
            mFirebaseAnalytics.logEvent(value, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun log(@Size(min = 1L, max = 32L) eventName: String, message: String) {
        val bundle = Bundle()
        bundle.putString("message", message)
        log(eventName, bundle)
    }
}
