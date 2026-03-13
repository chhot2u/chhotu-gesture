package com.chhotu.gesture.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chhotu_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DETECTION_ENABLED = booleanPreferencesKey("detection_enabled")
        val FPS = intPreferencesKey("fps")
        val CONFIDENCE_THRESHOLD = floatPreferencesKey("confidence_threshold")
        val HOLD_DURATION = longPreferencesKey("hold_duration")
        val IDLE_TIMEOUT_SECONDS = intPreferencesKey("idle_timeout_seconds")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val ADAPTIVE_FPS = booleanPreferencesKey("adaptive_fps")
    }

    val detectionEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.DETECTION_ENABLED] ?: false }

    val fps: Flow<Int> = context.dataStore.data
        .map { it[Keys.FPS] ?: 30 }

    val confidenceThreshold: Flow<Float> = context.dataStore.data
        .map { it[Keys.CONFIDENCE_THRESHOLD] ?: 0.85f }

    val holdDuration: Flow<Long> = context.dataStore.data
        .map { it[Keys.HOLD_DURATION] ?: 300L }

    val idleTimeoutSeconds: Flow<Int> = context.dataStore.data
        .map { it[Keys.IDLE_TIMEOUT_SECONDS] ?: 5 }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.IS_FIRST_LAUNCH] ?: true }

    val adaptiveFps: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.ADAPTIVE_FPS] ?: true }

    suspend fun setDetectionEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DETECTION_ENABLED] = enabled }
    }

    suspend fun setFps(fps: Int) {
        context.dataStore.edit { it[Keys.FPS] = fps }
    }

    suspend fun setConfidenceThreshold(threshold: Float) {
        context.dataStore.edit { it[Keys.CONFIDENCE_THRESHOLD] = threshold }
    }

    suspend fun setHoldDuration(duration: Long) {
        context.dataStore.edit { it[Keys.HOLD_DURATION] = duration }
    }

    suspend fun setIdleTimeoutSeconds(seconds: Int) {
        context.dataStore.edit { it[Keys.IDLE_TIMEOUT_SECONDS] = seconds }
    }

    suspend fun setFirstLaunch(isFirst: Boolean) {
        context.dataStore.edit { it[Keys.IS_FIRST_LAUNCH] = isFirst }
    }

    suspend fun setAdaptiveFps(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ADAPTIVE_FPS] = enabled }
    }
}
