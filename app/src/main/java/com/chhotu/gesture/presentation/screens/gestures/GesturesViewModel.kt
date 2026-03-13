package com.chhotu.gesture.presentation.screens.gestures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chhotu.gesture.domain.model.DefaultActions
import com.chhotu.gesture.domain.model.Gesture
import com.chhotu.gesture.domain.model.GestureMapping
import com.chhotu.gesture.domain.usecase.ManageGesturesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GestureWithMapping(
    val gesture: Gesture,
    val mapping: GestureMapping?,
    val actionName: String
)

data class GestureListUiState(
    val gestures: List<GestureWithMapping> = emptyList(),
    val isLoading: Boolean = true,
    val selectedGesture: GestureWithMapping? = null,
    val showDetailSheet: Boolean = false
)

@HiltViewModel
class GesturesViewModel @Inject constructor(
    private val manageGesturesUseCase: ManageGesturesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GestureListUiState())
    val uiState: StateFlow<GestureListUiState> = _uiState.asStateFlow()

    private val allActions = DefaultActions.all()

    init {
        loadGestures()
    }

    private fun loadGestures() {
        combine(
            manageGesturesUseCase.getAllGestures(),
            manageGesturesUseCase.getAllMappings()
        ) { gestures, mappings ->
            gestures.map { gesture ->
                val mapping = mappings.find { it.gestureId == gesture.id }
                val actionName = mapping?.let { m ->
                    allActions.find { it.id == m.actionId }?.name
                } ?: "Not mapped"
                GestureWithMapping(gesture, mapping, actionName)
            }
        }.onEach { items ->
            _uiState.update { it.copy(gestures = items, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    fun toggleGesture(gesture: Gesture) {
        viewModelScope.launch {
            manageGesturesUseCase.toggleGesture(gesture)
        }
    }

    fun updateMapping(gestureId: String, actionId: String) {
        viewModelScope.launch {
            manageGesturesUseCase.updateMapping(
                GestureMapping(gestureId = gestureId, actionId = actionId, isEnabled = true)
            )
        }
    }

    fun selectGesture(gestureWithMapping: GestureWithMapping) {
        _uiState.update {
            it.copy(selectedGesture = gestureWithMapping, showDetailSheet = true)
        }
    }

    fun dismissDetailSheet() {
        _uiState.update { it.copy(showDetailSheet = false, selectedGesture = null) }
    }
}
