package com.chhotu.gesture.di

import android.content.Context
import androidx.room.Room
import com.chhotu.gesture.data.local.db.ChhotuDatabase
import com.chhotu.gesture.data.local.db.dao.GestureDao
import com.chhotu.gesture.data.local.db.dao.StatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideChhotuDatabase(
        @ApplicationContext context: Context
    ): ChhotuDatabase {
        return Room.databaseBuilder(
            context,
            ChhotuDatabase::class.java,
            "chhotu_gesture_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGestureDao(database: ChhotuDatabase): GestureDao {
        return database.gestureDao()
    }

    @Provides
    @Singleton
    fun provideStatsDao(database: ChhotuDatabase): StatsDao {
        return database.statsDao()
    }
}
