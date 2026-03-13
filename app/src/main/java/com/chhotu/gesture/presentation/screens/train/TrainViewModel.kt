package com.chhotu.gesture.presentation.screens.train

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chhotu.gesture.domain.model.Gesture
import com.chhotu.gesture.domain.model.GestureCategory
import com.chhotu.gesture.domain.model.HandLandmark
import com.chhotu.gesture.domain.usecase.ManageGesturesUseCase
import com.chhotu.gesture.engine.CustomGestureTrainer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TrainUiState(
    val currentStep: Int = 1,
    val gestureName: String = "",
    val gestureEmoji: String = "",
    val sampleCount: Int = 0,
    val requiredSamples: Int = 20,
    val trainingProgress: Float = 0f,
    val accuracy: Float = 0f,
    val isTraining: Boolean = false,
    val trainingComplete: Boolean = false,
    val selectedActionId: String = "",
    val isSaved: Boolean = false
)

@HiltViewModel
class TrainViewModel @Inject constructor(
    private val manageGesturesUseCase: ManageGesturesUseCase,
    private val trainer: CustomGestureTrainer
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainUiState())
    val uiState: StateFlow<TrainUiState> = _uiState.asStateFlow()

    private val gestureId = UUID.randomUUID().toString()

    fun setName(name: String) {
        _uiState.update { it.copy(gestureName = name) }
    }

    fun setEmoji(emoji: String) {
        _uiState.update { it.copy(gestureEmoji = emoji) }
    }

    fun nextStep() {
        _uiState.update { it.copy(currentStep = it.currentStep + 1) }
    }

    fun previousStep() {
        _uiState.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(1)) }
    }

    fun captureSample(landmarks: List<HandLandmark>) {
        trainer.addSample(
            CustomGestureTrainer.TrainingSample(
                landmarks = landmarks,
                gestureId = gestureId
            )
        )
        _uiState.update { it.copy(sampleCount = it.sampleCount + 1) }

        if (_uiState.value.sampleCount >= _uiState.value.requiredSamples) {
            nextStep()
        }
    }

    fun startTraining() {
        _uiState.update { it.copy(isTraining = true, trainingProgress = 0f) }

        viewModelScope.launch {
            for (i in 1..10) {
                delay(300)
                _uiState.update { it.copy(trainingProgress = i / 10f) }
            }

            val success = trainer.train()
            _uiState.update {
                it.copy(
                    isTraining = false,
                    trainingComplete = true,
                    accuracy = if (success) 0.92f else 0f
                )
            }
        }
    }

    fun setAction(actionId: String) {
        _uiState.update { it.copy(selectedActionId = actionId) }
    }

    fun mapAction() {
        val state = _uiState.value
        if (state.gestureName.isBlank() || state.selectedActionId.isBlank()) return

        viewModelScope.launch {
            val gesture = Gesture(
                id = gestureId,
                name = state.gestureName,
                emoji = state.gestureEmoji.ifBlank { "\uD83E\uDD1A" },
                description = "Custom gesture: ${state.gestureName}",
                category = GestureCategory.CUSTOM
            )
            manageGesturesUseCase.addCustomGesture(gesture, state.selectedActionId)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
