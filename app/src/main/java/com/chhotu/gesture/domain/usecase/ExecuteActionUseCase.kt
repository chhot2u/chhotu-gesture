package com.chhotu.gesture.domain.usecase

import com.chhotu.gesture.actions.ActionExecutor
import com.chhotu.gesture.domain.model.DetectedGesture
import com.chhotu.gesture.domain.model.GestureMapping
import com.chhotu.gesture.domain.repository.GestureRepository
import com.chhotu.gesture.domain.repository.StatsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for executing the action mapped to a detected gesture.
 */
class ExecuteActionUseCase @Inject constructor(
    private val gestureRepository: GestureRepository,
    private val statsRepository: StatsRepository,
    private val actionExecutor: ActionExecutor
) {
    suspend operator fun invoke(detectedGesture: DetectedGesture): Boolean {
        val mappings = gestureRepository.getAllMappings().first()
        val mapping = mappings.find {
            it.gestureId == detectedGesture.gesture.id && it.isEnabled
        } ?: return false

        val executed = actionExecutor.execute(mapping.actionId)

        statsRepository.recordDetection(
            gestureId = detectedGesture.gesture.id,
            confidence = detectedGesture.confidence,
            actionExecuted = executed
        )

        return executed
    }
}
