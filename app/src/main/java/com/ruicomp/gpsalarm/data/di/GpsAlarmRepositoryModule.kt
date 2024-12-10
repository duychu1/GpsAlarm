package com.ruicomp.gpsalarm.data.di

import com.ruicomp.gpsalarm.data.fake.FakeGpsAlarmRepositoryImpl
import com.ruicomp.gpsalarm.data.repository.GpsAlarmRepository
import com.ruicomp.gpsalarm.data.repository.GpsAlarmRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GpsAlarmRepositoryModule {
    @Binds
    abstract fun bindGpsAlarmRepository(
        gpsAlarmRepositoryImpl: GpsAlarmRepositoryImpl
    ): GpsAlarmRepository
}