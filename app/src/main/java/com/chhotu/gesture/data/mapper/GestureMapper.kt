package com.chhotu.gesture.data.mapper

import com.chhotu.gesture.data.local.db.entity.ActionMappingEntity
import com.chhotu.gesture.data.local.db.entity.GestureEntity
import com.chhotu.gesture.domain.model.Gesture
import com.chhotu.gesture.domain.model.GestureCategory
import com.chhotu.gesture.domain.model.GestureMapping

fun GestureEntity.toDomain(): Gesture = Gesture(
    id = id,
    name = name,
    emoji = emoji,
    description = description,
    category = GestureCategory.valueOf(category),
    confidenceThreshold = confidenceThreshold,
    holdDurationMs = holdDurationMs,
    isEnabled = isEnabled
)

fun Gesture.toEntity(): GestureEntity = GestureEntity(
    id = id,
    name = name,
    emoji = emoji,
    description = description,
    category = category.name,
    confidenceThreshold = confidenceThreshold,
    holdDurationMs = holdDurationMs,
    isEnabled = isEnabled
)

fun ActionMappingEntity.toDomain(): GestureMapping = GestureMapping(
    gestureId = gestureId,
    actionId = actionId,
    isEnabled = isEnabled
)

fun GestureMapping.toEntity(): ActionMappingEntity = ActionMappingEntity(
    gestureId = gestureId,
    actionId = actionId,
    isEnabled = isEnabled
)
