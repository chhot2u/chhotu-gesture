package com.chhotu.gesture.presentation.screens.detect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chhotu.gesture.domain.model.DetectedGesture
import com.chhotu.gesture.domain.model.HandLandmark
import com.chhotu.gesture.domain.usecase.DetectGestureUseCase
import com.chhotu.gesture.domain.usecase.ExecuteActionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetectUiState(
    val isDetecting: Boolean = false,
    val lastDetectedGesture: DetectedGesture? = null,
    val lastActionName: String? = null,
    val fps: Int = 30,
    val confidence: Float = 0f,
    val isServiceRunning: Boolean = false,
    val isUserTouching: Boolean = false
)

@HiltViewModel
class DetectViewModel @Inject constructor(
    private val detectGestureUseCase: DetectGestureUseCase,
    private val executeActionUseCase: ExecuteActionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetectUiState())
    val uiState: StateFlow<DetectUiState> = _uiState.asStateFlow()

    private var touchCooldownJob: Job? = null

    companion object {
        /** How long after last touch-up to resume gesture actions (ms) */
        private const val TOUCH_COOLDOWN_MS = 500L
    }

    fun startDetection() {
        _uiState.update { it.copy(isDetecting = true) }
    }

    fun stopDetection() {
        _uiState.update {
            it.copy(
                isDetecting = false,
                lastDetectedGesture = null,
                lastActionName = null,
                confidence = 0f
            )
        }
    }

    /**
     * FIX: Called when user touches the screen.
     * Suppresses action execution during touch to prevent conflicts.
     */
    fun onUserTouchDown() {
        touchCooldownJob?.cancel()
        _uiState.update { it.copy(isUserTouching = true) }
    }

    /**
     * FIX: Called when user lifts finger.
     * Starts a cooldown before re-enabling gesture actions.
     */
    fun onUserTouchUp() {
        touchCooldownJob?.cancel()
        touchCooldownJob = viewModelScope.launch {
            delay(TOUCH_COOLDOWN_MS)
            _uiState.update { it.copy(isUserTouching = false) }
        }
    }

    fun onLandmarksDetected(landmarks: List<HandLandmark>) {
        if (!_uiState.value.isDetecting) return

        viewModelScope.launch {
            // Always detect gesture (for visual feedback)
            val detected = detectGestureUseCase(landmarks) ?: return@launch
            _uiState.update {
                it.copy(
                    lastDetectedGesture = detected,
                    confidence = detected.confidence
                )
            }

            // FIX: Only execute actions if user is NOT touching the screen
            if (!_uiState.value.isUserTouching) {
                val executed = executeActionUseCase(detected)
                if (executed) {
                    _uiState.update {
                        it.copy(lastActionName = detected.gesture.name)
                    }
                }
            }
        }
    }
}
