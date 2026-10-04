package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.ui.components.AlibiCategoryCard
import com.example.ui.components.ApiKeyConfigDialog
import com.example.ui.components.BarcodeStamp
import com.example.ui.components.CategoryChip
import com.example.ui.components.CharacterFace
import com.example.ui.components.CharacterShapeBadge
import com.example.ui.components.HazardStripeBanner
import com.example.ui.components.PrimaryButton
import com.example.ui.components.QuestionSetupSheet
import com.example.ui.components.SecurityStampBadge
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardBgElevated
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBg
import com.example.ui.theme.SecondaryBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningRed
import com.example.ui.viewmodel.ExcuseViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: ExcuseViewModel,
    modifier: Modifier = Modifier
) {
    val situationText by viewModel.situationInput.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedRecipient by viewModel.selectedRecipient.collectAsState()
    val severityLevel by viewModel.severityLevel.collectAsState()
    val selectedVibe by viewModel.selectedVibe.collectAsState()
    val isSetupSheetVisible by viewModel.isSetupSheetVisible.collectAsState()
    val customApiKey by viewModel.customApiKey.collectAsState()
    val customModel by viewModel.customModel.collectAsState()

    var showApiKeyDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Rotating situational placeholders
    val placeholders = listOf(
        "I forgot to reply to my friend.",
        "I'm 2 hours late for work.",
        "I skipped my 8 AM college lecture.",
        "I don't want to attend this party.",
        "I missed an assignment deadline.",
        "I forgot my partner's birthday."
    )
    var placeholderIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3200)
            placeholderIndex = (placeholderIndex + 1) % placeholders.size
        }
    }

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
                .padding(top = 16.dp, bottom = 120.dp)
        ) {
            // Top Bar with Brand & Paid API Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Small Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentYellow)
                            .border(1.dp, Color(0xFF000000), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EXCUSE FINDER",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = TextPrimary
                    )
                }

                // AI Model Status Pill (clickable to configure Paid / Pro Key)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AccentYellow.copy(alpha = 0.12f))
                        .border(1.dp, AccentYellow.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { showApiKeyDialog = true }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(AccentYellow)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (customApiKey.isNotBlank()) "PRO KEY ACTIVE" else "GEMINI 3.5 AI",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp,
                            color = AccentYellow
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hazard Caution Stripe Banner
            HazardStripeBanner(
                text = "/// DAMAGE CONTROL ENGINE v3.5 ///",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dramatic Headline
            Column {
                Text(
                    text = "WHAT",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 44.sp,
                    letterSpacing = (-1).sp,
                    color = TextPrimary
                )
                Text(
                    text = "DID",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 44.sp,
                    letterSpacing = (-1).sp,
                    color = TextPrimary
                )
                Text(
                    text = "YOU",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 44.sp,
                    letterSpacing = (-1).sp,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DO",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 44.sp,
                        letterSpacing = (-1).sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "?",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentYellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tell us what happened. We'll figure out what you should say.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Input Area: Large rounded dark box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INCIDENT STATEMENT",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = AccentYellow
                        )

                        if (situationText.isNotBlank()) {
                            Text(
                                text = "${situationText.length} chars",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = situationText,
                        onValueChange = { viewModel.updateSituation(it) },
                        placeholder = {
                            Text(
                                text = "I need an excuse because ${placeholders[placeholderIndex]}",
                                color = TextMuted,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("situation_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = AccentYellow
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (situationText.isNotBlank()) {
                            Text(
                                text = "Clear",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningRed,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { viewModel.updateSituation("") }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        // Tagline snippet
                        Text(
                            text = "“Because 'I forgot' won't save you.”",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Category Chips Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SELECT SCENARIO CATEGORY",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )

                Text(
                    text = selectedCategory.name,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentYellow
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal Scrollable Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Category.COLLEGE to "🎓",
                    Category.WORK to "💼",
                    Category.PARENTS to "🏠",
                    Category.FRIENDS to "🤝",
                    Category.RELATIONSHIP to "❤️",
                    Category.MONEY to "💸",
                    Category.SOCIAL to "🎉",
                    Category.LATE to "⏰",
                    Category.MESSAGES to "💬"
                ).forEach { (cat, emoji) ->
                    val isSelected = selectedCategory == cat
                    CategoryChip(
                        title = cat.displayName,
                        emoji = emoji,
                        isSelected = isSelected,
                        onSelect = {
                            viewModel.selectCategory(cat)
                            if (situationText.isBlank()) {
                                viewModel.updateSituation(cat.samplePrompt)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Big Full-Width Primary CTA Button
            PrimaryButton(
                text = "FIND MY EXCUSE →",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.openSetupSheet()
                },
                testTag = "find_excuse_cta"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Interactive Alibi Mascot Cards
            Text(
                text = "QUICK DAMAGE CONTROL PRESETS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AlibiCategoryCard(
                    title = "Forgot to\nReply",
                    subtitle = "99% SAFE",
                    face = CharacterFace.SMUG_SUNGLASSES,
                    accentColor = AccentYellow,
                    isSelected = situationText.contains("reply", ignoreCase = true),
                    onClick = {
                        viewModel.updateSituation("I forgot to reply to my friend for 3 days.")
                        viewModel.selectCategory(Category.MESSAGES)
                    },
                    modifier = Modifier.size(width = 150.dp, height = 155.dp)
                )

                AlibiCategoryCard(
                    title = "Work\nEmergency",
                    subtitle = "PRO ALIBI",
                    face = CharacterFace.NERVOUS_SWEAT,
                    accentColor = AccentCyan,
                    isSelected = situationText.contains("late", ignoreCase = true),
                    onClick = {
                        viewModel.updateSituation("I'm 2 hours late for the work team meeting.")
                        viewModel.selectCategory(Category.WORK)
                    },
                    modifier = Modifier.size(width = 150.dp, height = 155.dp)
                )

                AlibiCategoryCard(
                    title = "I'm Totally\nCooked",
                    subtitle = "98% RISK",
                    face = CharacterFace.COOKED_SKULL,
                    accentColor = WarningRed,
                    isSelected = situationText.contains("deadline", ignoreCase = true) || situationText.contains("exam", ignoreCase = true),
                    onClick = {
                        viewModel.updateSituation("I missed the project deadline and professor is asking.")
                        viewModel.selectCategory(Category.COLLEGE)
                    },
                    modifier = Modifier.size(width = 150.dp, height = 155.dp)
                )

                AlibiCategoryCard(
                    title = "Ghosting\na Party",
                    subtitle = "CHAOS TIER",
                    face = CharacterFace.CHAOS_DEVIL,
                    accentColor = AccentPurple,
                    isSelected = situationText.contains("party", ignoreCase = true),
                    onClick = {
                        viewModel.updateSituation("I don't want to attend the party tonight and need to cancel.")
                        viewModel.selectCategory(Category.SOCIAL)
                    },
                    modifier = Modifier.size(width = 150.dp, height = 155.dp)
                )
            }
        }

        // Setup Calibration Bottom Sheet (Question 1, 2, 3)
        if (isSetupSheetVisible) {
            QuestionSetupSheet(
                selectedRecipient = selectedRecipient,
                onSelectRecipient = { viewModel.selectRecipient(it) },
                severityLevel = severityLevel,
                onSelectSeverity = { viewModel.setSeverityLevel(it) },
                selectedVibe = selectedVibe,
                onSelectVibe = { viewModel.setVibe(it) },
                onDismiss = { viewModel.closeSetupSheet() },
                onConfirm = {
                    viewModel.startExcuseGeneration()
                }
            )
        }

        // Api Key & Paid Model Dialog
        if (showApiKeyDialog) {
            ApiKeyConfigDialog(
                currentApiKey = customApiKey,
                currentModel = customModel,
                onSave = { key, model ->
                    viewModel.setCustomApiKey(key)
                    viewModel.setCustomModel(model)
                },
                onDismiss = { showApiKeyDialog = false }
            )
        }
    }
}
