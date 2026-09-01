package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CorrectionLevel
import com.example.data.model.TutorPersonality
import com.example.ui.components.CEFRBadge
import com.example.ui.components.GlowingCard
import com.example.ui.theme.AIElectricCyan
import com.example.ui.theme.GermanBlack
import com.example.ui.theme.GermanGold
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.SuccessEmerald
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val user = userProfile ?: return
    var voiceSpeedSlider by remember(user.voiceSpeed) { mutableFloatStateOf(user.voiceSpeed) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // User Info Card
        item {
            GlowingCard(borderColor = GermanGold.copy(alpha = 0.35f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(GermanGold, AIElectricCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = GermanBlack, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, style = MaterialTheme.typography.titleLarge, color = PureWhite, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CEFRBadge(level = user.currentLevel)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("🔥 ${user.streakDays}d Streak • ⚡ ${user.totalXp} XP", color = Slate400, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // AI Tutor Personality Selector
        item {
            Text(
                text = "AI TUTOR PERSONALITY",
                color = GermanGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        items(TutorPersonality.values()) { personality ->
            val isSelected = user.tutorPersonality == personality
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.updatePersonality(personality) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Slate850 else Slate900),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan))) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(personality.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(personality.title, color = PureWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(personality.tagline, color = Slate400, fontSize = 12.sp)
                    }
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = GermanGold, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // Correction Intelligence Selector
        item {
            Text(
                text = "CORRECTION INTELLIGENCE",
                color = AIElectricCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CorrectionLevel.values().forEach { level ->
                    val isSelected = user.correctionLevel == level
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateCorrectionLevel(level) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Slate850 else Slate900),
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AIElectricCyan, GermanGold))) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(level.title, color = PureWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(level.description, color = Slate400, fontSize = 12.sp)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = AIElectricCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Voice Speed Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("German Speech Rate", color = PureWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${String.format("%.2f", voiceSpeedSlider)}x", color = GermanGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = voiceSpeedSlider,
                        onValueChange = {
                            voiceSpeedSlider = it
                            viewModel.updateVoiceSpeed(it)
                        },
                        valueRange = 0.6f..1.4f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = GermanGold,
                            activeTrackColor = GermanGold,
                            inactiveTrackColor = Slate800
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            viewModel.speakGermanText("Guten Tag! Ich bin DeutschAI und spreche mit dieser Geschwindigkeit.", voiceSpeedSlider)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = GermanGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test German Voice Speed", color = Slate100, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Retake Placement Test Action
        item {
            Button(
                onClick = { viewModel.openPlacementTest() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Slate850),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = GermanGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retake Adaptive Placement Test", color = GermanGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}
