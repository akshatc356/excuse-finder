package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Recipient
import com.example.data.model.Vibe
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardBgElevated
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuestionSetupSheet(
    selectedRecipient: Recipient,
    onSelectRecipient: (Recipient) -> Unit,
    severityLevel: Int,
    onSelectSeverity: (Int) -> Unit,
    selectedVibe: Vibe,
    onSelectVibe: (Vibe) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val haptic = LocalHapticFeedback.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardBg,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF333333))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INCIDENT CALIBRATION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AccentYellow
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Customize Your Alibi",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // QUESTION 1: WHO ARE YOU TALKING TO?
            Text(
                text = "1. WHO ARE YOU TALKING TO?",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Recipient.values().forEach { recipient ->
                    val isSelected = selectedRecipient == recipient
                    val bg = if (isSelected) AccentYellow else CardBgElevated
                    val textC = if (isSelected) Color(0xFF000000) else TextPrimary
                    val borderC = if (isSelected) AccentYellow else CardBorder

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                            .border(1.dp, borderC, RoundedCornerShape(16.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectRecipient(recipient)
                            }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                            .testTag("recipient_${recipient.name}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = recipient.iconEmoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = recipient.title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                color = textC
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // QUESTION 2: HOW BAD IS IT?
            SeveritySlider(
                currentSeverity = severityLevel,
                onSeverityChanged = onSelectSeverity
            )

            Spacer(modifier = Modifier.height(22.dp))

            // QUESTION 3: WHAT VIBE DO YOU WANT?
            Text(
                text = "3. WHAT VIBE DO YOU WANT?",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Vibe.values().forEach { vibe ->
                    val isSelected = selectedVibe == vibe
                    val bg = if (isSelected) AccentYellow else CardBgElevated
                    val textC = if (isSelected) Color(0xFF000000) else TextPrimary
                    val borderC = if (isSelected) AccentYellow else CardBorder

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                            .border(1.dp, borderC, RoundedCornerShape(16.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectVibe(vibe)
                            }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                            .testTag("vibe_${vibe.name}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = vibe.tag, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = vibe.title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                color = textC
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // GENERATE BUTTON
            PrimaryButton(
                text = "GENERATE ALIBIS ⚡",
                onClick = onConfirm,
                testTag = "generate_excuses_confirm"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "AI-powered damage control tailored to your exact situation.",
                fontSize = 11.sp,
                color = TextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
