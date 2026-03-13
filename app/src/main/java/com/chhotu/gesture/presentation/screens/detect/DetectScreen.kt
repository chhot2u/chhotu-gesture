package com.chhotu.gesture.presentation.screens.detect

import android.view.MotionEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chhotu.gesture.presentation.components.ConfidenceMeter
import com.chhotu.gesture.presentation.components.GestureOverlay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DetectScreen(viewModel: DetectViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (uiState.isDetecting) viewModel.stopDetection()
                    else viewModel.startDetection()
                }
            ) {
                Icon(
                    imageVector = if (uiState.isDetecting) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                    contentDescription = if (uiState.isDetecting) "Stop" else "Start"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                /*
                 * FIX: Detect user touch at the root level.
                 * When user touches the screen, notify ViewModel to suppress
                 * gesture action execution (prevents conflicts with real touches).
                 * Returns false so touch events PASS THROUGH to children.
                 */
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            viewModel.onUserTouchDown()
                        }
                        MotionEvent.ACTION_UP,
                        MotionEvent.ACTION_CANCEL -> {
                            viewModel.onUserTouchUp()
                        }
                    }
                    // CRITICAL: Return false to NOT consume the event
                    // This allows touches to pass through to buttons, cards, etc.
                    false
                }
        ) {
            // Layer 1 (bottom): Camera preview
            CameraPreview(
                onImageAnalyzed = { imageProxy ->
                    imageProxy.close()
                },
                modifier = Modifier.fillMaxSize()
            )

            // Layer 2 (middle): Gesture overlay — touch-transparent (uses graphicsLayer)
            uiState.lastDetectedGesture?.let { detected ->
                GestureOverlay(
                    landmarks = detected.landmarks,
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)  // FIX: Explicit z-index for visual layer
                )
            }

            // Layer 3 (top): Interactive UI — always receives touches
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .zIndex(2f),  // FIX: Higher z-index ensures touch reaches card
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.lastDetectedGesture?.gesture?.let {
                                "${it.emoji} ${it.name}"
                            } ?: "No gesture detected",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Confidence: ${(uiState.confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        uiState.lastActionName?.let { action ->
                            Text(
                                text = "Action: $action",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        // FIX: Show touch guard status
                        if (uiState.isUserTouching) {
                            Text(
                                text = "⏸ Actions paused (touching screen)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    ConfidenceMeter(confidence = uiState.confidence)
                }
            }
        }
    }
}
