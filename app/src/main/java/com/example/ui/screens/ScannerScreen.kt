package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ErrorState
import com.example.ui.components.ExcuseScanner
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.DarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.ExcuseViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun ScannerScreen(
    viewModel: ExcuseViewModel,
    modifier: Modifier = Modifier
) {
    val stepText by viewModel.scannerStepText.collectAsState()
    val keywordText by viewModel.scannerKeyword.collectAsState()
    val generationError by viewModel.generationError.collectAsState()

    BackHandler {
        viewModel.cancelOrRetryGeneration()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        if (generationError != null) {
            ErrorState(
                errorMessage = generationError,
                onTryAgain = { viewModel.cancelOrRetryGeneration() },
                onGoHome = { viewModel.navigateTo(Screen.HOME) }
            )
        } else {
            ExcuseScanner(
                stepText = stepText,
                keywordText = keywordText
            )

            // Cancel button at bottom
            TextButton(
                onClick = { viewModel.cancelOrRetryGeneration() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "ABORT SCAN",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
            }
        }
    }
}
