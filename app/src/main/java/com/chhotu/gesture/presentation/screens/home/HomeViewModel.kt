package com.chhotu.gesture.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chhotu.gesture.domain.model.DashboardStats
import com.chhotu.gesture.domain.usecase.GetGestureStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class HomeUiState(
    val stats: DashboardStats? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGestureStatsUseCase: GetGestureStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        getGestureStatsUseCase()
            .onEach { stats ->
                _uiState.value = HomeUiState(stats = stats, isLoading = false)
            }
            .catch {
                _uiState.value = HomeUiState(stats = null, isLoading = false)
            }
            .launchIn(viewModelScope)
    }
}
