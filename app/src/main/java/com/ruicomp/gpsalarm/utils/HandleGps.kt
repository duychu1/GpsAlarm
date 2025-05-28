package com.ruicomp.gpsalarm.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.location.LocationManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

@Composable
fun GpsCheckAndRequest(
    onGpsEnabled: () -> Unit = {},
    onGpsDisabled: () -> Unit = {},
) {
    val context = LocalContext.current
    var isGpsEnabled by remember { mutableStateOf(isGpsEnabled(context)) }
    var shouldShowGpsDialog by remember { mutableStateOf(false) }
    var isGpsEnableRequestLauncher by remember { mutableStateOf(false) }
    var onResumeTriggerCount by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // GPS was enabled by the user
            isGpsEnabled = true
            onGpsEnabled()
            shouldShowGpsDialog = false
            Log.d("GpsCheckAndRequest", "GPS enabled")
        } else {
            // User did not enable GPS
            isGpsEnabled = false
            onGpsDisabled()
            shouldShowGpsDialog = true
            Log.d("GpsCheckAndRequest", "GPS not enabled")
        }
    }

    // Listen for GPS provider changes using a android . content . BroadcastReceiver
            DisposableEffect(context) {
                val gpsSwitchStateReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
                            val newGpsStatus = isGpsEnabled(context)
                            Log.d("GpsCheckAndRequest", "PROVIDERS_CHANGED_ACTION: newStatus=$newGpsStatus, oldStatus=$isGpsEnabled")
//                            if (newGpsStatus != isGpsEnabled) {
                                isGpsEnabled = newGpsStatus
                                if (newGpsStatus) {
                                    onGpsEnabled()
                                    shouldShowGpsDialog = false // Hide dialog if GPS is enabled externally
                                } else {
                                    onGpsDisabled()
//                                    shouldShowGpsDialog = true

                                    // Optionally, decide if you want to show the dialog again here
                                    // For example, if the launcher is not active.
                                    if (!isGpsEnableRequestLauncher) {
                                        shouldShowGpsDialog = true
                                    }
                                }
//                            }
                        }
                    }
                }

                val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)

                // Register the receiver
                // For Android N and above, registerReceiver with context is enough.
                // For older versions, you might need to handle it differently if targeting them specifically.
                // However, for composables, LocalContext.current is usually an Activity context.
                BroadcastUtils.registerReceiver(context, gpsSwitchStateReceiver, intentFilter)
                Log.d("GpsCheckAndRequest", "BroadcastReceiver registered for GPS changes")


                onDispose {
                    context.unregisterReceiver(gpsSwitchStateReceiver)
                    Log.d("GpsCheckAndRequest", "BroadcastReceiver unregistered")
                }
            }

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    dlog("onResume: GpsCheckAndRequest")
                    isGpsEnabled = isGpsEnabled(context)
                    onResumeTriggerCount++
                }
                else -> {

                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    LaunchedEffect(key1 = isGpsEnabled, key2 = onResumeTriggerCount) {
        if (!isGpsEnabled && !isGpsEnableRequestLauncher) {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(3000).build()
            val builder = LocationSettingsRequest.Builder().addLocationRequest(request)
            val client = LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                isGpsEnabled = true
                onGpsEnabled()
                Log.d("GpsCheckAndRequest", "GPS is already enabled")
            }.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        shouldShowGpsDialog = true
                        isGpsEnableRequestLauncher = true
                        // Show the dialog by calling startIntentSenderForResult().
                        launcher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )

                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                        Log.d("GpsCheckAndRequest", "Error sending intent: $sendEx")
                    } catch (e: Exception) {
//                        shouldShowGpsDialog = true
                        Log.d("GpsCheckAndRequest", "Error: $e")
                    }
                } else {
                    // Settings change not available
                    isGpsEnabled = false
                    onGpsDisabled()
                    Log.d("GpsCheckAndRequest", "Settings change not available")
                }
            }
        }
    }

    if (shouldShowGpsDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            title = { Text("GPS Required") },
            text = { Text("GPS is required for this feature. Please enable it.") },
            confirmButton = {
                Button(onClick = {
                    shouldShowGpsDialog = false
                    isGpsEnableRequestLauncher = false
                    isGpsEnabled = isGpsEnabled(context)
                    if (!isGpsEnabled) {
                        val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                }) {
                    Text("Go to Settings")
                }
            }
        )
    }
}
fun isGpsEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}