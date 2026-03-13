package com.chhotu.gesture.domain.usecase

import com.chhotu.gesture.domain.model.DashboardStats
import com.chhotu.gesture.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving dashboard statistics.
 */
class GetGestureStatsUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    operator fun invoke(): Flow<DashboardStats> {
        return statsRepository.getDashboardStats()
    }
}
