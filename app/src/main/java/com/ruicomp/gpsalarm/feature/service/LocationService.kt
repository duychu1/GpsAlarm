package com.ruicomp.gpsalarm.feature.service

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

    private val FOREGROUND_SERVICE_ID = 101
    private val ARRIVED_NOTIFICATION_ID = 102
    private val INTENT_STOP_ALL_CODE = 202
    private val INTENT_STOP_CODE = 201

    @Inject
    lateinit var gpsAlarmRepository: GpsAlarmRepository

    private lateinit var locationManager: LocationManager
    private var listTargetAlarms: MutableList<GpsAlarm> = mutableListOf()
    private lateinit var stopAllPendingIntent: PendingIntent
    private lateinit var stopPendingIntent: PendingIntent
    private lateinit var notificationManager: NotificationManagerCompat

    private val locationListener = LocationListener { location ->
        dlog("locationListener: onLocationChanged: $location")
        listTargetAlarms.removeAll { gpsAlarm ->
            val distance = FloatArray(1)
            Location.distanceBetween(
                location.latitude, location.longitude,
                gpsAlarm.location.latitude, gpsAlarm.location.longitude, distance
            )
            dlog("locationListener: distance: ${distance[0]}")
            if (distance[0] < gpsAlarm.radius) {
                CoroutineScope(Dispatchers.IO).launch {
                    gpsAlarmRepository.updateIsActiveById(gpsAlarm.id, false)
                }
                handleArrival(gpsAlarm)
            }
            distance[0] < gpsAlarm.radius // Remove if distance is less than radius
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            dlog("broadcastReceiver: onReceive: ${intent.action}")
            when (intent.action) {
                "ACTION_STOP_ARRIVED" -> {
                    notificationManager.cancel(ARRIVED_NOTIFICATION_ID)
                }
                "ACTION_STOP_SERVICE" -> {
                    notificationManager.cancel(ARRIVED_NOTIFICATION_ID)
                    stopSelf()
                }
            }
        }
    }

    val intentFilter = IntentFilter().apply {
        addAction("ACTION_STOP_SERVICE")
        addAction("ACTION_STOP_ARRIVED")
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        createNotificationChannel(this)
        startForegroundService()
        listenLocationChange(minTimeMs = 1000L, minDistanceM = 10f, locationListener)
        BroadcastUtils.registerReceiver(this, broadcastReceiver, intentFilter)
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
            handleNoti()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleNoti(listSize: Int = listTargetAlarms.size) {
        if (listSize == 0) return
        // Default text
        val contentText: String = if (listSize == 1) {
            "On the way to the destination"
        } else {
            "On the way to $listSize destinations"
        }

// Build the notification
        val notification = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Progressing")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .addAction(0, // Replace with your stop icon
                getString(R.string.stop_all), // Replace with your title
                stopAllPendingIntent)
            .build()

        notificationManager = NotificationManagerCompat.from(this)

        try {
            notificationManager.notify(FOREGROUND_SERVICE_ID, notification)
        } catch (se: SecurityException) {
            dlog("handleArrival: SecurityException: ${se.message}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
        stopAllPendingIntent = PendingIntent.getBroadcast(
            this,
            INTENT_STOP_ALL_CODE,
            Intent("ACTION_STOP_SERVICE"),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Progressing")
            .setContentText("On the way to destination")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, // Replace with your stop icon
                getString(R.string.stop_all), // Replace with your title
                stopAllPendingIntent)
            .build()

        startForeground(FOREGROUND_SERVICE_ID, notification)
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

    fun getFirst5Character(title: String): String {
        return if (title.length > 5) {
            title.substring(0, 5) + "..."
        } else {
            title
        }
    }

    // Send a notification when the user enters the target location radius
    private fun handleArrival(gpsAlarm: GpsAlarm) {
        stopPendingIntent = PendingIntent.getBroadcast(
            this,
            INTENT_STOP_CODE,
            Intent("ACTION_STOP_ARRIVED"),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle(getString(R.string.arrived))
            .setContentText(
                getString(
                    R.string.you_have_entered_the_area,
                    getFirst5Character(gpsAlarm.name)
                )
            )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(0,
                getString(R.string.stop),
                stopPendingIntent)

        try {
            notificationManager.notify(ARRIVED_NOTIFICATION_ID, notificationBuilder.build())
        } catch (se: SecurityException) {
            dlog("handleArrival: SecurityException: ${se.message}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        handleNoti(listTargetAlarms.size - 1)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun hasNotificationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }


}
