package com.chhotu.gesture.presentation.screens.train

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chhotu.gesture.domain.model.DefaultActions
import com.chhotu.gesture.presentation.screens.detect.CameraPreview

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrainScreen(viewModel: TrainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            StepIndicator(currentStep = uiState.currentStep, totalSteps = 4)
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                },
                label = "step_transition"
            ) { step ->
                when (step) {
                    1 -> StepNameEmoji(
                        name = uiState.gestureName,
                        emoji = uiState.gestureEmoji,
                        onNameChange = viewModel::setName,
                        onEmojiChange = viewModel::setEmoji,
                        onNext = viewModel::nextStep
                    )
                    2 -> StepCaptureSamples(
                        sampleCount = uiState.sampleCount,
                        requiredSamples = uiState.requiredSamples,
                        onCapture = viewModel::captureSample
                    )
                    3 -> StepTraining(
                        progress = uiState.trainingProgress,
                        accuracy = uiState.accuracy,
                        isTraining = uiState.isTraining,
                        trainingComplete = uiState.trainingComplete,
                        onStartTraining = viewModel::startTraining,
                        onNext = viewModel::nextStep
                    )
                    4 -> StepMapAction(
                        selectedActionId = uiState.selectedActionId,
                        isSaved = uiState.isSaved,
                        onSelectAction = viewModel::setAction,
                        onSave = viewModel::mapAction
                    )
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 1..totalSteps) {
            val color = if (i <= currentStep) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
            Text(
                text = "$i",
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
        }
    }
}

@Composable
private fun StepNameEmoji(
    name: String,
    emoji: String,
    onNameChange: (String) -> Unit,
    onEmojiChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Name Your Gesture",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Gesture Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = emoji,
            onValueChange = onEmojiChange,
            label = { Text("Emoji") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNext,
            enabled = name.isNotBlank()
        ) {
            Text("Next")
        }
    }
}

@Composable
private fun StepCaptureSamples(
    sampleCount: Int,
    requiredSamples: Int,
    onCapture: (List<com.chhotu.gesture.domain.model.HandLandmark>) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Capture Samples: $sampleCount / $requiredSamples",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { sampleCount.toFloat() / requiredSamples },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CameraPreview(
                onImageAnalyzed = { imageProxy ->
                    imageProxy.close()
                }
            )

            FloatingActionButton(
                onClick = { onCapture(emptyList()) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Capture")
            }
        }
    }
}

@Composable
private fun StepTraining(
    progress: Float,
    accuracy: Float,
    isTraining: Boolean,
    trainingComplete: Boolean,
    onStartTraining: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Training",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (isTraining) {
            CircularProgressIndicator(modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${(progress * 100).toInt()}%")
        } else if (trainingComplete) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Complete",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Accuracy: ${(accuracy * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNext) {
                Text("Next")
            }
        } else {
            Text(
                text = "Ready to train your custom gesture model.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onStartTraining) {
                Text("Start Training")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepMapAction(
    selectedActionId: String,
    isSaved: Boolean,
    onSelectAction: (String) -> Unit,
    onSave: () -> Unit
) {
    val allActions = DefaultActions.all()
    var expanded by remember { mutableStateOf(false) }
    val selectedName = allActions.find { it.id == selectedActionId }?.name ?: "Select an action"

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Map Action",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Action") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                allActions.forEach { action ->
                    DropdownMenuItem(
                        text = { Text(action.name) },
                        onClick = {
                            onSelectAction(action.id)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isSaved) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Saved",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gesture saved!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Button(
                onClick = onSave,
                enabled = selectedActionId.isNotBlank()
            ) {
                Text("Save Gesture")
            }
        }
    }
}
