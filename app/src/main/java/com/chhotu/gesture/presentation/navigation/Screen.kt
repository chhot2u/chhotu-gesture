package com.chhotu.gesture.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        icon = Icons.Filled.Home
    )

    data object Detect : Screen(
        route = "detect",
        title = "Detect",
        icon = Icons.Filled.RadioButtonChecked
    )

    data object Gestures : Screen(
        route = "gestures",
        title = "Gestures",
        icon = Icons.Filled.TouchApp
    )

    data object Train : Screen(
        route = "train",
        title = "Train",
        icon = Icons.Filled.FitnessCenter
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = Icons.Filled.Settings
    )

    companion object {
        val bottomNavItems = listOf(Home, Detect, Gestures, Train, Settings)
    }
}
