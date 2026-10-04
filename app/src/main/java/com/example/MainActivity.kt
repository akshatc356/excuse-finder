package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ExcuseBottomNavigation
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.ExcuseFinderTheme
import com.example.ui.viewmodel.ExcuseViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExcuseFinderTheme {
                val viewModel: ExcuseViewModel = viewModel()
                ExcuseFinderApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ExcuseFinderApp(viewModel: ExcuseViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    BackHandler(enabled = currentScreen != Screen.HOME) {
        viewModel.handleBack()
    }

    val showBottomNav = currentScreen in listOf(
        Screen.HOME,
        Screen.HISTORY,
        Screen.PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBg,
        bottomBar = {
            if (showBottomNav) {
                ExcuseBottomNavigation(
                    currentScreen = currentScreen,
                    onNavigate = { targetScreen ->
                        viewModel.navigateTo(targetScreen)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomNav) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp(0f))
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen(viewModel = viewModel)
                    Screen.SCANNER -> ScannerScreen(viewModel = viewModel)
                    Screen.RESULTS -> ResultsScreen(viewModel = viewModel)
                    Screen.HISTORY -> HistoryScreen(viewModel = viewModel)
                    Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}
