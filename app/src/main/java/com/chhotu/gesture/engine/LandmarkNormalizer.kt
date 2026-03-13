package com.chhotu.gesture.engine

import com.chhotu.gesture.domain.model.HandLandmark
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.sqrt

class LandmarkNormalizer @Inject constructor() {

    fun normalize(landmarks: List<HandLandmark>): List<HandLandmark> {
        if (landmarks.isEmpty()) return emptyList()

        val wrist = landmarks[HandLandmark.WRIST]
        val cx = wrist.x
        val cy = wrist.y
        val cz = wrist.z

        val centered = landmarks.map {
            it.copy(x = it.x - cx, y = it.y - cy, z = it.z - cz)
        }

        val maxDist = centered.maxOfOrNull { lm ->
            sqrt(lm.x * lm.x + lm.y * lm.y + lm.z * lm.z)
        } ?: 1f

        val scale = max(maxDist, 1e-6f)

        return centered.map {
            it.copy(x = it.x / scale, y = it.y / scale, z = it.z / scale)
        }
    }
}
