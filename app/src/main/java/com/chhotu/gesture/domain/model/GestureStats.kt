package com.chhotu.gesture.domain.model

/**
 * Usage statistics for a gesture.
 */
data class GestureStats(
    val gestureId: String,
    val gestureName: String,
    val totalDetections: Long,
    val successfulActions: Long,
    val averageConfidence: Float,
    val lastUsedTimestamp: Long
) {
    val successRate: Float
        get() = if (totalDetections > 0) {
            successfulActions.toFloat() / totalDetections.toFloat()
        } else 0f
}

/**
 * Aggregated stats for the dashboard.
 */
data class DashboardStats(
    val totalGesturesDetected: Long,
    val totalActionsExecuted: Long,
    val averageAccuracy: Float,
    val topGesture: String?,
    val gestureStats: List<GestureStats>,
    val recentActivity: List<ActivityEntry>
)

data class ActivityEntry(
    val gestureName: String,
    val actionName: String,
    val confidence: Float,
    val timestamp: Long
)
