package com.ruicomp.gpsalarm.utils // Replace with your actual package name

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtils {

    /**
     * Checks if all specified permissions are granted.
     *
     * @param context Context
     * @param permissions List of permission constants (e.g., Manifest.permission.CAMERA)
     * @return True if all permissions are granted, false otherwise.
     */
    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasPermissions(context: Context, permissions: List<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Determines which permissions from the requested list still need to be requested
     * (i.e., are not already granted).
     *
     * @param context Context
     * @param requestedPermissions The list of permissions you intend to request.
     * @return A list of permissions that are not yet granted.
     */
    fun getPermissionsToRequest(context: Context, requestedPermissions: List<String>): List<String> {
        return requestedPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Checks if rationale should be shown for any of the given permissions.
     */
    fun shouldShowRationale(context: Context, permissionsToCheck: List<String>): Boolean {
        return permissionsToCheck.any {
            ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it)
        }
    }


    /**
     * Creates and shows a standard rationale dialog.
     *
     * @param activity The Activity context.
     * @param rationaleMessage Message explaining why the permissions are needed.
     * @param positiveButtonText Text for the positive button (e.g., "OK", "Continue").
     * @param onPositiveClick Lambda executed when the positive button is clicked (usually triggers the actual permission request).
     * @param negativeButtonText Text for the negative button (e.g., "Cancel"). Defaults to null (no negative button).
     * @param onNegativeClick Lambda executed when the negative button is clicked. Defaults to null.
     */
    fun showRationaleDialog(
        context: Context,
        rationaleMessage: String,
        positiveButtonText: String = "OK",
        onPositiveClick: () -> Unit,
        negativeButtonText: String? = "Cancel",
        onNegativeClick: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setMessage(rationaleMessage)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                dialog.dismiss()
                onPositiveClick()
            }
            .apply {
                if (negativeButtonText != null) {
                    setNegativeButton(negativeButtonText) { dialog, _ ->
                        dialog.dismiss()
                        onNegativeClick?.invoke()
                    }
                }
            }
            .setCancelable(false) // Prevent dismissing by tapping outside
            .show()
    }

    /**
     * Creates and shows a dialog informing the user that permissions were permanently
     * denied and guiding them to app settings.
     *
     * @param activity The Activity context.
     * @param message Message explaining the situation and instructing the user.
     * @param positiveButtonText Text for the positive button (e.g., "Go to Settings").
     * @param negativeButtonText Text for the negative button (e.g., "Cancel").
     */
    fun showAppSettingsDialog(
        activity: Activity,
        message: String = "Permissions were permanently denied. You need to enable them in app settings to use this feature.",
        positiveButtonText: String = "Go to Settings",
        negativeButtonText: String = "Cancel"
    ) {
        AlertDialog.Builder(activity)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                dialog.dismiss()
                openAppSettings(activity)
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                dialog.dismiss()
                // Optionally handle cancellation
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Creates and shows a dialog informing the user that permissions were permanently
     * denied and guiding them to app settings.
     * Overload for Fragments.
     *
     * @param fragment The Fragment context.
     * @param message Message explaining the situation and instructing the user.
     * @param positiveButtonText Text for the positive button (e.g., "Go to Settings").
     * @param negativeButtonText Text for the negative button (e.g., "Cancel").
     */
    fun showAppSettingsDialog(
        context: Context,
        message: String = "Permissions were permanently denied. You need to enable them in app settings to use this feature.",
        positiveButtonText: String = "Go to Settings",
        negativeButtonText: String = "Cancel"
    ) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                dialog.dismiss()
                openAppSettings(context)
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                dialog.dismiss()
                // Optionally handle cancellation
            }
            .setCancelable(false)
            .show()
    }


    /**
     * Opens the application's specific settings screen.
     *
     * @param context Context
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Requests permissions with rationale handling.
     */
    fun requestPermissionsWithRationale(
        context: Context,
        permissionsToRequest: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>,
        rationaleMessage: String,
        onPermissionsGranted: () -> Unit,
        onRationaleNeeded: (() -> Unit)? = null,
        onRationaleDeclined: (() -> Unit)? = null
    ) {
        val permissionsNeeded = getPermissionsToRequest(context, permissionsToRequest.toList())

        if (permissionsNeeded.isEmpty()) {
            // All permissions already granted
            onPermissionsGranted()
            return
        }

        val showRationale = shouldShowRationale(context, permissionsNeeded)

        if (showRationale) {
            onRationaleNeeded?.invoke() // Callback before showing dialog
            showRationaleDialog(
                context = context,
                rationaleMessage = rationaleMessage,
                onPositiveClick = { launcher.launch(permissionsNeeded.toTypedArray()) },
                onNegativeClick = { onRationaleDeclined?.invoke() }
            )
        } else {
            // No rationale needed (first time asking or permanently denied previously)
            // Just launch the request directly
            launcher.launch(permissionsNeeded.toTypedArray())
        }
    }

    @SuppressLint("ServiceCast")
    fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun dialogRequestEnableGPS(
        context: Context,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
    ) {
        AlertDialog.Builder(context)
            .setTitle("Enable GPS")
            .setMessage("GPS is required for this application to function. Please enable GPS.")
            .setPositiveButton("Enable") { dialog, _ ->
                dialog.dismiss()
                onPositiveClick()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                onNegativeClick()
            }
            .setCancelable(false)
            .show()
    }
}
