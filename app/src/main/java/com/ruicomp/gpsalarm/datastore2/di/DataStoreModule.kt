package com.ruicomp.gpsalarm.datastore2.di

import com.ruicomp.gpsalarm.datastore2.DataStorePreferences
import com.ruicomp.gpsalarm.datastore2.dataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataStoreModule = module {
    single { DataStorePreferences.getInstance(androidContext().dataStore) }
}