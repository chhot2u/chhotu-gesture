package com.chhotu.gesture.presentation.screens.gestures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chhotu.gesture.presentation.components.GestureCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesturesScreen(viewModel: GesturesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Gestures",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(uiState.gestures, key = { it.gesture.id }) { item ->
            GestureCard(
                gesture = item.gesture,
                actionName = item.actionName,
                isEnabled = item.gesture.isEnabled,
                onToggle = { viewModel.toggleGesture(item.gesture) },
                onClick = { viewModel.selectGesture(item) }
            )
        }
    }

    if (uiState.showDetailSheet && uiState.selectedGesture != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissDetailSheet() },
            sheetState = sheetState
        ) {
            GestureDetailContent(
                gestureWithMapping = uiState.selectedGesture!!,
                onUpdateMapping = { actionId ->
                    viewModel.updateMapping(uiState.selectedGesture!!.gesture.id, actionId)
                },
                onToggle = { viewModel.toggleGesture(uiState.selectedGesture!!.gesture) }
            )
        }
    }
}
