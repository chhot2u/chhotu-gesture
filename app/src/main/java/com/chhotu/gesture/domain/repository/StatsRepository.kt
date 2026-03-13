package com.chhotu.gesture.domain.repository

import com.chhotu.gesture.domain.model.ActivityEntry
import com.chhotu.gesture.domain.model.DashboardStats
import com.chhotu.gesture.domain.model.GestureStats
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun getDashboardStats(): Flow<DashboardStats>
    fun getGestureStats(gestureId: String): Flow<GestureStats?>
    fun getRecentActivity(limit: Int = 50): Flow<List<ActivityEntry>>
    suspend fun recordDetection(gestureId: String, confidence: Float, actionExecuted: Boolean)
    suspend fun clearStats()
}
