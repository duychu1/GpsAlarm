package com.ruicomp.gpsalarm.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val EXAMPLE_STRING = stringPreferencesKey("example_string")
    val EXAMPLE_INT = intPreferencesKey("example_int")
    val EXAMPLE_BOOLEAN = booleanPreferencesKey("example_boolean")
    val CAMERA_LATITUDE = doublePreferencesKey("camera_latitude")
    val CAMERA_LONGITUDE = doublePreferencesKey("camera_longitude")
    val CAMERA_ZOOM = floatPreferencesKey("camera_zoom")


    val IS_MAP_DARK_THEME = booleanPreferencesKey("is_map_dark_theme")
}