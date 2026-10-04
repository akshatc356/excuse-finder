package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardBgElevated
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningRed

enum class CharacterFace {
    SMUG_SUNGLASSES,
    COOKED_SKULL,
    NERVOUS_SWEAT,
    DOCTOR_PATCH,
    CHAOS_DEVIL
}

/**
 * Expressive Vector Character Graphics for Excuse Finder
 */
@Composable
fun CharacterShapeBadge(
    face: CharacterFace,
    modifier: Modifier = Modifier,
    accentColor: Color = AccentYellow
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        when (face) {
            CharacterFace.SMUG_SUNGLASSES -> {
                // Cool yellow/cyber face with angular sunglasses & smug grin
                drawCircle(
                    color = accentColor,
                    radius = w * 0.44f,
                    center = Offset(cx, cy)
                )
                // Sunglasses frame
                drawRoundRect(
                    color = Color(0xFF000000),
                    topLeft = Offset(w * 0.16f, h * 0.32f),
                    size = Size(w * 0.30f, h * 0.22f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRoundRect(
                    color = Color(0xFF000000),
                    topLeft = Offset(w * 0.54f, h * 0.32f),
                    size = Size(w * 0.30f, h * 0.22f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                // Bridge
                drawRect(
                    color = Color(0xFF000000),
                    topLeft = Offset(w * 0.42f, h * 0.38f),
                    size = Size(w * 0.16f, h * 0.08f)
                )
                // Glare highlights on glasses
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(w * 0.22f, h * 0.36f),
                    end = Offset(w * 0.34f, h * 0.46f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(w * 0.60f, h * 0.36f),
                    end = Offset(w * 0.72f, h * 0.46f),
                    strokeWidth = 3f
                )
                // Smug angled grin
                val smilePath = Path().apply {
                    moveTo(w * 0.36f, h * 0.68f)
                    quadraticTo(w * 0.52f, h * 0.78f, w * 0.70f, h * 0.64f)
                }
                drawPath(path = smilePath, color = Color(0xFF000000), style = Stroke(width = 6f))
            }

            CharacterFace.COOKED_SKULL -> {
                // "YOU'RE COOKED" Melting Skull / Hot Pan Mascot
                drawCircle(
                    color = WarningRed,
                    radius = w * 0.42f,
                    center = Offset(cx, cy * 0.9f)
                )
                // Melting bottom drips
                drawRoundRect(
                    color = WarningRed,
                    topLeft = Offset(w * 0.22f, h * 0.65f),
                    size = Size(w * 0.18f, h * 0.28f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = WarningRed,
                    topLeft = Offset(w * 0.46f, h * 0.68f),
                    size = Size(w * 0.22f, h * 0.26f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                // X X Eyes
                val eyeL = Offset(w * 0.34f, h * 0.42f)
                val eyeR = Offset(w * 0.66f, h * 0.42f)
                val ex = 10f
                drawLine(Color(0xFF000000), eyeL - Offset(ex, ex), eyeL + Offset(ex, ex), strokeWidth = 5f)
                drawLine(Color(0xFF000000), eyeL + Offset(-ex, ex), eyeL + Offset(ex, -ex), strokeWidth = 5f)
                drawLine(Color(0xFF000000), eyeR - Offset(ex, ex), eyeR + Offset(ex, ex), strokeWidth = 5f)
                drawLine(Color(0xFF000000), eyeR + Offset(-ex, ex), eyeR + Offset(ex, -ex), strokeWidth = 5f)
                // Open jaw
                drawOval(
                    color = Color(0xFF000000),
                    topLeft = Offset(w * 0.38f, h * 0.60f),
                    size = Size(w * 0.24f, h * 0.18f)
                )
            }

            CharacterFace.NERVOUS_SWEAT -> {
                // Wide terrified eyes with sweat drop
                drawCircle(
                    color = AccentCyan,
                    radius = w * 0.44f,
                    center = Offset(cx, cy)
                )
                // Big white sclera
                drawCircle(Color(0xFFFFFFFF), radius = w * 0.15f, center = Offset(w * 0.35f, h * 0.40f))
                drawCircle(Color(0xFFFFFFFF), radius = w * 0.15f, center = Offset(w * 0.65f, h * 0.40f))
                // Tiny black pupils
                drawCircle(Color(0xFF000000), radius = w * 0.05f, center = Offset(w * 0.35f, h * 0.40f))
                drawCircle(Color(0xFF000000), radius = w * 0.05f, center = Offset(w * 0.65f, h * 0.40f))
                // Wavy nervous mouth
                val mouth = Path().apply {
                    moveTo(w * 0.30f, h * 0.70f)
                    lineTo(w * 0.42f, h * 0.66f)
                    lineTo(w * 0.54f, h * 0.72f)
                    lineTo(w * 0.68f, h * 0.68f)
                }
                drawPath(path = mouth, color = Color(0xFF000000), style = Stroke(width = 5f))
                // Giant blue/yellow sweat drop
                val drop = Path().apply {
                    moveTo(w * 0.82f, h * 0.20f)
                    quadraticTo(w * 0.94f, h * 0.32f, w * 0.86f, h * 0.40f)
                    quadraticTo(w * 0.76f, h * 0.40f, w * 0.78f, h * 0.28f)
                    close()
                }
                drawPath(drop, color = Color(0xFFFFFFFF))
            }

            CharacterFace.DOCTOR_PATCH -> {
                // Band-aid with cross
                drawRoundRect(
                    color = AccentYellow,
                    topLeft = Offset(w * 0.15f, h * 0.25f),
                    size = Size(w * 0.70f, h * 0.50f),
                    cornerRadius = CornerRadius(16f, 16f)
                )
                // Band-aid dots
                val dotColor = Color(0xFF000000).copy(alpha = 0.35f)
                drawCircle(dotColor, 4f, Offset(w * 0.26f, h * 0.50f))
                drawCircle(dotColor, 4f, Offset(w * 0.74f, h * 0.50f))
                // Red medical cross
                drawRect(Color(0xFFFF4D4D), Offset(w * 0.44f, h * 0.35f), Size(w * 0.12f, h * 0.30f))
                drawRect(Color(0xFFFF4D4D), Offset(w * 0.35f, h * 0.44f), Size(w * 0.30f, h * 0.12f))
            }

            CharacterFace.CHAOS_DEVIL -> {
                // Purple chaotic imp with horns
                drawCircle(
                    color = AccentPurple,
                    radius = w * 0.42f,
                    center = Offset(cx, cy * 1.05f)
                )
                // Horns
                val hornL = Path().apply {
                    moveTo(w * 0.22f, h * 0.36f)
                    lineTo(w * 0.12f, h * 0.12f)
                    lineTo(w * 0.38f, h * 0.26f)
                    close()
                }
                drawPath(hornL, AccentYellow)
                val hornR = Path().apply {
                    moveTo(w * 0.78f, h * 0.36f)
                    lineTo(w * 0.88f, h * 0.12f)
                    lineTo(w * 0.62f, h * 0.26f)
                    close()
                }
                drawPath(hornR, AccentYellow)
                // Slanted cunning eyes
                drawLine(Color(0xFF000000), Offset(w * 0.30f, h * 0.48f), Offset(w * 0.42f, h * 0.54f), 5f)
                drawLine(Color(0xFF000000), Offset(w * 0.70f, h * 0.48f), Offset(w * 0.58f, h * 0.54f), 5f)
                // Sharp toothy grin
                val devilMouth = Path().apply {
                    moveTo(w * 0.32f, h * 0.72f)
                    quadraticTo(w * 0.50f, h * 0.86f, w * 0.68f, h * 0.72f)
                    close()
                }
                drawPath(devilMouth, Color(0xFF000000))
            }
        }
    }
}

/**
 * Diagonal Hazard Caution Stripe Banner
 */
@Composable
fun HazardStripeBanner(
    text: String = "/// DAMAGE CONTROL ENGINE ACTIVE ///",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CardBg)
            .border(1.dp, AccentYellow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = AccentYellow
            )

            // Barcode stamp representation
            BarcodeStamp(
                modifier = Modifier
                    .width(44.dp)
                    .height(14.dp)
            )
        }
    }
}

/**
 * Minimalist Tech Barcode Graphic
 */
@Composable
fun BarcodeStamp(
    modifier: Modifier = Modifier,
    barColor: Color = AccentYellow
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val bars = listOf(3f, 1f, 4f, 2f, 1f, 5f, 2f, 1f, 4f, 2f, 3f, 1f, 4f)
        val totalWeights = bars.sum()
        var currentX = 0f

        bars.forEachIndexed { index, weight ->
            val barW = (weight / totalWeights) * w
            if (index % 2 == 0) {
                drawRect(
                    color = barColor,
                    topLeft = Offset(currentX, 0f),
                    size = Size(barW * 0.85f, h)
                )
            }
            currentX += barW
        }
    }
}

/**
 * Streetwear/AI Stamp Badge ("100% BULLETPROOF", "VERIFIED", etc.)
 */
@Composable
fun SecurityStampBadge(
    text: String,
    accentColor: Color = AccentYellow,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .border(1.dp, accentColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = accentColor
        )
    }
}

/**
 * Interactive Alibi Category Card with Character Mascot
 */
@Composable
fun AlibiCategoryCard(
    title: String,
    subtitle: String,
    face: CharacterFace,
    accentColor: Color = AccentYellow,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) AccentYellow else CardBorder
    val bgColor = if (isSelected) CardBgElevated else CardBg

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                SecurityStampBadge(
                    text = subtitle,
                    accentColor = if (isSelected) AccentYellow else TextMuted
                )

                CharacterShapeBadge(
                    face = face,
                    accentColor = accentColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 18.sp,
                    color = if (isSelected) AccentYellow else TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isSelected) "● ACTIVE FILTER" else "Tap to prepare",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) AccentYellow else TextMuted
                )
            }
        }
    }
}
