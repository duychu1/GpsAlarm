package com.ruicomp.gpsalarm.feature.service

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.data.repository.GpsAlarmRepository
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.utils.BroadcastUtils
import com.ruicomp.gpsalarm.utils.dlog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var gpsAlarmRepository: GpsAlarmRepository

    private lateinit var locationManager: LocationManager
    private var listTargetAlarms: MutableList<GpsAlarm> = mutableListOf()
    private lateinit var stopPendingIntent: PendingIntent

    private val locationListener = LocationListener { location ->
        dlog("locationListener: onLocationChanged: $location")
        listTargetAlarms.removeAll { targetLocation ->
            val distance = FloatArray(1)
            Location.distanceBetween(
                location.latitude, location.longitude,
                targetLocation.location.latitude, targetLocation.location.longitude, distance
            )
            dlog("locationListener: distance: ${distance[0]}")
            if (distance[0] < targetLocation.radius) {
                handleArrival()
            }
            distance[0] < targetLocation.radius // Remove if distance is less than radius
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            dlog("broadcastReceiver: onReceive: ${intent.action}")
            if (intent.action == "ACTION_STOP_SERVICE") {
                stopSelf()
            }
        }
    }

//    val stopServiceIntent = Intent(this, LocationService::class.java).apply {
//        action = "ACTION_STOP_SERVICE"
//    }
//    val stopPendingIntent1: PendingIntent = PendingIntent.getBroadcast(
//        this,
//        0,
//        stopServiceIntent,
//        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
//    )
//
//    // Create a NotificationCompat.Action to stop the service
//    val stopAction = NotificationCompat.Action.Builder(
//        R.drawable.ic_my_location, // Replace with your stop icon
//        "Turn off", // Replace with your title
//        stopPendingIntent
//    ).build()



    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        BroadcastUtils.registerReceiver(this, broadcastReceiver, IntentFilter("ACTION_STOP_SERVICE"))
        createNotificationChannel(this)
        startForegroundService()
        listenLocationChange(minTimeMs = 1000L, minDistanceM = 10f, locationListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        dlog("onStartCommand: intent action: ${intent?.action}")
        val targetAlarm = intent?.getParcelableExtra<GpsAlarm>("target_location")

        targetAlarm?.let {
            when(intent.action) {
                "ACTION_NEW_TARGET" -> listTargetAlarms.add(it)
                "ACTION_REMOVE_TARGET" -> {
                    listTargetAlarms.removeAll { alarm -> alarm.id == it.id  }
                    if (listTargetAlarms.isEmpty()) {
                        stopSelf()
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        locationManager.removeUpdates(locationListener)

        CoroutineScope(Dispatchers.IO).launch {
            listTargetAlarms.forEach {
                try {
                    gpsAlarmRepository.updateIsActiveById(it.id, false)
                } catch (e: Exception) {
                    dlog("onDestroy: failed to set alarm inactive ${e.message}")
                    e.printStackTrace()
                }
            }
        }
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    // Start the foreground service with a notification
    private fun startForegroundService() {
        stopPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent("ACTION_STOP_SERVICE"),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Tracking Location")
            .setContentText("Tracking your location in the background.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, // Replace with your stop icon
                "Turn off", // Replace with your title
                stopPendingIntent)
            .build()

        startForeground(1, notification)
    }

    // Listen for location updates
    private fun listenLocationChange(minTimeMs: Long, minDistanceM: Float, locationListener: LocationListener) {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTimeMs,
                minDistanceM,
                locationListener,
            )
        } catch (ex: SecurityException) {
            Log.e("listenLocationChange", "Permission denied")
        } catch (ex: Exception) {
            Log.e("listenLocationChange", "Error while listening for location changes", ex)
        }
    }


    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "location_channel",
            "Location Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for location tracking"
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    // Send a notification when the user enters the target location radius
    private fun handleArrival() {
        val notificationBuilder = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("You are near the target location!")
            .setContentText("You have entered the target radius.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(0, // Replace with your stop icon
                "Turn off", // Replace with your title
                stopPendingIntent)

        val notificationManager = NotificationManagerCompat.from(this)

        try {
            notificationManager.notify(1, notificationBuilder.build())
        } catch (se: SecurityException) {
            dlog("handleArrival: SecurityException: ${se.message}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun hasNotificationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }


}
