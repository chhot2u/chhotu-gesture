package com.chhotu.gesture.presentation.screens.gestures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chhotu.gesture.domain.model.DefaultActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestureDetailContent(
    gestureWithMapping: GestureWithMapping,
    onUpdateMapping: (String) -> Unit,
    onToggle: () -> Unit
) {
    val gesture = gestureWithMapping.gesture
    val allActions = DefaultActions.all()
    var confidenceThreshold by remember { mutableFloatStateOf(gesture.confidenceThreshold) }
    var holdDuration by remember { mutableFloatStateOf(gesture.holdDurationMs.toFloat()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf(gestureWithMapping.actionName) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "${gesture.emoji} ${gesture.name}",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = gesture.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column {
            Text(
                text = "Confidence Threshold: ${(confidenceThreshold * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge
            )
            Slider(
                value = confidenceThreshold,
                onValueChange = { confidenceThreshold = it },
                valueRange = 0.5f..1.0f,
                steps = 9
            )
        }

        Column {
            Text(
                text = "Hold Duration: ${holdDuration.toInt()}ms",
                style = MaterialTheme.typography.labelLarge
            )
            Slider(
                value = holdDuration,
                onValueChange = { holdDuration = it },
                valueRange = 100f..1000f,
                steps = 8
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = selectedAction,
                onValueChange = {},
                readOnly = true,
                label = { Text("Mapped Action") },
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
                            selectedAction = action.name
                            onUpdateMapping(action.id)
                            expanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enabled",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = gesture.isEnabled,
                onCheckedChange = { onToggle() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
