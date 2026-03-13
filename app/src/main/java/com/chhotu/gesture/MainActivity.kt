package com.chhotu.gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.chhotu.gesture.presentation.navigation.ChhotuNavHost
import com.chhotu.gesture.presentation.theme.ChhotuGestureTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChhotuGestureTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ChhotuNavHost()
                }
            }
        }
    }
}
