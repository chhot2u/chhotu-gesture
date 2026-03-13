package com.chhotu.gesture.di

import com.chhotu.gesture.data.repository.GestureRepositoryImpl
import com.chhotu.gesture.data.repository.StatsRepositoryImpl
import com.chhotu.gesture.domain.repository.GestureRepository
import com.chhotu.gesture.domain.repository.StatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGestureRepository(
        impl: GestureRepositoryImpl
    ): GestureRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(
        impl: StatsRepositoryImpl
    ): StatsRepository
}
