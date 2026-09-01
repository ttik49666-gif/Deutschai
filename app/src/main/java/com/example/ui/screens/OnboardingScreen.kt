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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.TargetDialect
import com.example.data.model.TutorPersonality
import com.example.ui.components.GlowingCard
import com.example.ui.theme.AIElectricCyan
import com.example.ui.theme.GermanBlack
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

enum class ExperiencePath {
    COMPLETE_BEGINNER,
    PRIOR_KNOWLEDGE
}

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var learnerName by remember { mutableStateOf("") }
    var selectedDialect by remember { mutableStateOf(TargetDialect.MOROCCAN_DARIJA) }
    var experiencePath by remember { mutableStateOf(ExperiencePath.COMPLETE_BEGINNER) }
    var selectedGoal by remember { mutableStateOf("💼 العمل والاستقرار في ألمانيا (Career & Relocation)") }
    var selectedTime by remember { mutableIntStateOf(15) }
    var selectedPersonality by remember { mutableStateOf(TutorPersonality.FRIENDLY) }

    val goals = listOf(
        "💼 العمل والاستقرار في ألمانيا (Career & Relocation)",
        "🗣️ الطلاقة والمحادثة اليومية (Everyday Fluency)",
        "📝 التحضير لامتحان Goethe / telc (Exam Preparation)",
        "🎓 الدراسة والجامعة في ألمانيا (University Study)",
        "✈️ السفر والسياحة (Travel & Cultural Immersion)"
    )

    val times = listOf(10, 15, 20, 30)

    val totalSteps = 4

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("onboarding_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Step progress
        item {
            LinearProgressIndicator(
                progress = { ((step + 1) / totalSteps.toFloat()) },
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
                // Step 0: Welcome & Dialect Selection
                item {
                    GlowingCard(borderColor = GermanGold.copy(alpha = 0.5f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DEUTSCHAI ULTIMATE", color = GermanGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Willkommen! مرحباً بك", style = MaterialTheme.typography.headlineMedium, color = PureWhite, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "مدربك الذكي المخصص لتعلم الألمانية بطلاقة. شروحات فورية، تشخيص الأخطاء، ومحادثات صوتية حية.",
                                color = Slate300,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                item {
                    Text("ما اسمك أو اللقب المفضل لك؟", color = PureWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = learnerName,
                        onValueChange = { learnerName = it },
                        placeholder = { Text("مثال: يوسف / سارة / Alex", color = Slate400) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GermanGold,
                            unfocusedBorderColor = Slate800,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                item {
                    Text("اختر لغة الشرح والتوضيح المفضلة:", color = PureWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("المعلم الآلي سيشرح القواعد والمفردات بلغتك المختارة", color = Slate400, fontSize = 12.sp)
                }

                item {
                    // Moroccan Darija Card
                    val isDarija = selectedDialect == TargetDialect.MOROCCAN_DARIJA
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedDialect = TargetDialect.MOROCCAN_DARIJA },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDarija) Slate850 else Slate900),
                        border = if (isDarija) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan))) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🇲🇦", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("الدارجة المغربية (Moroccan Darija)", color = PureWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("شروحات بالدارجة، أمثلة مبسطة، ومقارنات لغوية ذكية", color = Slate400, fontSize = 12.sp)
                            }
                            if (isDarija) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GermanGold)
                            }
                        }
                    }
                }

                item {
                    // MSA Card
                    val isMSA = selectedDialect == TargetDialect.MODERN_STANDARD_ARABIC
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedDialect = TargetDialect.MODERN_STANDARD_ARABIC },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isMSA) Slate850 else Slate900),
                        border = if (isMSA) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan))) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌍", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("العربية الفصحى (Modern Standard Arabic)", color = PureWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("شروحات وافية بالعربية الفصحى ومصطلحات دقيقة", color = Slate400, fontSize = 12.sp)
                            }
                            if (isMSA) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GermanGold)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Step 1: EXPERIENCE ASSESSMENT (The Core Branching Decision)
                item {
                    Text("ما هو مستواك وخبرتك في اللغة الألمانية؟", style = MaterialTheme.typography.headlineMedium, color = PureWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("حدد نقطة انطلاقك لتخصيص مسارك التعليمي بدقة:", color = Slate400, fontSize = 13.sp)
                }

                // Option 1: Complete Beginner
                item {
                    val isBeg = experiencePath == ExperiencePath.COMPLETE_BEGINNER
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { experiencePath = ExperiencePath.COMPLETE_BEGINNER },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isBeg) Slate850 else Slate900),
                        border = if (isBeg) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SuccessEmerald, GermanGold))) else null
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🌱", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("مبتدئ تماماً (من الصفر)", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SuccessEmerald.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("A1.1 Starter Pack", color = SuccessEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "لم أدرس الألمانية من قبل. ابدأ معي فوراً بحروف الأبجدية، النطق الصوتي الصحيح، الأرقام 0-100، الضمائر، والتحيات الأساسية.",
                                color = Slate300,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "⚡ سيتم تعيين مستواك تلقائياً A1.1 والبدء فوراً دون الحاجة لاختبار تحديد المستوى.",
                                color = GermanGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Option 2: Prior Knowledge
                item {
                    val isPrior = experiencePath == ExperiencePath.PRIOR_KNOWLEDGE
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { experiencePath = ExperiencePath.PRIOR_KNOWLEDGE },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isPrior) Slate850 else Slate900),
                        border = if (isPrior) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AIElectricCyan, GermanGold))) else null
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎯", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("لدي معرفة ودراسة سابقة", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AIElectricCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("CEFR Placement Test", color = AIElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "لدي معرفة سابقة بالقواعد والمفردات. أرغب في خوض اختبار تحديد المستوى التكيفي (12 سؤالاً في القواعد، المفردات، القراءة، والاستماع) لمعايرة مستواي بدقة من A1 إلى C1.",
                                color = Slate300,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "🚀 سيتوجه بك التطبيق مباشرة إلى اختبار تحديد المستوى المتعدد المهارات.",
                                color = AIElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            2 -> {
                // Step 2: Goals & Daily Time
                item {
                    Text("ما هو هدفك الأساسي من تعلم الألمانية؟", style = MaterialTheme.typography.headlineMedium, color = PureWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("سيتم توجيه المحادثات ومفردات اليوم نحو هدفك", color = Slate400, fontSize = 13.sp)
                }

                items(goals) { goal ->
                    val isSel = selectedGoal == goal
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGoal = goal },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSel) Slate850 else Slate900),
                        border = if (isSel) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan))) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(goal, color = PureWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            if (isSel) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GermanGold)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("كم دقيقة ترغب في التدرب يومياً؟", color = PureWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$t", color = if (isSel) GermanBlack else PureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text("دقائق/يوم", color = if (isSel) GermanBlack else Slate400, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Step 3: Tutor Personality & Summary Confirmation
                item {
                    Text("اختر شخصية المعلم الآلي (AI Tutor):", style = MaterialTheme.typography.headlineMedium, color = PureWhite, fontWeight = FontWeight.Bold)
                }

                items(TutorPersonality.entries.toTypedArray()) { p ->
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

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    GlowingCard(borderColor = GermanGold.copy(alpha = 0.5f)) {
                        Column {
                            Text("ملخص خطتك التعليمية:", color = GermanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (experiencePath == ExperiencePath.COMPLETE_BEGINNER) {
                                    "🌱 المسار: الأساسيات من الصفر (A1.1 Starter Pack)\n🌍 لغة الشرح: ${if (selectedDialect == TargetDialect.MOROCCAN_DARIJA) "الدارجة المغربية" else "العربية الفصحى"}\n⏱️ الالتزام: $selectedTime دقيقة يومياً"
                                } else {
                                    "🎯 المسار: اختبار تحديد المستوى التكيفي (12 سؤالاً)\n🌍 لغة الشرح: ${if (selectedDialect == TargetDialect.MOROCCAN_DARIJA) "الدارجة المغربية" else "العربية الفصحى"}\n⏱️ الالتزام: $selectedTime دقيقة يومياً"
                                },
                                color = PureWhite,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }

        // CTA Button
        item {
            Button(
                onClick = {
                    if (step < totalSteps - 1) {
                        step++
                    } else {
                        val name = if (learnerName.isNotBlank()) learnerName.trim() else "Learner"
                        if (experiencePath == ExperiencePath.COMPLETE_BEGINNER) {
                            viewModel.onboardAsBeginner(
                                name = name,
                                goal = selectedGoal,
                                minutes = selectedTime,
                                personality = selectedPersonality,
                                dialect = selectedDialect
                            )
                        } else {
                            viewModel.onboardWithPriorKnowledge(
                                name = name,
                                goal = selectedGoal,
                                minutes = selectedTime,
                                personality = selectedPersonality,
                                dialect = selectedDialect
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GermanGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (step < totalSteps - 1) {
                            "المتابعة (Weiter)"
                        } else if (experiencePath == ExperiencePath.COMPLETE_BEGINNER) {
                            "🚀 ابدأ الأساسيات من الصفر (A1.1 Starter)"
                        } else {
                            "🎯 ابدأ اختبار تحديد المستوى (Start Test)"
                        },
                        color = GermanBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GermanBlack)
                }
            }
        }
    }
}
