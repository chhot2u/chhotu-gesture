package com.chhotu.gesture.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gestures")
data class GestureEntity(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val category: String,
    val confidenceThreshold: Float,
    val holdDurationMs: Long,
    val isEnabled: Boolean
)
