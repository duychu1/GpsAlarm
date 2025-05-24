package com.ruicomp.gpsalarm.utils

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager

fun Window.hideSystemBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.let {
            // Hide the status bar
            it.hide(WindowInsets.Type.statusBars())
            // Optional: Hide the navigation bar
            it.hide(WindowInsets.Type.navigationBars())
            // Optional: Enable immersive mode - makes system bars translucent and auto-hides them
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    } else {
        decorView.systemUiVisibility = (
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // For versions below Android 11 (API 30)
//        @Suppress("DEPRECATION")
//        val flags = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                // These flags are often used with HIDE_NAVIGATION for a more seamless experience
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN // Optional: if you want to hide status bar too
//                )
//        @Suppress("DEPRECATION")
//        this.decorView.systemUiVisibility = this.decorView.systemUiVisibility or flags
    }
}

fun Window.hideNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Use WindowInsetsController for Android R and above
        insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.navigationBars())
            // Optional: Set behavior to show transient bars on swipe for immersive effect
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        // Use deprecated systemUiVisibility for older Android versions
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}

/**
 * Shows the system navigation bar for the given Window.
 * Uses WindowInsetsController for Android R+ and systemUiVisibility for older versions.
 */
fun Window.showNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Use WindowInsetsController for Android R and above
        insetsController?.let { controller ->
            controller.show(WindowInsets.Type.navigationBars())
            // Optional: Restore default behavior
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_DEFAULT // Or your desired default
        }
    } else {
        // Use deprecated systemUiVisibility for older Android versions
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Or your desired default flags
                        or View.SYSTEM_UI_FLAG_VISIBLE
                )
    }
}

fun Window.setDarkIconStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        decorView.windowInsetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
//        WindowCompat.getInsetsController(this,decorView).isAppearanceLightStatusBars = false
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility =
            decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Window.setLightIconStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        decorView.windowInsetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility =
            decorView.systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}