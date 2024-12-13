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
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.utils.BroadcastUtils
import com.ruicomp.gpsalarm.utils.dlog

class LocationService : Service() {

    private lateinit var locationManager: LocationManager
    private var listTargetAlarms: MutableList<GpsAlarm> = mutableListOf()

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
                sendNotification()
            }
            distance[0] < targetLocation.radius // Remove if distance is less than radius
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)

            if (latitude != 0.0 && longitude != 0.0) {
                // Process the data
                Log.d("LocationService", "Received data via broadcast - Latitude: $latitude, Longitude: $longitude")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        BroadcastUtils.registerReceiver(this, broadcastReceiver, IntentFilter("com.example.location.UPDATE"))
        createNotificationChannel(this)
        startForegroundService()
        listenLocationChange(minTimeMs = 1000L, minDistanceM = 10f, locationListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val targetAlarm = intent?.getParcelableExtra<GpsAlarm>("target_location")
        targetAlarm?.let {
            listTargetAlarms.add(it)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        locationManager.removeUpdates(locationListener)
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    // Start the foreground service with a notification
    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Tracking Location")
            .setContentText("Tracking your location in the background.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)
    }

    // Listen for location updates
    private fun listenLocationChange(minTimeMs: Long = 1000L, minDistanceM: Float = 10f, locationListener: LocationListener) {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTimeMs,
                minDistanceM,
                locationListener
            )
        } catch (ex: SecurityException) {
            Log.e("LocationService", "Permission denied")
        } catch (ex: Exception) {
            Log.e("LocationService", "Error while listening for location changes", ex)
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
    private fun sendNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("You are near the target location!")
            .setContentText("You have entered the target radius.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notificationManager.notify(1, notificationBuilder.build())
        } else {
            notificationManager.notify(1, notificationBuilder.build())
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
