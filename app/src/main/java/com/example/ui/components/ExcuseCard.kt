package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.ExcuseOption
import com.example.data.model.ExcuseType
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
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
fun ExcuseCard(
    excuse: ExcuseOption,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember(excuse.text) { mutableStateOf(excuse.text) }
    var isCopied by remember { mutableStateOf(false) }

    val (badgeBg, badgeText, badgeColor, face) = when (excuse.type) {
        ExcuseType.SAFE -> Quadruple(SuccessGreen.copy(alpha = 0.15f), "SAFE PLAY", SuccessGreen, CharacterFace.SMUG_SUNGLASSES)
        ExcuseType.FUNNY -> Quadruple(AccentYellow.copy(alpha = 0.15f), "WITTY SPIN", AccentYellow, CharacterFace.NERVOUS_SWEAT)
        ExcuseType.UNHINGED -> Quadruple(WarningRed.copy(alpha = 0.18f), "UNHINGED AUDACITY", WarningRed, CharacterFace.CHAOS_DEVIL)
        ExcuseType.PATCHED -> Quadruple(AccentCyan.copy(alpha = 0.15f), "DOCTOR PATCHED", AccentCyan, CharacterFace.DOCTOR_PATCH)
    }

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AccentYellow else CardBorder,
        label = "card_border"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardBg)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(22.dp))
            .clickable { onSelect() }
            .padding(18.dp)
            .testTag("excuse_card_${excuse.type.name}")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CharacterShapeBadge(
                    face = face,
                    accentColor = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        color = badgeColor
                    )
                }
            }

            // Stats telemetry pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBgElevated)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Plausibility ",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${excuse.believability}%",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentYellow
                )
                Text(
                    text = " • ",
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Text(
                    text = "Risk ",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${excuse.risk}%",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (excuse.risk > 50) WarningRed else SuccessGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Body Text / Editor
        if (isEditing) {
            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentYellow,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardBgElevated,
                    unfocusedContainerColor = CardBgElevated,
                    cursorColor = AccentYellow
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                SecondaryButton(
                    text = "DONE",
                    onClick = { isEditing = false },
                    modifier = Modifier.height(38.dp)
                )
            }
        } else {
            Text(
                text = "“$editText”",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit trigger
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isEditing = !isEditing }
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Excuse",
                    tint = TextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isEditing) "CANCEL" else "EDIT TEXT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            // Copy Action Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isCopied) AccentYellow else CardBgElevated)
                    .border(1.dp, if (isCopied) AccentYellow else CardBorder, RoundedCornerShape(14.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Excuse", editText)
                        clipboard.setPrimaryClip(clip)
                        isCopied = true
                        onCopy(editText)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("copy_button_${excuse.type.name}"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = if (isCopied) Color(0xFF000000) else TextPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCopied) "COPIED ✓" else "COPY TEXT",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isCopied) Color(0xFF000000) else TextPrimary
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
