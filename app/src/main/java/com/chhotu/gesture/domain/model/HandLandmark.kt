package com.chhotu.gesture.domain.model

/**
 * Represents a single hand landmark point detected by MediaPipe.
 * MediaPipe Hands detects 21 landmarks per hand.
 *
 * @param x Normalized x-coordinate [0.0, 1.0]
 * @param y Normalized y-coordinate [0.0, 1.0]
 * @param z Depth relative to wrist (smaller = closer to camera)
 * @param index Landmark index (0-20) per MediaPipe hand landmark model
 */
data class HandLandmark(
    val x: Float,
    val y: Float,
    val z: Float,
    val index: Int
) {
    companion object {
        const val WRIST = 0
        const val THUMB_CMC = 1
        const val THUMB_MCP = 2
        const val THUMB_IP = 3
        const val THUMB_TIP = 4
        const val INDEX_MCP = 5
        const val INDEX_PIP = 6
        const val INDEX_DIP = 7
        const val INDEX_TIP = 8
        const val MIDDLE_MCP = 9
        const val MIDDLE_PIP = 10
        const val MIDDLE_DIP = 11
        const val MIDDLE_TIP = 12
        const val RING_MCP = 13
        const val RING_PIP = 14
        const val RING_DIP = 15
        const val RING_TIP = 16
        const val PINKY_MCP = 17
        const val PINKY_PIP = 18
        const val PINKY_DIP = 19
        const val PINKY_TIP = 20

        const val TOTAL_LANDMARKS = 21
    }
}
