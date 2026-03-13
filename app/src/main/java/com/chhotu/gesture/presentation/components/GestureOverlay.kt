package com.chhotu.gesture.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import com.chhotu.gesture.domain.model.HandLandmark

private val FINGER_CONNECTIONS = listOf(
    HandLandmark.WRIST to HandLandmark.THUMB_CMC,
    HandLandmark.THUMB_CMC to HandLandmark.THUMB_MCP,
    HandLandmark.THUMB_MCP to HandLandmark.THUMB_IP,
    HandLandmark.THUMB_IP to HandLandmark.THUMB_TIP,
    HandLandmark.WRIST to HandLandmark.INDEX_MCP,
    HandLandmark.INDEX_MCP to HandLandmark.INDEX_PIP,
    HandLandmark.INDEX_PIP to HandLandmark.INDEX_DIP,
    HandLandmark.INDEX_DIP to HandLandmark.INDEX_TIP,
    HandLandmark.WRIST to HandLandmark.MIDDLE_MCP,
    HandLandmark.MIDDLE_MCP to HandLandmark.MIDDLE_PIP,
    HandLandmark.MIDDLE_PIP to HandLandmark.MIDDLE_DIP,
    HandLandmark.MIDDLE_DIP to HandLandmark.MIDDLE_TIP,
    HandLandmark.WRIST to HandLandmark.RING_MCP,
    HandLandmark.RING_MCP to HandLandmark.RING_PIP,
    HandLandmark.RING_PIP to HandLandmark.RING_DIP,
    HandLandmark.RING_DIP to HandLandmark.RING_TIP,
    HandLandmark.WRIST to HandLandmark.PINKY_MCP,
    HandLandmark.PINKY_MCP to HandLandmark.PINKY_PIP,
    HandLandmark.PINKY_PIP to HandLandmark.PINKY_DIP,
    HandLandmark.PINKY_DIP to HandLandmark.PINKY_TIP,
    HandLandmark.INDEX_MCP to HandLandmark.MIDDLE_MCP,
    HandLandmark.MIDDLE_MCP to HandLandmark.RING_MCP,
    HandLandmark.RING_MCP to HandLandmark.PINKY_MCP
)

/**
 * Draws hand landmarks on a Canvas overlay.
 *
 * FIX: Uses graphicsLayer {} to make this composable completely touch-transparent.
 * The overlay is visual-only — all touch events pass through to layers below.
 */
@Composable
fun GestureOverlay(
    landmarks: List<HandLandmark>,
    modifier: Modifier = Modifier,
    pointColor: Color = Color.Cyan,
    lineColor: Color = Color.Green
) {
    if (landmarks.isEmpty()) return

    val landmarkMap = landmarks.associateBy { it.index }

    Canvas(
        modifier = modifier
            // FIX: graphicsLayer makes this render-only — zero touch interception
            .graphicsLayer { }
    ) {
        val w = size.width
        val h = size.height

        for ((startIdx, endIdx) in FINGER_CONNECTIONS) {
            val start = landmarkMap[startIdx] ?: continue
            val end = landmarkMap[endIdx] ?: continue
            drawLine(
                color = lineColor,
                start = Offset(start.x * w, start.y * h),
                end = Offset(end.x * w, end.y * h),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }

        for (landmark in landmarks) {
            drawCircle(
                color = pointColor,
                radius = 8f,
                center = Offset(landmark.x * w, landmark.y * h)
            )
        }
    }
}
