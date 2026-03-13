package com.chhotu.gesture.domain.model

/**
 * Represents a gesture definition — either built-in or custom-trained.
 */
data class Gesture(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val category: GestureCategory,
    val confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
    val holdDurationMs: Long = DEFAULT_HOLD_DURATION_MS,
    val isEnabled: Boolean = true
) {
    companion object {
        const val DEFAULT_CONFIDENCE_THRESHOLD = 0.85f
        const val DEFAULT_HOLD_DURATION_MS = 300L
    }
}

enum class GestureCategory {
    BUILT_IN,
    CUSTOM
}
