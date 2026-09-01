package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AIElectricCyan
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.FeminineRed
import com.example.ui.theme.GermanCrimson
import com.example.ui.theme.GermanGold
import com.example.ui.theme.GermanGoldLight
import com.example.ui.theme.MasculineBlue
import com.example.ui.theme.NeuterGreen
import com.example.ui.theme.PluralYellow
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessEmerald

@Composable
fun CEFRBadge(
    level: String,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    val (bgColor, textColor) = when {
        level.startsWith("A1") -> MasculineBlue.copy(alpha = 0.2f) to MasculineBlue
        level.startsWith("A2") -> AIElectricCyan.copy(alpha = 0.2f) to AIElectricCyan
        level.startsWith("B1") -> SuccessEmerald.copy(alpha = 0.2f) to SuccessEmerald
        level.startsWith("B2") -> GermanGold.copy(alpha = 0.2f) to GermanGold
        level.startsWith("C1") -> GermanCrimson.copy(alpha = 0.2f) to GermanCrimson
        else -> Slate700.copy(alpha = 0.3f) to Slate300
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = if (isLarge) 12.dp else 8.dp, vertical = if (isLarge) 6.dp else 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level,
            color = textColor,
            fontSize = if (isLarge) 14.sp else 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GenderArticleChip(
    article: String?,
    modifier: Modifier = Modifier
) {
    if (article.isNullOrBlank()) return

    val (bgColor, textColor) = when (article.lowercase()) {
        "der" -> MasculineBlue.copy(alpha = 0.2f) to MasculineBlue
        "die" -> FeminineRed.copy(alpha = 0.2f) to FeminineRed
        "das" -> NeuterGreen.copy(alpha = 0.2f) to NeuterGreen
        else -> PluralYellow.copy(alpha = 0.2f) to PluralYellow
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = article,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StreakXPBadge(
    streakDays: Int,
    xp: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Streak Pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Slate800)
                .border(1.dp, GermanGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥", fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$streakDays d",
                color = GermanGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // XP Pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Slate800)
                .border(1.dp, AIElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚡", fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$xp XP",
                color = AIElectricCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GlowingCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GermanGold.copy(alpha = 0.35f),
    backgroundColor: Color = Slate900,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(borderColor, CardBorderSubtle)))
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun AnimatedAudioWaveform(
    isActive: Boolean,
    isAiSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(320, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(480, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w3"
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w4"
    )

    val barColor = if (isAiSpeaking) AIElectricCyan else GermanGold

    Row(
        modifier = modifier.height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val heights = if (isActive) listOf(wave1, wave2, wave4, wave3, wave2, wave1, wave3) else listOf(0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f)
        heights.forEach { fraction ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((36 * fraction).dp)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}
