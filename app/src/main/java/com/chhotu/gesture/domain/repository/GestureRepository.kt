package com.chhotu.gesture.domain.repository

import com.chhotu.gesture.domain.model.Gesture
import com.chhotu.gesture.domain.model.GestureMapping
import kotlinx.coroutines.flow.Flow

interface GestureRepository {
    fun getAllGestures(): Flow<List<Gesture>>
    fun getGestureById(id: String): Flow<Gesture?>
    suspend fun insertGesture(gesture: Gesture)
    suspend fun updateGesture(gesture: Gesture)
    suspend fun deleteGesture(id: String)
    fun getAllMappings(): Flow<List<GestureMapping>>
    suspend fun upsertMapping(mapping: GestureMapping)
    suspend fun deleteMapping(gestureId: String)
    suspend fun initializeDefaults()
}
