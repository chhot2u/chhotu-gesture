package com.chhotu.gesture.di

import com.chhotu.gesture.actions.ActionExecutor
import com.chhotu.gesture.actions.ActionRegistry
import com.chhotu.gesture.engine.ConfidenceScorer
import com.chhotu.gesture.engine.GestureClassifier
import com.chhotu.gesture.engine.GestureDebouncer
import com.chhotu.gesture.engine.LandmarkNormalizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @Provides
    @Singleton
    fun provideLandmarkNormalizer(): LandmarkNormalizer {
        return LandmarkNormalizer()
    }

    @Provides
    @Singleton
    fun provideConfidenceScorer(): ConfidenceScorer {
        return ConfidenceScorer()
    }

    @Provides
    @Singleton
    fun provideGestureClassifier(
        landmarkNormalizer: LandmarkNormalizer,
        confidenceScorer: ConfidenceScorer
    ): GestureClassifier {
        return GestureClassifier(landmarkNormalizer, confidenceScorer)
    }

    @Provides
    @Singleton
    fun provideGestureDebouncer(): GestureDebouncer {
        return GestureDebouncer()
    }

    @Provides
    @Singleton
    fun provideActionRegistry(): ActionRegistry {
        return ActionRegistry()
    }

    @Provides
    @Singleton
    fun provideActionExecutor(
        actionRegistry: ActionRegistry
    ): ActionExecutor {
        return ActionExecutor(actionRegistry)
    }
}
