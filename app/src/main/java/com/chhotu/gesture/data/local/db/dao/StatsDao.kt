package com.chhotu.gesture.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chhotu.gesture.data.local.db.entity.GestureStatsEntity
import kotlinx.coroutines.flow.Flow

data class GestureStatsSummary(
    val gestureId: String,
    val totalDetections: Long,
    val successfulActions: Long,
    val averageConfidence: Float,
    val lastUsedTimestamp: Long
)

@Dao
interface StatsDao {

    @Insert
    suspend fun insertStat(stat: GestureStatsEntity)

    @Query(
        """
        SELECT gestureId,
               COUNT(*) AS totalDetections,
               SUM(CASE WHEN actionExecuted = 1 THEN 1 ELSE 0 END) AS successfulActions,
               AVG(confidence) AS averageConfidence,
               MAX(timestamp) AS lastUsedTimestamp
        FROM gesture_stats
        WHERE gestureId = :gestureId
        GROUP BY gestureId
        """
    )
    fun getStatsForGesture(gestureId: String): Flow<GestureStatsSummary?>

    @Query(
        """
        SELECT gestureId,
               COUNT(*) AS totalDetections,
               SUM(CASE WHEN actionExecuted = 1 THEN 1 ELSE 0 END) AS successfulActions,
               AVG(confidence) AS averageConfidence,
               MAX(timestamp) AS lastUsedTimestamp
        FROM gesture_stats
        GROUP BY gestureId
        """
    )
    fun getAllGestureStats(): Flow<List<GestureStatsSummary>>

    @Query("SELECT * FROM gesture_stats ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentActivity(limit: Int): Flow<List<GestureStatsEntity>>

    @Query("SELECT COUNT(*) FROM gesture_stats")
    fun getTotalDetections(): Flow<Long>

    @Query("DELETE FROM gesture_stats")
    suspend fun clearAll()
}
