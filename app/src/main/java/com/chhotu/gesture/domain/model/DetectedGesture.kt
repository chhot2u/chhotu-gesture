package com.chhotu.gesture.domain.model

/**
 * Represents a gesture that was detected in a camera frame.
 */
data class DetectedGesture(
    val gesture: Gesture,
    val confidence: Float,
    val timestamp: Long,
    val handedness: Handedness,
    val landmarks: List<HandLandmark>
)

enum class Handedness {
    LEFT,
    RIGHT,
    UNKNOWN
}
