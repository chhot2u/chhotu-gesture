package com.chhotu.gesture.domain.usecase

import com.chhotu.gesture.domain.model.Gesture
import com.chhotu.gesture.domain.model.GestureMapping
import com.chhotu.gesture.domain.repository.GestureRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for managing gestures and their action mappings.
 */
class ManageGesturesUseCase @Inject constructor(
    private val repository: GestureRepository
) {
    fun getAllGestures(): Flow<List<Gesture>> = repository.getAllGestures()

    fun getAllMappings(): Flow<List<GestureMapping>> = repository.getAllMappings()

    suspend fun updateMapping(mapping: GestureMapping) {
        repository.upsertMapping(mapping)
    }

    suspend fun toggleGesture(gesture: Gesture) {
        repository.updateGesture(gesture.copy(isEnabled = !gesture.isEnabled))
    }

    suspend fun addCustomGesture(gesture: Gesture, actionId: String) {
        repository.insertGesture(gesture)
        repository.upsertMapping(
            GestureMapping(
                gestureId = gesture.id,
                actionId = actionId,
                isEnabled = true
            )
        )
    }

    suspend fun deleteCustomGesture(gestureId: String) {
        repository.deleteMapping(gestureId)
        repository.deleteGesture(gestureId)
    }

    suspend fun initializeDefaults() {
        repository.initializeDefaults()
    }
}
