package com.ruicomp.gpsalarm.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
    var showGpsDialog by remember { mutableStateOf(false) }
    var isGpsEnableRequestLauncher by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // GPS was enabled by the user
            isGpsEnabled = true
            onGpsEnabled()
            showGpsDialog = false
            Log.d("GpsCheckAndRequest", "GPS enabled")
        } else {
            // User did not enable GPS
            isGpsEnabled = false
            onGpsDisabled()
            showGpsDialog = true
            Log.d("GpsCheckAndRequest", "GPS not enabled")
        }

    }

    LaunchedEffect(key1 = isGpsEnabled) {
        if (!isGpsEnabled && !isGpsEnableRequestLauncher) {
            val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(1000).build()
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
                        isGpsEnableRequestLauncher = true
                        showGpsDialog = true
                        // Show the dialog by calling startIntentSenderForResult().
                        launcher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )

                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                        Log.d("GpsCheckAndRequest", "Error sending intent: $sendEx")
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

    if (showGpsDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            title = { Text("GPS Required") },
            text = { Text("GPS is required for this feature. Please enable it.") },
            confirmButton = {
                Button(onClick = {
                    showGpsDialog = false
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