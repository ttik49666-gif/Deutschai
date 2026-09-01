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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.model.TutorPersonality
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
import com.example.ui.viewmodel.MainViewModel

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedGoal by remember { mutableStateOf("Career & Relocation to Germany") }
    var selectedTime by remember { mutableIntStateOf(15) }
    var selectedPersonality by remember { mutableStateOf(TutorPersonality.FRIENDLY) }

    val goals = listOf(
        "💼 Career & Relocation to Germany",
        "🗣️ Everyday Conversational Fluency",
        "📝 Goethe / telc Exam Preparation",
        "🎓 Academic Study & University",
        "✈️ Travel & Cultural Immersion"
    )

    val times = listOf(10, 15, 20, 30)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("onboarding_screen"),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 36.dp, bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            LinearProgressIndicator(
                progress = { ((step + 1) / 4f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GermanGold,
                trackColor = Slate800
            )
        }

        when (step) {
            0 -> {
                // Welcome Step
                item {
                    GlowingCard(borderColor = GermanGold.copy(alpha = 0.5f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DEUTSCHAI", color = GermanGold, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Learn German.\nSpeak German.\nThink German.", style = MaterialTheme.typography.displayMedium, color = PureWhite, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "Your personal AI German tutor. From A1 to C1, real voice dialogues, immediate error diagnosis, and adaptive CEFR progression.",
                                color = Slate300,
                                fontSize = 14.sp,
                                lineHeight = 21.sp
                            )
                        }
                    }
                }
            }

            1 -> {
                // Goal Selection
                item {
                    Text("What is your primary German goal?", style = MaterialTheme.typography.headlineMedium, color = PureWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("DeutschAI will tailor your conversation topics and grammar drills to this objective.", color = Slate400, fontSize = 13.sp)
                }

                items(goals) { goal ->
                    val isSel = selectedGoal == goal
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGoal = goal },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSel) Slate850 else Slate900),
                        border = if (isSel) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan))) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(goal, color = PureWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            if (isSel) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GermanGold)
                            }
                        }
                    }
                }
            }

            2 -> {
                // Daily Commitment & Personality
                item {
                    Text("Choose your daily practice commitment", style = MaterialTheme.typography.headlineMedium, color = PureWhite, fontWeight = FontWeight.Bold)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        times.forEach { t ->
                            val isSel = selectedTime == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSel) GermanGold else Slate900)
                                    .clickable { selectedTime = t }
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$t", color = if (isSel) GermanBlack else PureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text("min/day", color = if (isSel) GermanBlack else Slate400, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select your AI Tutor Personality", style = MaterialTheme.typography.titleLarge, color = PureWhite, fontWeight = FontWeight.Bold)
                }

                items(TutorPersonality.values()) { p ->
                    val isSel = selectedPersonality == p
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPersonality = p },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSel) Slate850 else Slate900),
                        border = if (isSel) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan))) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(p.emoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.title, color = PureWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(p.tagline, color = Slate400, fontSize = 12.sp)
                            }
                            if (isSel) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GermanGold)
                            }
                        }
                    }
                }
            }

            3 -> {
                // Readiness & Placement Test invitation
                item {
                    GlowingCard(borderColor = AIElectricCyan.copy(alpha = 0.5f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ready to start your personalized plan!", color = AIElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("We will calibrate your CEFR level and prepare your interactive curriculum.", color = Slate300, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }

        // Navigation CTA Button
        item {
            Button(
                onClick = {
                    if (step < 3) {
                        step++
                    } else {
                        viewModel.updatePersonality(selectedPersonality)
                        viewModel.closeOnboarding()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GermanGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (step < 3) "Continue" else "Launch DeutschAI",
                        color = GermanBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = GermanBlack)
                }
            }
        }
    }
}
