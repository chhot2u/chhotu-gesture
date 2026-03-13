package com.chhotu.gesture.presentation.screens.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggle(
            title = "Detection Enabled",
            checked = uiState.detectionEnabled,
            onCheckedChange = viewModel::setDetectionEnabled
        )

        HorizontalDivider()

        SettingsSlider(
            title = "FPS",
            value = uiState.fps.toFloat(),
            valueRange = 10f..30f,
            steps = 3,
            valueLabel = "${uiState.fps}",
            onValueChange = { viewModel.setFps(it.toInt()) }
        )

        HorizontalDivider()

        SettingsSlider(
            title = "Confidence Threshold",
            value = uiState.confidenceThreshold,
            valueRange = 0.5f..1.0f,
            steps = 9,
            valueLabel = "${(uiState.confidenceThreshold * 100).toInt()}%",
            onValueChange = viewModel::setConfidenceThreshold
        )

        HorizontalDivider()

        SettingsSlider(
            title = "Hold Duration",
            value = uiState.holdDuration.toFloat(),
            valueRange = 100f..1000f,
            steps = 8,
            valueLabel = "${uiState.holdDuration}ms",
            onValueChange = { viewModel.setHoldDuration(it.toLong()) }
        )

        HorizontalDivider()

        SettingsSlider(
            title = "Idle Timeout",
            value = uiState.idleTimeoutSeconds.toFloat(),
            valueRange = 3f..30f,
            steps = 8,
            valueLabel = "${uiState.idleTimeoutSeconds}s",
            onValueChange = { viewModel.setIdleTimeoutSeconds(it.toInt()) }
        )

        HorizontalDivider()

        SettingsToggle(
            title = "Adaptive FPS",
            checked = uiState.adaptiveFps,
            onCheckedChange = viewModel::setAdaptiveFps
        )

        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Setup Accessibility Service")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "About",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Chhotu Gesture v1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Control your phone with hand gestures",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsSlider(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueLabel: String,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
    }
}
