package com.chhotu.gesture.domain.usecase

import com.chhotu.gesture.domain.model.DetectedGesture
import com.chhotu.gesture.domain.model.HandLandmark
import com.chhotu.gesture.engine.GestureClassifier
import javax.inject.Inject

/**
 * Use case for detecting a gesture from hand landmarks.
 */
class DetectGestureUseCase @Inject constructor(
    private val classifier: GestureClassifier
) {
    suspend operator fun invoke(landmarks: List<HandLandmark>): DetectedGesture? {
        return classifier.classify(landmarks)
    }
}
