package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BarcodeStamp
import com.example.ui.components.CharacterFace
import com.example.ui.components.CharacterShapeBadge
import com.example.ui.components.ExcuseCard
import com.example.ui.components.ExcuseScoreSection
import com.example.ui.components.HazardStripeBanner
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecurityStampBadge
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardBgElevated
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBg
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningRed
import com.example.ui.viewmodel.ExcuseViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun ResultsScreen(
    viewModel: ExcuseViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val result by viewModel.currentResult.collectAsState()
    val selectedIdx by viewModel.selectedExcuseIndex.collectAsState()
    val copyFeedback by viewModel.copyFeedback.collectAsState()

    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    if (result == null) {
        viewModel.navigateTo(Screen.HOME)
        return
    }

    val excuseResult = result!!

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 40.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardBg)
                        .border(1.dp, CardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "INCIDENT DOSSIER",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AccentYellow
                    )
                    Text(
                        text = "Generated Alibis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                // Share selected
                IconButton(
                    onClick = {
                        val activeText = excuseResult.excuses.getOrNull(selectedIdx)?.text ?: ""
                        if (activeText.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, activeText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Excuse"))
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardBg)
                        .border(1.dp, CardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hazard Banner
            HazardStripeBanner(
                text = "/// 3 TACTICAL RESPONSES COMPILED ///",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Incident Brief Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(CardBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ORIGINAL INCIDENT",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = AccentYellow
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SecurityStampBadge(
                                text = "TO: ${excuseResult.recipient.uppercase()}",
                                accentColor = AccentCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            SecurityStampBadge(
                                text = excuseResult.category.uppercase(),
                                accentColor = AccentYellow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "“${excuseResult.situation}”",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SELECT YOUR RESPONSE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Tap to choose",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Excuses Cards (Safe, Funny, Unhinged)
            excuseResult.excuses.forEachIndexed { index, option ->
                val isSelected = selectedIdx == index
                ExcuseCard(
                    excuse = option,
                    isSelected = isSelected,
                    onSelect = { viewModel.selectExcuseIndex(index) },
                    onCopy = { textToCopy ->
                        viewModel.copyExcuse(textToCopy)
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Excuse Telemetry Metrics for selected excuse
            val activeOption = excuseResult.excuses.getOrNull(selectedIdx) ?: excuseResult.excuses.firstOrNull()
            if (activeOption != null) {
                ExcuseScoreSection(
                    believability = activeOption.believability,
                    risk = activeOption.risk,
                    drama = activeOption.drama,
                    effort = activeOption.effort
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Back to Home CTA
            PrimaryButton(
                text = "CREATE ANOTHER EXCUSE →",
                onClick = {
                    viewModel.updateSituation("")
                    viewModel.navigateTo(Screen.HOME)
                },
                testTag = "create_another_excuse_button"
            )
        }
    }
}
