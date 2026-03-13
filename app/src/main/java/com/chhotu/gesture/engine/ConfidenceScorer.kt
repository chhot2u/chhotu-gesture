package com.chhotu.gesture.engine

import javax.inject.Inject
import kotlin.math.max

class ConfidenceScorer @Inject constructor() {

    fun score(distances: List<Float>): Float {
        if (distances.isEmpty()) return 0f

        val minDist = distances.min()
        if (minDist <= 0f) return 1f

        val weights = distances.map { 1f / max(it, 1e-6f) }
        val totalWeight = weights.sum()
        val bestWeight = weights.max()

        return (bestWeight / totalWeight).coerceIn(0f, 1f)
    }

    fun meetsThreshold(confidence: Float, threshold: Float): Boolean =
        confidence >= threshold
}
