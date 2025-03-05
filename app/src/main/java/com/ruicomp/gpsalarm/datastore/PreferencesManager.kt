package com.ruicomp.gpsalarm.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import jakarta.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class PreferencesManager @Inject internal constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getString(key: Preferences.Key<String>): String? = dataStore.data.first()[key]



    suspend fun saveInt(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getInt(key: Preferences.Key<Int>): Int? = dataStore.data.first()[key]


    suspend fun saveBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getBoolean(key: Preferences.Key<Boolean>): Boolean? = dataStore.data.first()[key]


    suspend fun saveLong(key: Preferences.Key<Long>, value: Long) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getLong(key: Preferences.Key<Long>): Long? = dataStore.data.first()[key]



    suspend fun saveDouble(key: Preferences.Key<Double>, value: Double) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getDouble(key: Preferences.Key<Double>): Double? = dataStore.data.first()[key]



    suspend fun saveFloat(key: Preferences.Key<Float>, value: Float) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getFloat(key: Preferences.Key<Float>): Float? = dataStore.data.first()[key]

}
