package com.ruicomp.gpsalarm.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class PreferencesManager @Inject internal constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        @Volatile
        private var INSTANCE: PreferencesManager? = null
        fun getInstance(dataStore: DataStore<Preferences>): PreferencesManager =
            INSTANCE ?: synchronized(this) { INSTANCE ?: PreferencesManager(dataStore).also { INSTANCE = it } }
    }

    // Generic save function
    suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    // Generic get function
    suspend fun <T> get(key: Preferences.Key<T>): T? = dataStore.data.map { preferences ->
        preferences[key]
    }.first()

    // Specific save functions for each type, for clarity
    suspend fun saveString(key: Preferences.Key<String>, value: String) = save(key, value)
    suspend fun saveInt(key: Preferences.Key<Int>, value: Int) = save(key, value)
    suspend fun saveBoolean(key: Preferences.Key<Boolean>, value: Boolean) = save(key, value)
    suspend fun saveLong(key: Preferences.Key<Long>, value: Long) = save(key, value)
    suspend fun saveDouble(key: Preferences.Key<Double>, value: Double) = save(key, value)
    suspend fun saveFloat(key: Preferences.Key<Float>, value: Float) = save(key, value)


    // Specific get functions for each type, for clarity
    suspend fun getString(key: Preferences.Key<String>): String? = get(key)
    suspend fun getInt(key: Preferences.Key<Int>): Int? = get(key)
    suspend fun getBoolean(key: Preferences.Key<Boolean>): Boolean? = get(key)
    suspend fun getLong(key: Preferences.Key<Long>): Long? = get(key)
    suspend fun getDouble(key: Preferences.Key<Double>): Double? = get(key)
    suspend fun getFloat(key: Preferences.Key<Float>): Float? = get(key)

    // Generic read function as flow
    fun <T> getAsFlow(key: Preferences.Key<T>): Flow<T?> = dataStore.data.map { preferences ->
        preferences[key]
    }

}
