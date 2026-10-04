package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardBgElevated
import com.example.ui.theme.CardBorder
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningRed

@Composable
fun SeveritySlider(
    currentSeverity: Int, // 1, 2, 3
    onSeverityChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val (activeColor, dramaticText, icon) = when (currentSeverity) {
        1 -> Triple(SuccessGreen, "NOT BAD", "🟢")
        2 -> Triple(AccentYellow, "UH OH", "🟡")
        else -> Triple(WarningRed, "I'M COOKED", "💀")
    }

    val animatedColor by animateColorAsState(targetValue = activeColor, label = "severity_color")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .border(
                if (currentSeverity == 3) 2.dp else 1.dp,
                if (currentSeverity == 3) WarningRed else CardBorder,
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "2. HOW BAD IS IT?",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = icon,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(
                    text = dramaticText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = animatedColor
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3 Segmented Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBgElevated)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val levels = listOf(
                1 to "NOT BAD",
                2 to "UH OH",
                3 to "I'M COOKED"
            )

            levels.forEach { (level, title) ->
                val isSelected = currentSeverity == level
                val bg = if (isSelected) {
                    when (level) {
                        1 -> SuccessGreen
                        2 -> AccentYellow
                        else -> WarningRed
                    }
                } else Color.Transparent

                val textColor = if (isSelected) {
                    when (level) {
                        1 -> Color(0xFF000000)
                        2 -> Color(0xFF000000)
                        else -> Color(0xFFFFFFFF)
                    }
                } else TextMuted

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSeverityChanged(level)
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }
}
