package com.ruicomp.gpsalarm.datastore2

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
//    val EXAMPLE_STRING = stringPreferencesKey("example_string")
//    val EXAMPLE_INT = intPreferencesKey("example_int")
//    val EXAMPLE_BOOLEAN = booleanPreferencesKey("example_boolean")
//    val EXAMPLE_LONG = longPreferencesKey("example_long")
//    val EXAMPLE_DOUBLE = doublePreferencesKey("example_double")
//    val EXAMPLE_FLOAT = floatPreferencesKey("example_float")

    val LANGUAGE_STRING = stringPreferencesKey("language_string")

    val MAP_TYPE_STRING = stringPreferencesKey("map_type_string")
    val MAP_TYPE_INT = intPreferencesKey("map_type_int")
    val LIVE_EARTH_LOCATION = booleanPreferencesKey("live_earth_location")
    val IS_ONBOARD_COMPLETE = booleanPreferencesKey("is_onboard_complete")
    val IS_PERMISSION_COMPLETE = booleanPreferencesKey("is_permission_complete")
    val TEMPLATE_TYPE_STRING = stringPreferencesKey("template_type_string")
    val LAST_PREMIUM_SHOWN_AT = longPreferencesKey("interval_show_less_premium")



}