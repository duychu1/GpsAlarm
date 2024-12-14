package com.ruicomp.gpsalarm.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import kotlin.collections.forEach

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions (
    permissions: List<String>,
    permissionNameDisplay: String,
) {
    val permissionStates = rememberMultiplePermissionsState(
        permissions = permissions
    )

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionStates.launchMultiplePermissionRequest()
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

    permissionStates.permissions.forEach { it ->

        when {
            it.status.isGranted -> {
                Log.d("RequestPermissions","write: granted")
                if (!arePermissionsGranted(context, permissions)) {
                    RequestPermissionDialog(
                        context = context,
                        permissionNameDisplay = permissionNameDisplay
                    ) { }
                }
            }
            !it.status.isGranted -> {
                Log.d("RequestPermissions","write: deny or something else")
                RequestPermissionDialog(
                    context = context,
                    permissionNameDisplay = permissionNameDisplay
                ) { }
            }
        }
    }

}

@Composable
fun DialogWarnPermission() {
    var isShow by remember { mutableStateOf(true) }
    if (isShow) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(text = "Permission") },
            text = { Text(text = "If DENY permission, maybe you CANNOT download video") },
            confirmButton = {
                Button(onClick = { isShow = false }) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isShow = false }) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            shape = MaterialTheme.shapes.large
        )
    }
}

@Composable
fun RequestPermissionDialog(
    context: Context,
    permissionNameDisplay: String,
    onPermissionGranted: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Permission Required") },
        text = { Text("This app needs access $permissionNameDisplay to work properly.") },
        confirmButton = {
            Button(
                onClick = {
                    // Open the device settings to request the permission
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:" + context.packageName)
                    context.startActivity(intent)
                }
            ) {
                Text("OK")
            }
        }
    )
}

fun arePermissionsGranted(context: Context, permissions: List<String>): Boolean {
    return permissions.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}