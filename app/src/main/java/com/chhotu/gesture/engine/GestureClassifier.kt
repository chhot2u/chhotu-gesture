package com.chhotu.gesture.engine

import com.chhotu.gesture.domain.model.DetectedGesture
import com.chhotu.gesture.domain.model.Gesture
import com.chhotu.gesture.domain.model.GestureCategory
import com.chhotu.gesture.domain.model.HandLandmark
import com.chhotu.gesture.domain.model.Handedness
import javax.inject.Inject

class GestureClassifier @Inject constructor(
    private val normalizer: LandmarkNormalizer,
    private val confidenceScorer: ConfidenceScorer
) {

    private data class GesturePattern(
        val gesture: Gesture,
        val thumbExtended: Boolean,
        val indexExtended: Boolean,
        val middleExtended: Boolean,
        val ringExtended: Boolean,
        val pinkyExtended: Boolean
    )

    private val patterns = listOf(
        GesturePattern(
            gesture = Gesture(id = "open_hand", name = "Open Hand", emoji = "\uD83E\uDD1A", description = "Open palm facing the camera", category = GestureCategory.BUILT_IN),
            thumbExtended = true, indexExtended = true, middleExtended = true,
            ringExtended = true, pinkyExtended = true
        ),
        GesturePattern(
            gesture = Gesture(id = "fist", name = "Fist", emoji = "✊", description = "Closed fist facing the camera", category = GestureCategory.BUILT_IN),
            thumbExtended = false, indexExtended = false, middleExtended = false,
            ringExtended = false, pinkyExtended = false
        ),
        GesturePattern(
            gesture = Gesture(id = "thumbs_up", name = "Thumbs Up", emoji = "\uD83D\uDC4D", description = "Thumb pointing upward", category = GestureCategory.BUILT_IN),
            thumbExtended = true, indexExtended = false, middleExtended = false,
            ringExtended = false, pinkyExtended = false
        ),
        GesturePattern(
            gesture = Gesture(id = "peace", name = "Peace", emoji = "✌\uFE0F", description = "Index and middle fingers raised", category = GestureCategory.BUILT_IN),
            thumbExtended = false, indexExtended = true, middleExtended = true,
            ringExtended = false, pinkyExtended = false
        ),
        GesturePattern(
            gesture = Gesture(id = "pointing", name = "Pointing", emoji = "\uD83D\uDC46", description = "Index finger pointing upward", category = GestureCategory.BUILT_IN),
            thumbExtended = false, indexExtended = true, middleExtended = false,
            ringExtended = false, pinkyExtended = false
        )
    )

    fun classify(landmarks: List<HandLandmark>): DetectedGesture? {
        if (landmarks.size < 21) return null

        val normalized = normalizer.normalize(landmarks)

        val thumbExt = isThumbExtended(normalized)
        val indexExt = isFingerExtended(
            normalized, HandLandmark.INDEX_TIP,
            HandLandmark.INDEX_PIP, HandLandmark.INDEX_MCP
        )
        val middleExt = isFingerExtended(
            normalized, HandLandmark.MIDDLE_TIP,
            HandLandmark.MIDDLE_PIP, HandLandmark.MIDDLE_MCP
        )
        val ringExt = isFingerExtended(
            normalized, HandLandmark.RING_TIP,
            HandLandmark.RING_PIP, HandLandmark.RING_MCP
        )
        val pinkyExt = isFingerExtended(
            normalized, HandLandmark.PINKY_TIP,
            HandLandmark.PINKY_PIP, HandLandmark.PINKY_MCP
        )

        val distances = patterns.map { pattern ->
            var dist = 0f
            if (pattern.thumbExtended != thumbExt) dist += 1f
            if (pattern.indexExtended != indexExt) dist += 1f
            if (pattern.middleExtended != middleExt) dist += 1f
            if (pattern.ringExtended != ringExt) dist += 1f
            if (pattern.pinkyExtended != pinkyExt) dist += 1f
            dist
        }

        val minDistance = distances.min()
        if (minDistance > 1f) return null

        val bestIndex = distances.indexOf(minDistance)
        val confidence = confidenceScorer.score(distances)

        return DetectedGesture(
            gesture = patterns[bestIndex].gesture,
            confidence = confidence,
            timestamp = System.currentTimeMillis(),
            handedness = Handedness.UNKNOWN,
            landmarks = landmarks
        )
    }

    private fun isFingerExtended(
        landmarks: List<HandLandmark>,
        fingerTip: Int,
        fingerPip: Int,
        fingerMcp: Int
    ): Boolean {
        val tip = landmarks[fingerTip]
        val pip = landmarks[fingerPip]
        val mcp = landmarks[fingerMcp]
        return tip.y < pip.y && pip.y < mcp.y
    }

    private fun isThumbExtended(landmarks: List<HandLandmark>): Boolean {
        val tip = landmarks[HandLandmark.THUMB_TIP]
        val ip = landmarks[HandLandmark.THUMB_IP]
        val mcp = landmarks[HandLandmark.THUMB_MCP]
        val tipDistFromCenter = kotlin.math.abs(tip.x)
        val mcpDistFromCenter = kotlin.math.abs(mcp.x)
        return tipDistFromCenter > mcpDistFromCenter && tipDistFromCenter > kotlin.math.abs(ip.x)
    }
}
