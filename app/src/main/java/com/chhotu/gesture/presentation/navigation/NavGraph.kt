package com.chhotu.gesture.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chhotu.gesture.presentation.screens.detect.DetectScreen
import com.chhotu.gesture.presentation.screens.detect.DetectViewModel
import com.chhotu.gesture.presentation.screens.gestures.GesturesScreen
import com.chhotu.gesture.presentation.screens.gestures.GesturesViewModel
import com.chhotu.gesture.presentation.screens.home.HomeScreen
import com.chhotu.gesture.presentation.screens.home.HomeViewModel
import com.chhotu.gesture.presentation.screens.settings.SettingsScreen
import com.chhotu.gesture.presentation.screens.settings.SettingsViewModel
import com.chhotu.gesture.presentation.screens.train.TrainScreen
import com.chhotu.gesture.presentation.screens.train.TrainViewModel

@Composable
fun ChhotuNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Screen.bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(viewModel = viewModel)
            }
            composable(Screen.Detect.route) {
                val viewModel: DetectViewModel = hiltViewModel()
                DetectScreen(viewModel = viewModel)
            }
            composable(Screen.Gestures.route) {
                val viewModel: GesturesViewModel = hiltViewModel()
                GesturesScreen(viewModel = viewModel)
            }
            composable(Screen.Train.route) {
                val viewModel: TrainViewModel = hiltViewModel()
                TrainScreen(viewModel = viewModel)
            }
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
