package com.chhotu.gesture.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gesture_stats",
    indices = [Index(value = ["gestureId"])]
)
data class GestureStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "gestureId") val gestureId: String,
    val confidence: Float,
    val actionExecuted: Boolean,
    val timestamp: Long
)
