package com.chhotu.gesture.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "action_mappings")
data class ActionMappingEntity(
    @PrimaryKey val gestureId: String,
    val actionId: String,
    val isEnabled: Boolean
)
