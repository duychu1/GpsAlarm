package com.ruicomp.gpsalarm.database.di

import android.content.Context
import androidx.room.Room
import com.google.android.datatransport.runtime.dagger.Provides
import com.ruicomp.gpsalarm.database.GpsAlarmDao
import com.ruicomp.gpsalarm.database.GpsAlarmDatabase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGpsAlarmDatabase(@ApplicationContext appContext: Context): GpsAlarmDatabase {
        return Room.databaseBuilder(
            appContext,
            GpsAlarmDatabase::class.java,
            "gps_alarm_database"
        ).build()
    }

    @Provides
    fun provideGpsAlarmDao(database: GpsAlarmDatabase): GpsAlarmDao {
        return database.gpsAlarmDao()
    }
}