package com.ruicomp.gpsalarm.data.di

import com.ruicomp.gpsalarm.data.GpsAlarmRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GpsAlarmModule {

    @Provides
    fun provideGpsAlarmRepoImpl(): GpsAlarmRepoImpl {
        return GpsAlarmRepoImpl()
    }
}