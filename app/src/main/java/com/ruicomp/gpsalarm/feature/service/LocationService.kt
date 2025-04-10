package com.ruicomp.gpsalarm.feature.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.data.repository.GpsAlarmRepository
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.utils.BroadcastUtils
import com.ruicomp.gpsalarm.utils.dlog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    private val FOREGROUND_SERVICE_ID = 101
    private val ARRIVED_NOTIFICATION_ID = 102
    private val INTENT_STOP_ALL_CODE = 202
    private val INTENT_STOP_CODE = 201
    private val ARRIVED_NOTI_GROUP_KEY = "ARRIVED_NOTI_GROUP_KEY"

    @Inject
    lateinit var gpsAlarmRepository: GpsAlarmRepository

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var soundJob: Job? = null
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
                    stopSoundAndVibration()
                }

                "ACTION_STOP_SERVICE" -> {
                    notificationManager.cancelAll()
                    stopSoundAndVibration()
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
        listenLocationChange(minTimeMs = 1000L, minDistanceM = 3f, locationListener)
        BroadcastUtils.registerReceiver(this, broadcastReceiver, intentFilter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        dlog("onStartCommand: intent action: ${intent?.action}")
        val targetAlarm = intent?.getParcelableExtra<GpsAlarm>("target_location")

        targetAlarm?.let {
            when (intent.action) {
                "ACTION_NEW_TARGET" -> listTargetAlarms.add(it)
                "ACTION_REMOVE_TARGET" -> {
                    listTargetAlarms.removeAll { alarm -> alarm.id == it.id }
                    if (listTargetAlarms.isEmpty()) {
                        stopSelf()
                        return@let
                    }
                }
            }
            handleNoti()
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
        stopAllPendingIntent = PendingIntent.getBroadcast(
            this,
            INTENT_STOP_ALL_CODE,
            Intent("ACTION_STOP_SERVICE"),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = foregroundNoti()

        startForeground(FOREGROUND_SERVICE_ID, notification)
    }

    @SuppressLint("StringFormatMatches")
    private fun handleNoti(listSize: Int = listTargetAlarms.size) {
        val contentText = when (listSize) {
            0 -> getString(R.string.all_destinations_have_been_reached)
            1 -> getString(R.string.on_the_way_to_the_destination)
            else -> getString(R.string.on_the_way_to_number_destinations, listSize)
        }

        val contentTitle = when (listSize) {
            0 -> getString(R.string.arrived)
            else -> getString(R.string.progressing)
        }


        val notification = foregroundNofiUpdate(contentTitle, contentText)

        notificationManager = NotificationManagerCompat.from(this)

        try {
            notificationManager.notify(FOREGROUND_SERVICE_ID, notification)
        } catch (se: SecurityException) {
            dlog("handleArrival: SecurityException: ${se.message}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun foregroundNoti(): Notification =
        NotificationCompat.Builder(this, "location_channel")
            .setContentTitle(getString(R.string.progressing))
            .setContentText(getString(R.string.on_the_way_to_the_destination))
            .setSmallIcon(R.drawable.ic_app_noti)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setDeleteIntent(stopAllPendingIntent)
            .setOngoing(true)
            .addAction(
                0, // Replace with your stop icon
                getString(R.string.stop_all), // Replace with your title
                stopAllPendingIntent
            )
            .build()

    private fun foregroundNofiUpdate(contentTitle: String, contentText: String): Notification =
        NotificationCompat.Builder(this, "location_channel")
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_app_noti)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .addAction(
                0, // Replace with your stop icon
                getString(R.string.stop_all), // Replace with your title
                stopAllPendingIntent
            )
            .build()

    // Listen for location updates
    private fun listenLocationChange(
        minTimeMs: Long,
        minDistanceM: Float,
        locationListener: LocationListener,
    ) {
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


    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "location_channel",
            "Location Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for location tracking"
            enableVibration(true)
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

        val notificationBuilder = arrivedNoti(gpsAlarm.name)
        playSoundAndVibrate(this, gpsAlarm)

        try {
            notificationManager.notify(ARRIVED_NOTIFICATION_ID, notificationBuilder.build())
        } catch (se: SecurityException) {
            dlog("handleArrival: SecurityException: ${se.message}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        handleNoti(listTargetAlarms.size - 1)
    }

    private fun arrivedNoti(gpsAlarmName: String): NotificationCompat.Builder =
        NotificationCompat.Builder(this, "location_channel")
            .setContentTitle(getString(R.string.arrived))
            .setContentText(
                getString(
                    R.string.you_have_entered_the_area,
                    getFirst5Character(gpsAlarmName)
                )
            )
            .setSmallIcon(R.drawable.ic_app_noti)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setAutoCancel(true)
            .setGroup(ARRIVED_NOTI_GROUP_KEY)
            .setGroupSummary(true)
            .addAction(
                0,
                getString(R.string.stop),
                stopPendingIntent
            )



    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun playSoundAndVibrate(context: Context, gpsAlarm: GpsAlarm) {
        stopSoundAndVibration()

        val settingUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, settingUri)
            setAudioAttributes(android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                .build())
            prepare()
        }
        mediaPlayer?.setVolume(gpsAlarm.alarmSettings.soundVolume, gpsAlarm.alarmSettings.soundVolume) // Default volume
        mediaPlayer?.start()

        vibrator = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.getSystemService(context, VibratorManager::class.java)?.defaultVibrator
        } else {
            ContextCompat.getSystemService(context, Vibrator::class.java)
        })

        val vibrationLevel = (gpsAlarm.alarmSettings.vibrationLevel*255).toInt()

        if (vibrationLevel > 0) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(
                        0,
                        500,
                        1500,
                        500
                    ), // Vibrate for durationInMillis, then off for durationInMillis
                    intArrayOf(0, vibrationLevel, 0, vibrationLevel),
                    0 // Repeat indefinitely
                )
            )
        }

        if (!gpsAlarm.alarmSettings.isRepeating) {
            soundJob = CoroutineScope(Dispatchers.Main).launch {
                delay(gpsAlarm.alarmSettings.duration.toLong() * 1000)
                stopSoundAndVibration()

            }
        }
    }


    private fun stopSoundAndVibration() {
        soundJob?.cancel()
        soundJob = null

        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }

        vibrator?.cancel()
        vibrator = null
        if (listTargetAlarms.isEmpty()) stopSelf()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun hasNotificationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }


}
