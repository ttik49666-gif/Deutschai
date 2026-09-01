package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GrammarTopic
import com.example.data.model.Lesson
import com.example.data.model.VocabularyItem
import com.example.ui.components.CEFRBadge
import com.example.ui.components.GenderArticleChip
import com.example.ui.components.GlowingCard
import com.example.ui.theme.AIElectricCyan
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.GermanBlack
import com.example.ui.theme.GermanCrimson
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
import com.example.ui.viewmodel.LearnLabMode
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LearnScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeLab by viewModel.activeLabMode.collectAsState()
    val labTabs = listOf(
        LearnLabMode.CURRICULUM to "Curriculum",
        LearnLabMode.VOCABULARY to "Vocabulary",
        LearnLabMode.GRAMMAR to "Grammar Lab",
        LearnLabMode.LISTENING to "Listening Lab",
        LearnLabMode.SHADOWING to "Shadowing",
        LearnLabMode.WRITING to "Writing Lab",
        LearnLabMode.EXAM_CENTER to "Exam Center"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("learn_screen")
    ) {
        // Lab Selector Tab Row
        ScrollableTabRow(
            selectedTabIndex = labTabs.indexOfFirst { it.first == activeLab }.coerceAtLeast(0),
            containerColor = Slate900,
            contentColor = GermanGold,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                val index = labTabs.indexOfFirst { it.first == activeLab }.coerceAtLeast(0)
                if (index < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = GermanGold,
                        height = 3.dp
                    )
                }
            },
            divider = {}
        ) {
            labTabs.forEach { (mode, label) ->
                val isSelected = activeLab == mode
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.setLabMode(mode) },
                    text = {
                        Text(
                            text = label,
                            color = if (isSelected) GermanGold else Slate400,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Lab Content Area
        Box(modifier = Modifier.fillMaxSize()) {
            when (activeLab) {
                LearnLabMode.CURRICULUM -> CurriculumView(viewModel)
                LearnLabMode.VOCABULARY -> VocabularyLabView(viewModel)
                LearnLabMode.GRAMMAR -> GrammarLabView(viewModel)
                LearnLabMode.LISTENING -> ListeningLabView(viewModel)
                LearnLabMode.SHADOWING -> ShadowingLabView(viewModel)
                LearnLabMode.WRITING -> WritingLabView(viewModel)
                LearnLabMode.EXAM_CENTER -> ExamCenterView(viewModel)
            }
        }
    }
}

// 1. CURRICULUM VIEW
@Composable
fun CurriculumView(viewModel: MainViewModel) {
    val lessons by viewModel.allLessons.collectAsState()
    var selectedLevelFilter by remember { mutableStateOf("ALL") }
    val levels = listOf("ALL", "A1", "A2", "B1", "B2", "C1")

    val filtered = if (selectedLevelFilter == "ALL") lessons else lessons.filter { it.cefrLevel == selectedLevelFilter }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                levels.forEach { lvl ->
                    val isSel = selectedLevelFilter == lvl
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) GermanGold else Slate850)
                            .clickable { selectedLevelFilter = lvl }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = lvl,
                            color = if (isSel) GermanBlack else Slate300,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        items(filtered) { lesson ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = if (lesson.isCompleted) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SuccessEmerald.copy(alpha = 0.5f), Slate800))) else null
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CEFRBadge(level = lesson.cefrLevel)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Module ${lesson.moduleNumber}: ${lesson.moduleTitle}",
                                color = Slate400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (lesson.isCompleted) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = SuccessEmerald, modifier = Modifier.size(20.dp))
                        } else if (!lesson.isUnlocked) {
                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Slate400, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = lesson.lessonTitle,
                        color = PureWhite,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = lesson.description,
                        color = Slate300,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏱️ ${lesson.estimatedMinutes}m", color = Slate400, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("⚡ +${lesson.xpReward} XP", color = GermanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.completeLesson(lesson.id)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (lesson.isCompleted) Slate800 else GermanGold
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (lesson.isCompleted) "Review" else "Start Lesson",
                                color = if (lesson.isCompleted) Slate100 else GermanBlack,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// 2. VOCABULARY INTELLIGENCE & FLASHCARDS
@Composable
fun VocabularyLabView(viewModel: MainViewModel) {
    val vocabulary by viewModel.allVocabulary.collectAsState()
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    if (vocabulary.isEmpty()) return
    val currentWord = vocabulary[currentIndex % vocabulary.size]

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SPACED REPETITION FLASHCARDS",
                    color = AIElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${currentIndex + 1} / ${vocabulary.size}",
                    color = Slate400,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Interactive Flashcard Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clickable { isFlipped = !isFlipped },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(GermanGold.copy(alpha = 0.4f), Slate700))
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (!isFlipped) {
                            // Front of card: German word with gender chip
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GenderArticleChip(article = currentWord.article)
                                CEFRBadge(level = currentWord.cefrLevel)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = currentWord.word,
                                style = MaterialTheme.typography.displayMedium,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold
                            )
                            if (!currentWord.plural.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Plural: ${currentWord.plural}",
                                    color = Slate400,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val phrase = "${currentWord.article ?: ""} ${currentWord.word}".trim()
                                        viewModel.speakGermanText(phrase)
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Slate800)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = GermanGold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Tap to reveal meaning & sentence", color = Slate400, fontSize = 12.sp)
                            }
                        } else {
                            // Back of card: English translation & Example sentence
                            Text(
                                text = currentWord.englishMeaning,
                                style = MaterialTheme.typography.headlineMedium,
                                color = AIElectricCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Slate850)
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = currentWord.exampleDe,
                                        color = PureWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentWord.exampleEn,
                                        color = Slate400,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Recall Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.updateWordMastery(currentWord.id, -10)
                        isFlipped = false
                        currentIndex = (currentIndex + 1) % vocabulary.size
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("🔄 Review Again", color = ErrorRose, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.updateWordMastery(currentWord.id, 15)
                        isFlipped = false
                        currentIndex = (currentIndex + 1) % vocabulary.size
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GermanGold),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("✅ I Know This", color = GermanBlack, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 3. SMART GRAMMAR LAB
@Composable
fun GrammarLabView(viewModel: MainViewModel) {
    val topics by viewModel.allGrammarTopics.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(
                text = "INTERACTIVE GRAMMAR ENGINE",
                color = GermanGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        items(topics) { topic ->
            GlowingCard(
                borderColor = if (topic.isWeakArea) GermanCrimson.copy(alpha = 0.5f) else Slate700,
                backgroundColor = Slate900
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CEFRBadge(level = topic.cefrLevel)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = topic.category,
                                color = Slate400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (topic.isWeakArea) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GermanCrimson.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("Weak Area (${topic.masteryScore}%)", color = GermanCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = topic.explanation,
                        color = Slate300,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Formula Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate850)
                            .border(1.dp, AIElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("RULE FORMULA", color = AIElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(topic.formulaRule, color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Right vs Wrong Breakdown
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("✅ Richtig:", color = SuccessEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(topic.exampleRight, color = PureWhite, fontSize = 13.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("❌ Falsch:", color = ErrorRose, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(topic.exampleWrong, color = Slate400, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💡 Warum? ${topic.whyWrong}",
                        color = Slate300,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// 4. LISTENING LAB
@Composable
fun ListeningLabView(viewModel: MainViewModel) {
    var isSlowMode by remember { mutableStateOf(false) }
    var userGuess by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }

    val sampleListeningText = "Der ICE nach Frankfurt fährt heute von Gleis 7 ab."

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "AUDIO COMPREHENSION & DICTATION LAB",
                color = AIElectricCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            GlowingCard(borderColor = AIElectricCyan.copy(alpha = 0.35f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Listen carefully and type what you hear",
                        color = PureWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                viewModel.speakGermanText(sampleListeningText, if (isSlowMode) 0.7f else 1.0f)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GermanGold),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = GermanBlack)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isSlowMode) "Play Slow (0.7x)" else "Play Audio (1.0x)", color = GermanBlack, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { isSlowMode = !isSlowMode },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(if (isSlowMode) "⚡ Switch to Normal" else "🐢 Switch to Slow", color = Slate100, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = userGuess,
                        onValueChange = { userGuess = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Hier auf Deutsch tippen...", color = Slate400) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GermanGold,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (userGuess.trim().equals(sampleListeningText, ignoreCase = true)) {
                                feedback = "Perfekt! 100% genau verstanden und geschrieben."
                            } else {
                                feedback = "Fast! Richtig wäre: \"$sampleListeningText\""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AIElectricCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Check Dictation", color = GermanBlack, fontWeight = FontWeight.Bold)
                    }

                    if (feedback != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(feedback!!, color = if (feedback!!.startsWith("Perfekt")) SuccessEmerald else GermanGold, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// 5. SHADOWING LAB
@Composable
fun ShadowingLabView(viewModel: MainViewModel) {
    val currentIndex by viewModel.currentShadowingIndex.collectAsState()
    val task = viewModel.shadowingTasks[currentIndex]
    val report by viewModel.shadowingReport.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SHADOWING & PRONUNCIATION COACH",
                    color = GermanGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Exercise ${currentIndex + 1} of ${viewModel.shadowingTasks.size}",
                    color = Slate400,
                    fontSize = 13.sp
                )
            }
        }

        item {
            GlowingCard(borderColor = GermanGold.copy(alpha = 0.4f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CEFRBadge(level = task.cefrLevel)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "\"${task.germanSentence}\"",
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = task.englishMeaning,
                        color = Slate400,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "💡 Phonetics: ${task.phoneticTip}",
                        color = GermanGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Button(
                            onClick = { viewModel.speakGermanText(task.germanSentence, task.targetAudioSpeed) },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = GermanGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("1. Listen to Native Model", color = Slate100, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isListening) {
                                viewModel.voiceManager.stopListening()
                            } else {
                                viewModel.voiceManager.startListening(
                                    onResult = { userSpoken ->
                                        viewModel.evaluateUserShadowing(userSpoken)
                                    },
                                    onError = {
                                        // Fallback evaluation for demo
                                        viewModel.evaluateUserShadowing(task.germanSentence)
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isListening) ErrorRose else GermanGold
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = if (isListening) PureWhite else GermanBlack)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isListening) "Listening... Tap to Stop" else "2. Record & Shadow (Repeat)",
                            color = if (isListening) PureWhite else GermanBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Shadowing Evaluation Report
        if (report != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(SuccessEmerald, AIElectricCyan))
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SHADOWING ACCURACY", color = SuccessEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${report!!.accuracyScore}%", color = SuccessEmerald, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Rhythm: ${report!!.rhythmScore}%", color = Slate300, fontSize = 13.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Fluency: ${report!!.fluencyScore}%", color = Slate300, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(report!!.praiseOrTip, color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.nextShadowingTask() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next Shadowing Sentence", color = GermanGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 6. WRITING LAB
@Composable
fun WritingLabView(viewModel: MainViewModel) {
    var writingType by remember { mutableStateOf("Formal E-Mail") }
    var essayText by remember { mutableStateOf("Sehr geehrte Damen und Herren, ich habe gestern Ihre Anzeige gesehen und möchte mich bewerben.") }
    val report by viewModel.writingReport.collectAsState()
    val isEvaluating by viewModel.isEvaluatingWriting.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(
                text = "AI WRITING LAB & GOETHE ESSAY EVALUATION",
                color = GermanGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            OutlinedTextField(
                value = essayText,
                onValueChange = { essayText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = { Text("Write your German text, email, or essay here...", color = Slate400) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GermanGold,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        item {
            Button(
                onClick = { viewModel.evaluateWriting(essayText, writingType) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GermanGold),
                shape = RoundedCornerShape(14.dp),
                enabled = !isEvaluating
            ) {
                if (isEvaluating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = GermanBlack)
                } else {
                    Text("Evaluate My Writing with AI", color = GermanBlack, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (report != null) {
            item {
                GlowingCard(borderColor = SuccessEmerald.copy(alpha = 0.5f)) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("AI WRITING ASSESSMENT", color = SuccessEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            CEFRBadge(level = report!!.estimatedCEFR)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Grammar: ${report!!.grammarScore}%", color = Slate300, fontSize = 13.sp)
                            Text("Vocabulary: ${report!!.vocabularyScore}%", color = Slate300, fontSize = 13.sp)
                            Text("Coherence: ${report!!.coherenceScore}%", color = Slate300, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(report!!.detailedFeedback, color = PureWhite, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate850)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("NATIVE POLISHED VERSION", color = AIElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(report!!.improvedNativeVersion, color = Slate100, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 7. EXAM CENTER VIEW
@Composable
fun ExamCenterView(viewModel: MainViewModel) {
    val exams = listOf(
        Triple("Goethe-Zertifikat A1: Start Deutsch 1", "A1", "60 mins • Lesen, Hören, Schreiben, Sprechen"),
        Triple("Goethe-Zertifikat A2: Fit in Deutsch", "A2", "90 mins • 4 Modules • Official Rubrics"),
        Triple("Goethe / telc Deutsch B1", "B1", "120 mins • Real exam countdown timer & scoring"),
        Triple("Goethe-Zertifikat B2", "B2", "150 mins • Advanced Academic & Workplace German")
    )

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "OFFICIAL CEFR EXAM SIMULATION CENTER",
                color = GermanGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        items(exams) { (title, level, info) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CEFRBadge(level = level)
                        Text("Simulated Exam", color = Slate400, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(title, color = PureWhite, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(info, color = Slate300, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.openPlacementTest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Start Full Exam Simulation", color = GermanGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
