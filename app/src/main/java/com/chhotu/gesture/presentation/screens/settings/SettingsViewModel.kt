package com.chhotu.gesture.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chhotu.gesture.data.local.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val detectionEnabled: Boolean = false,
    val fps: Int = 30,
    val confidenceThreshold: Float = 0.85f,
    val holdDuration: Long = 300L,
    val idleTimeoutSeconds: Int = 5,
    val adaptiveFps: Boolean = true,
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        combine(
            settingsDataStore.detectionEnabled,
            settingsDataStore.fps,
            settingsDataStore.confidenceThreshold,
            settingsDataStore.holdDuration,
            settingsDataStore.idleTimeoutSeconds,
            settingsDataStore.adaptiveFps
        ) { values ->
            SettingsUiState(
                detectionEnabled = values[0] as Boolean,
                fps = values[1] as Int,
                confidenceThreshold = values[2] as Float,
                holdDuration = values[3] as Long,
                idleTimeoutSeconds = values[4] as Int,
                adaptiveFps = values[5] as Boolean,
                isLoading = false
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun setDetectionEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setDetectionEnabled(enabled) }
    }

    fun setFps(fps: Int) {
        viewModelScope.launch { settingsDataStore.setFps(fps) }
    }

    fun setConfidenceThreshold(threshold: Float) {
        viewModelScope.launch { settingsDataStore.setConfidenceThreshold(threshold) }
    }

    fun setHoldDuration(duration: Long) {
        viewModelScope.launch { settingsDataStore.setHoldDuration(duration) }
    }

    fun setIdleTimeoutSeconds(seconds: Int) {
        viewModelScope.launch { settingsDataStore.setIdleTimeoutSeconds(seconds) }
    }

    fun setAdaptiveFps(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setAdaptiveFps(enabled) }
    }
}
