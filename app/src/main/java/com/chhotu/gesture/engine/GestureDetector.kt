package com.chhotu.gesture.engine

import com.chhotu.gesture.domain.model.DetectedGesture
import com.chhotu.gesture.domain.model.HandLandmark
import javax.inject.Inject

class GestureDetector @Inject constructor(
    private val classifier: GestureClassifier,
    private val debouncer: GestureDebouncer
) {

    fun processLandmarks(landmarks: List<HandLandmark>): DetectedGesture? {
        val detected = classifier.classify(landmarks)
        val timestamp = System.currentTimeMillis()
        val confirmedId = debouncer.update(detected?.gesture?.id, timestamp)

        return if (confirmedId != null && detected != null && detected.gesture.id == confirmedId) {
            detected
        } else {
            null
        }
    }

    /**
     * Called by the foreground detection service on each frame tick.
     * Returns true if a hand was detected (regardless of gesture match).
     */
    fun detectFrame(): Boolean {
        // Stub: actual camera frame processing is handled via HandTracker callbacks.
        // This returns false when no hand landmarks are available.
        return false
    }

    fun reset() {
        debouncer.reset()
    }
}
