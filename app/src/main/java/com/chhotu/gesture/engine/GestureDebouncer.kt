package com.chhotu.gesture.engine

import javax.inject.Inject

class GestureDebouncer @Inject constructor() {

    private var currentGestureId: String? = null
    private var firstSeenTimestamp: Long = 0L
    private var holdDurationMs: Long = 300L

    fun update(gestureId: String?, timestamp: Long): String? {
        if (gestureId == null) {
            reset()
            return null
        }

        if (gestureId != currentGestureId) {
            currentGestureId = gestureId
            firstSeenTimestamp = timestamp
            return null
        }

        val elapsed = timestamp - firstSeenTimestamp
        return if (elapsed >= holdDurationMs) gestureId else null
    }

    fun reset() {
        currentGestureId = null
        firstSeenTimestamp = 0L
    }
}
