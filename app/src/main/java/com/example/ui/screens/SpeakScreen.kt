package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.ArabicTranslationDetail
import com.example.data.model.ChatMessage
import com.example.data.model.ConversationMode
import com.example.data.model.TargetDialect
import com.example.ui.components.AnimatedAudioWaveform
import com.example.ui.components.CEFRBadge
import com.example.ui.theme.AIElectricCyan
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.GermanBlack
import com.example.ui.theme.GermanCrimson
import com.example.ui.theme.GermanGold
import com.example.ui.theme.PureWhite
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
fun SpeakScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isTutorThinking.collectAsState()
    val activeMode by viewModel.conversationMode.collectAsState()
    val activeScenario by viewModel.activeScenario.collectAsState()
    val completedMilestones by viewModel.completedMilestones.collectAsState()
    val lastAnalysis by viewModel.lastTutorAnalysis.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val audioRms by viewModel.voiceManager.audioRms.collectAsState()
    val handsFree by viewModel.handsFreeMode.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .imePadding()
            .testTag("speak_screen")
    ) {
        // Mode Selector Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ConversationMode.values()) { mode ->
                val isSelected = activeMode == mode
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) GermanGold else Slate800)
                        .clickable { viewModel.setConversationMode(mode) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(mode.icon, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = mode.title,
                            color = if (isSelected) GermanBlack else Slate300,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Active Scenario & Milestone Progress Header
        if (activeScenario != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(GermanGold.copy(alpha = 0.5f), AIElectricCyan.copy(alpha = 0.5f)))
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(activeScenario!!.iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = activeScenario!!.title,
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = activeScenario!!.titleAr,
                                    color = GermanGold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        CEFRBadge(level = activeScenario!!.cefrLevel)
                    }

                    if (activeScenario!!.milestones.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Missionsziele / أهداف السيناريو:", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            activeScenario!!.milestones.forEachIndexed { index, milestone ->
                                val isDone = index in completedMilestones
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isDone) SuccessEmerald else Slate700,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = milestone,
                                        color = if (isDone) SuccessEmerald else Slate300,
                                        fontSize = 12.sp,
                                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Voice Waveform Bar
        if (isSpeaking || isListening) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AnimatedAudioWaveform(
                        isActive = isSpeaking || isListening,
                        isAiSpeaking = isSpeaking
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isListening) "🎙️ Ich höre zu... Sprich jetzt!" else "🔊 DeutschAI spricht...",
                        color = if (isListening) ErrorRose else GermanGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Chat Message Transcript
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(
                    msg = msg,
                    dialect = userProfile?.targetDialect ?: TargetDialect.MSA,
                    viewModel = viewModel
                )
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Slate850)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = GermanGold, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("DeutschAI analysiert Grammatik & Satzbau...", color = Slate400, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Voice & Input Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp)
        ) {
            // Hands-Free & Quick Audio Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = handsFree,
                        onCheckedChange = { viewModel.toggleHandsFree() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GermanGold,
                            checkedTrackColor = GermanGold.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Hands-Free Modus", color = Slate300, fontSize = 12.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate800)
                            .clickable {
                                val lastAiMsg = messages.lastOrNull { it.sender == "AI" }?.text ?: ""
                                viewModel.speakGermanText(lastAiMsg, 0.75f)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("🐢 Langsam 0.7x", color = GermanGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Mic Button & Text Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Giant Push-to-talk Mic Button
                val micBrush = if (isListening) {
                    Brush.linearGradient(listOf(ErrorRose, ErrorRose))
                } else {
                    Brush.linearGradient(listOf(GermanGold, AIElectricCyan))
                }
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(micBrush)
                        .clickable {
                            if (isListening) {
                                viewModel.voiceManager.stopListening()
                            } else {
                                viewModel.voiceManager.startListening(
                                    onResult = { recognizedText ->
                                        viewModel.sendUserMessage(recognizedText)
                                    },
                                    onError = {
                                        // If speech recognition is unavailable, user can type
                                    }
                                )
                            }
                        }
                        .testTag("push_to_talk_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = GermanBlack,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Text Input field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Auf Deutsch sprechen oder tippen...", color = Slate400, fontSize = 13.sp) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendUserMessage(inputText)
                                    inputText = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = GermanGold)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GermanGold,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageBubble(
    msg: ChatMessage,
    dialect: TargetDialect,
    viewModel: MainViewModel
) {
    val isUser = msg.sender == "USER"
    var showTranslation by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .background(if (isUser) Slate800 else Slate850)
                .border(
                    1.dp,
                    if (isUser) Slate700 else GermanGold.copy(alpha = 0.25f),
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isUser) "Du (Alex)" else "DeutschAI Tutor",
                    color = if (isUser) AIElectricCyan else GermanGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                if (!isUser) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.speakGermanText(msg.text, 1.0f) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Play Audio", tint = Slate300, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { showTranslation = !showTranslation },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Translate, contentDescription = "Translation", tint = if (showTranslation) GermanGold else Slate400, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = msg.text,
                color = PureWhite,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // Arabic Translation & Contextual Breakdown
            if (showTranslation) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate900)
                        .padding(10.dp)
                ) {
                    val detail = msg.translationDetail
                    val mainTrans = detail?.contextualTranslation ?: msg.translation ?: ""

                    Text(
                        text = "الترجمة السياقية:",
                        color = GermanGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = mainTrans,
                        color = PureWhite,
                        fontSize = 13.sp
                    )

                    if (dialect == TargetDialect.DARIJA && detail?.darijaAlternative != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "بالدارجة المغربية: ${detail.darijaAlternative}",
                            color = AIElectricCyan,
                            fontSize = 12.sp
                        )
                    }

                    if (detail != null && detail.wordByWord.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تفكيك الكلمات والمفردات:",
                            color = Slate400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            detail.wordByWord.forEach { word ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Slate800)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${word.german} ➜ ${word.arabic}",
                                        color = Slate300,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    if (!detail?.grammarNotes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "💡 ملاحظة نحوية: ${detail?.grammarNotes}",
                            color = SuccessEmerald,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Grammar Feedback Card
            if (!isUser && (!msg.grammarFeedback.isNullOrBlank() || !msg.grammarFeedbackAr.isNullOrBlank())) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate900)
                        .border(1.dp, GermanCrimson.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "🔍 تصحيح وتوجيه نحوي:",
                            color = GermanGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (!msg.grammarFeedback.isNullOrBlank()) {
                            Text(text = msg.grammarFeedback, color = Slate300, fontSize = 11.sp)
                        }
                        if (!msg.grammarFeedbackAr.isNullOrBlank()) {
                            Text(text = msg.grammarFeedbackAr, color = PureWhite, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (!isUser && msg.naturalAlternative != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "✨ الأسلوب الأكثر عفوية: ${msg.naturalAlternative}",
                    color = AIElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
