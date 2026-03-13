package com.chhotu.gesture.data.repository

import com.chhotu.gesture.data.local.db.dao.StatsDao
import com.chhotu.gesture.data.local.db.entity.GestureStatsEntity
import com.chhotu.gesture.domain.model.ActivityEntry
import com.chhotu.gesture.domain.model.DashboardStats
import com.chhotu.gesture.domain.model.GestureStats
import com.chhotu.gesture.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao
) : StatsRepository {

    override fun getDashboardStats(): Flow<DashboardStats> =
        combine(
            statsDao.getAllGestureStats(),
            statsDao.getRecentActivity(50),
            statsDao.getTotalDetections()
        ) { summaries, recentEntities, totalDetections ->
            val gestureStats = summaries.map { summary ->
                GestureStats(
                    gestureId = summary.gestureId,
                    gestureName = summary.gestureId,
                    totalDetections = summary.totalDetections,
                    successfulActions = summary.successfulActions,
                    averageConfidence = summary.averageConfidence,
                    lastUsedTimestamp = summary.lastUsedTimestamp
                )
            }
            val totalActions = summaries.sumOf { it.successfulActions }
            val avgAccuracy = if (summaries.isNotEmpty()) {
                summaries.map { it.averageConfidence }.average().toFloat()
            } else 0f
            val topGesture = summaries.maxByOrNull { it.totalDetections }?.gestureId

            val recentActivity = recentEntities.map { entity ->
                ActivityEntry(
                    gestureName = entity.gestureId,
                    actionName = if (entity.actionExecuted) "executed" else "detected",
                    confidence = entity.confidence,
                    timestamp = entity.timestamp
                )
            }

            DashboardStats(
                totalGesturesDetected = totalDetections,
                totalActionsExecuted = totalActions,
                averageAccuracy = avgAccuracy,
                topGesture = topGesture,
                gestureStats = gestureStats,
                recentActivity = recentActivity
            )
        }

    override fun getGestureStats(gestureId: String): Flow<GestureStats?> =
        statsDao.getStatsForGesture(gestureId).map { summary ->
            summary?.let {
                GestureStats(
                    gestureId = it.gestureId,
                    gestureName = it.gestureId,
                    totalDetections = it.totalDetections,
                    successfulActions = it.successfulActions,
                    averageConfidence = it.averageConfidence,
                    lastUsedTimestamp = it.lastUsedTimestamp
                )
            }
        }

    override fun getRecentActivity(limit: Int): Flow<List<ActivityEntry>> =
        statsDao.getRecentActivity(limit).map { entities ->
            entities.map { entity ->
                ActivityEntry(
                    gestureName = entity.gestureId,
                    actionName = if (entity.actionExecuted) "executed" else "detected",
                    confidence = entity.confidence,
                    timestamp = entity.timestamp
                )
            }
        }

    override suspend fun recordDetection(
        gestureId: String,
        confidence: Float,
        actionExecuted: Boolean
    ) {
        statsDao.insertStat(
            GestureStatsEntity(
                gestureId = gestureId,
                confidence = confidence,
                actionExecuted = actionExecuted,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clearStats() {
        statsDao.clearAll()
    }
}
