package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.ui.components.CEFRBadge
import com.example.ui.components.GlowingCard
import com.example.ui.theme.AIElectricCyan
import com.example.ui.theme.ErrorRose
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
import com.example.ui.viewmodel.PlacementQuestionType

@Composable
fun PlacementTestScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val questions = viewModel.placementQuestions
    val placementResult by viewModel.placementResult.collectAsState()

    var currentQIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }

    // Per-skill tracking
    var grammarCorrect by remember { mutableIntStateOf(0) }
    var grammarTotal by remember { mutableIntStateOf(0) }
    var vocabCorrect by remember { mutableIntStateOf(0) }
    var vocabTotal by remember { mutableIntStateOf(0) }
    var readingCorrect by remember { mutableIntStateOf(0) }
    var readingTotal by remember { mutableIntStateOf(0) }
    var listeningCorrect by remember { mutableIntStateOf(0) }
    var listeningTotal by remember { mutableIntStateOf(0) }
    var speakingCorrect by remember { mutableIntStateOf(0) }
    var speakingTotal by remember { mutableIntStateOf(0) }
    var writingCorrect by remember { mutableIntStateOf(0) }
    var writingTotal by remember { mutableIntStateOf(0) }

    val currentQ = questions.getOrNull(currentQIndex)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("placement_test_screen")
    ) {
        if (placementResult == null && currentQ != null) {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Top Progress Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.closePlacementTest() }) {
                            Icon(Icons.Default.Close, contentDescription = "Exit", tint = Slate400)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "CEFR PLACEMENT TEST",
                                color = GermanGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "اختبار تحديد المستوى التكيفي",
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        }

                        CEFRBadge(level = currentQ.cefr)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (currentQIndex.toFloat() / questions.size.toFloat()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GermanGold,
                        trackColor = Slate800
                    )
                }

                // Question Card
                item {
                    GlowingCard(borderColor = GermanGold.copy(alpha = 0.35f)) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Skill: ${currentQ.skill}",
                                    color = AIElectricCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Frage ${currentQIndex + 1} von ${questions.size}",
                                    color = Slate400,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentQ.prompt,
                                style = MaterialTheme.typography.titleLarge,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold
                            )

                            if (currentQ.promptAr.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentQ.promptAr,
                                    color = Slate300,
                                    fontSize = 13.sp
                                )
                            }

                            if (!currentQ.contextSnippet.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Slate800)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = currentQ.contextSnippet,
                                        color = GermanGold,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp
                                    )
                                }
                            }

                            if (currentQ.questionType == PlacementQuestionType.LISTENING_COMPREHENSION && !currentQ.audioSentence.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.speakGermanText(currentQ.audioSentence) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = GermanGold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("🔊 Hörbeispiel abspielen (استمع للمقطع)", color = Slate100, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Options List
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        currentQ.options.forEachIndexed { index, option ->
                            val isSelected = selectedOptionIndex == index
                            val isCorrect = index == currentQ.correctIndex

                            val (borderBrush, bgColor) = when {
                                hasAnswered && isCorrect ->
                                    Brush.linearGradient(listOf(SuccessEmerald, SuccessEmerald)) to Slate850
                                hasAnswered && isSelected && !isCorrect ->
                                    Brush.linearGradient(listOf(ErrorRose, ErrorRose)) to Slate850
                                isSelected ->
                                    Brush.linearGradient(listOf(GermanGold, AIElectricCyan)) to Slate850
                                else ->
                                    Brush.linearGradient(listOf(Slate700, Slate800)) to Slate900
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !hasAnswered) {
                                        selectedOptionIndex = index
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = bgColor),
                                border = CardDefaults.outlinedCardBorder().copy(brush = borderBrush)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${('A' + index)}. ",
                                        color = GermanGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = option,
                                        color = PureWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (hasAnswered && isCorrect) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessEmerald)
                                    }
                                }
                            }
                        }
                    }
                }

                // Answer explanation and Next button
                item {
                    if (hasAnswered) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate850)
                                .padding(14.dp)
                        ) {
                            Column {
                                Text("💡 Erklärung & Regel / التوضيح:", color = GermanGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                if (currentQ.explanation.isNotBlank()) {
                                    Text(currentQ.explanation, color = Slate300, fontSize = 13.sp)
                                }
                                if (currentQ.explanationAr.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(currentQ.explanationAr, color = PureWhite, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (!hasAnswered) {
                                hasAnswered = true
                                 val isCorrect = selectedOptionIndex == currentQ.correctIndex
                                when (currentQ.skill) {
                                    "Grammar" -> {
                                        grammarTotal++
                                        if (isCorrect) grammarCorrect++
                                    }
                                    "Vocabulary" -> {
                                        vocabTotal++
                                        if (isCorrect) vocabCorrect++
                                    }
                                    "Reading" -> {
                                        readingTotal++
                                        if (isCorrect) readingCorrect++
                                    }
                                    "Listening" -> {
                                        listeningTotal++
                                        if (isCorrect) listeningCorrect++
                                    }
                                    "Speaking" -> {
                                        speakingTotal++
                                        if (isCorrect) speakingCorrect++
                                    }
                                    "Writing" -> {
                                        writingTotal++
                                        if (isCorrect) writingCorrect++
                                    }
                                }
                            } else {
                                if (currentQIndex < questions.size - 1) {
                                    currentQIndex++
                                    selectedOptionIndex = null
                                    hasAnswered = false
                                } else {
                                    val calcSpeaking = if (speakingTotal > 0) (speakingCorrect * 100) / speakingTotal else 75
                                    val calcWriting = if (writingTotal > 0) (writingCorrect * 100) / writingTotal else 70
                                    viewModel.evaluateMultiSkillPlacement(
                                        grammarCorrect = grammarCorrect,
                                        grammarTotal = grammarTotal,
                                        vocabCorrect = vocabCorrect,
                                        vocabTotal = vocabTotal,
                                        readingCorrect = readingCorrect,
                                        readingTotal = readingTotal,
                                        listeningCorrect = listeningCorrect,
                                        listeningTotal = listeningTotal,
                                        speakingScore = calcSpeaking,
                                        writingScore = calcWriting
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GermanGold),
                        shape = RoundedCornerShape(14.dp),
                        enabled = selectedOptionIndex != null
                    ) {
                        Text(
                            text = if (!hasAnswered) "Antwort prüfen (تحقق من الإجابة)" else if (currentQIndex < questions.size - 1) "Nächste Frage (السؤال التالي)" else "Mein CEFR-Niveau berechnen (عرض النتيجة)",
                            color = GermanBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else if (placementResult != null) {
            // Placement Results Report
            val res = placementResult!!
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    GlowingCard(borderColor = SuccessEmerald.copy(alpha = 0.5f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PLACEMENT TEST COMPLETED", color = SuccessEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Calibrated CEFR Level", color = Slate400, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(res.estimatedLevel, style = MaterialTheme.typography.displayLarge, color = PureWhite, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${res.confidenceScore}% Confidence Rating", color = GermanGold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("6-SKILL CEFR PROFILE / تقرير المهارات الست", color = AIElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Speaking: ${res.speakingScore}", color = Slate300, fontSize = 13.sp)
                                Text("Listening: ${res.listeningScore}", color = Slate300, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grammar: ${res.grammarScore}", color = Slate300, fontSize = 13.sp)
                                Text("Vocabulary: ${res.vocabularyScore}", color = Slate300, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Reading: ${res.readingScore}", color = Slate300, fontSize = 13.sp)
                                Text("Writing: ${res.writingScore}", color = Slate300, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(res.summaryFeedback, color = PureWhite, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                    }
                }

                item {
                    Button(
                        onClick = { viewModel.closePlacementTest() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GermanGold),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Apply to My Learning Path (تطبيق على خطتي التعليمية)", color = GermanBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
