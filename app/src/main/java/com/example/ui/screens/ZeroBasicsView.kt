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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TargetDialect
import com.example.ui.components.GlowingCard
import com.example.ui.theme.AIElectricCyan
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
import com.example.ui.viewmodel.MainViewModel

enum class ZeroBasicsTab {
    ALPHABET,
    NUMBERS,
    GREETINGS,
    PRONOUNS,
    PRACTICE
}

data class AlphabetLetter(
    val letter: String,
    val phoneticAr: String,
    val exampleDe: String,
    val exampleMeaningAr: String,
    val exampleMeaningDarija: String,
    val isSpecial: Boolean = false
)

data class DiphthongRule(
    val combination: String,
    val pronunciationDe: String,
    val explanationAr: String,
    val explanationDarija: String,
    val examples: List<Pair<String, String>>
)

data class NumberItem(
    val digit: Int,
    val wordDe: String,
    val meaningAr: String,
    val meaningDarija: String,
    val ruleNote: String? = null
)

data class GreetingPhrase(
    val phraseDe: String,
    val phraseAr: String,
    val phraseDarija: String,
    val contextNote: String,
    val isFormal: Boolean = false
)

data class PronounItem(
    val pronounDe: String,
    val pronounAr: String,
    val pronounDarija: String,
    val personType: String,
    val exampleSentence: String
)

@Composable
fun ZeroBasicsView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isDarija = userProfile?.targetDialect == TargetDialect.MOROCCAN_DARIJA

    var activeTab by remember { mutableStateOf(ZeroBasicsTab.ALPHABET) }

    val tabs = listOf(
        ZeroBasicsTab.ALPHABET to "🔤 Alphabet / الحروف",
        ZeroBasicsTab.NUMBERS to "🔢 Numbers / الأرقام",
        ZeroBasicsTab.GREETINGS to "👋 Greetings / التحيات",
        ZeroBasicsTab.PRONOUNS to "👥 Pronouns / الضمائر",
        ZeroBasicsTab.PRACTICE to "🎯 Drills / تمارين"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("zero_basics_screen")
    ) {
        // Sub-tabs Header
        ScrollableTabRow(
            selectedTabIndex = tabs.indexOfFirst { it.first == activeTab }.coerceAtLeast(0),
            containerColor = Slate900,
            contentColor = GermanGold,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                val index = tabs.indexOfFirst { it.first == activeTab }.coerceAtLeast(0)
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
            tabs.forEach { (tab, label) ->
                val isSel = activeTab == tab
                Tab(
                    selected = isSel,
                    onClick = { activeTab = tab },
                    text = {
                        Text(
                            text = label,
                            color = if (isSel) GermanGold else Slate400,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (activeTab) {
                ZeroBasicsTab.ALPHABET -> AlphabetSection(viewModel, isDarija)
                ZeroBasicsTab.NUMBERS -> NumbersSection(viewModel, isDarija)
                ZeroBasicsTab.GREETINGS -> GreetingsSection(viewModel, isDarija)
                ZeroBasicsTab.PRONOUNS -> PronounsSection(viewModel, isDarija)
                ZeroBasicsTab.PRACTICE -> BasicsPracticeSection(viewModel, isDarija)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. ALPHABET & PHONETICS SECTION
// -------------------------------------------------------------
@Composable
private fun AlphabetSection(viewModel: MainViewModel, isDarija: Boolean) {
    val alphabetList = remember {
        listOf(
            AlphabetLetter("A a", "آه (A)", "der Apfel", "التفاحة", "التفاحة"),
            AlphabetLetter("B b", "بِي (Be)", "das Buch", "الكتاب", "الكتاب"),
            AlphabetLetter("C c", "تْسِي (Tse)", "der Computer", "الحاسوب", "البيسي / الحاسوب"),
            AlphabetLetter("D d", "دِي (De)", "das Danke", "الشكر", "الشكر"),
            AlphabetLetter("E e", "إِيه (E)", "der Elefant", "الفيل", "الفيل"),
            AlphabetLetter("F f", "إِف (Ef)", "der Freund", "الصديق", "الصديق / الصاحب"),
            AlphabetLetter("G g", "غِي (Ge)", "der Garten", "الحديقة", "الجريدة / الحديقة"),
            AlphabetLetter("H h", "هَا (Ha)", "das Haus", "المنزل", "الدار / الدار"),
            AlphabetLetter("I i", "إِي (I)", "der Igel", "القنفذ", "القنفذ"),
            AlphabetLetter("J j", "يُوت (Jot)", "das Jahr", "السنة", "العام / السنة"),
            AlphabetLetter("K k", "كَا (Ka)", "die Katze", "القطة", "القطة / المشة"),
            AlphabetLetter("L l", "إِل (El)", "die Lampe", "المصباح", "البولة / المصباح"),
            AlphabetLetter("M m", "إِم (Em)", "der Mann", "الرجل", "الراجل"),
            AlphabetLetter("N n", "إِن (En)", "die Nacht", "الليل", "الليل"),
            AlphabetLetter("O o", "أُو (O)", "die Orange", "البرتقالة", "الليمونة / البرتقالة"),
            AlphabetLetter("P p", "بِي شديدة (Pe)", "der Pass", "جواز السفر", "الباسبور / جواز السفر"),
            AlphabetLetter("Q q", "كُو (Ku)", "die Quelle", "المصدر / الينبوع", "المنبع / العين"),
            AlphabetLetter("R r", "إِر (Er)", "die Reise", "الرحلة / السفر", "السفر / الرحلة"),
            AlphabetLetter("S s", "إِس (Es)", "die Sonne", "الشمس", "الشمس"),
            AlphabetLetter("T t", "تِي (Te)", "der Tag", "اليوم", "النهار / اليوم"),
            AlphabetLetter("U u", "أُو مضمومة (U)", "die Uhr", "الساعة", "المكانة / الساعة"),
            AlphabetLetter("V v", "فَاو (Vau)", "der Vater", "الأب", "الوالد / الأب"),
            AlphabetLetter("W w", "ڤِي (We)", "das Wasser", "الماء", "الما / الماء"),
            AlphabetLetter("X x", "إِكْس (Iks)", "das Xylofon", "الزيلوفون", "آلة الزيلوفون"),
            AlphabetLetter("Y y", "إِبْسِيلُون (Ypsilon)", "das Yoga", "اليوغا", "اليوغا"),
            AlphabetLetter("Z z", "تْسِت (Tset)", "der Zug", "القطار", "التران / القطار"),
            // Special Characters (Umlaute & Eszett)
            AlphabetLetter("Ä ä", "إِيه مُمَالة (A-Umlaut)", "die Äpfel", "التفاح (جمع)", "التفاح", isSpecial = true),
            AlphabetLetter("Ö ö", "أُو دائرية (O-Umlaut)", "das Öl / schön", "الزيت / جميل", "الزيت / زوين", isSpecial = true),
            AlphabetLetter("Ü ü", "إِيو مضمومة (U-Umlaut)", "über / die Tür", "فوق / الباب", "فوق / الباب", isSpecial = true),
            AlphabetLetter("ß", "إِسْتْسِت (Eszett / Sharp S)", "die Straße", "الشارع", "الشارع / الزنقة", isSpecial = true)
        )
    }

    val diphthongs = remember {
        listOf(
            DiphthongRule(
                combination = "ei / ai",
                pronunciationDe = "Aussprache: wie 'ay' [aɪ]",
                explanationAr = "يُنطق دائماً كصوت 'أَيْ' مفتوحاً (مثل mein = ملكي، dein = ملكك).",
                explanationDarija = "كايتنطق بحال 'أَيْ' (بحال كلمة 'بين' بالدارجة، مثلاً mein = ديالي).",
                examples = listOf("mein" to "ملكي", "nein" to "لا", "eins" to "واحد", "Arbeit" to "عمل")
            ),
            DiphthongRule(
                combination = "eu / äu",
                pronunciationDe = "Aussprache: wie 'oy' [ɔʏ]",
                explanationAr = "يُنطق كصوت 'أوْيْ' (مثل Deutsch = ألماني، Europa = أوروبا، Häuser = بيوت).",
                explanationDarija = "كايتنطق 'أوْيْ' مجموع بحال Deutsch (ألماني) و Häuser (ديور).",
                examples = listOf("Deutsch" to "ألماني", "Freund" to "صديق", "neu" to "جديد", "Häuser" to "منازل")
            ),
            DiphthongRule(
                combination = "ie",
                pronunciationDe = "Aussprache: langes 'i' [iː]",
                explanationAr = "يُنطق كياء ممدودة وطويلة 'إِييي' (مثل Bier = بيرة، Liebe = حب، wie = كيف).",
                explanationDarija = "كايتنطق 'إِي' طويلة وممدودة بحال كلمة 'فيك' (Bier, Liebe, nie).",
                examples = listOf("Bier" to "مشروب", "Liebe" to "حب", "sieben" to "سبعة", "wie" to "كيف")
            ),
            DiphthongRule(
                combination = "ch (Ich-Laut vs. Ach-Laut)",
                pronunciationDe = "Weich [ç] nach e,i / Hart [x] nach a,o,u",
                explanationAr = "ينطق شين ناعمة مرققة بعد e, i (مثل ich, nicht) وينطق خاء مفخمة بعد a, o, u (مثل Nacht, Buch).",
                explanationDarija = "كايتنطق 'ش' رطبة بعد e و i (بحال ich, Milch)، و 'خ' صريحة بعد a, o, u (بحال Nacht, Buch).",
                examples = listOf("ich" to "أنا (شين ناعمة)", "Milch" to "حليب", "Nacht" to "ليل (خاء)", "Buch" to "كتاب (خاء)")
            ),
            DiphthongRule(
                combination = "sp / st",
                pronunciationDe = "Aussprache: 'schp' / 'scht' am Wortanfang",
                explanationAr = "في أول الكلمة ينطق كصوت 'شب' و 'شت' (مثل Sport = شبورت، Stadt = شتات).",
                explanationDarija = "فأول الكلمة كايتنطق 'شب' و 'شت' (Sport كاتتقرى Schport, Stadt كاتتقرى Schtadt).",
                examples = listOf("Sport" to "رياضة", "Stadt" to "مدينة", "sprechen" to "يتحدث", "Student" to "طالب")
            )
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Intro Card
        item {
            GlowingCard(borderColor = GermanGold.copy(alpha = 0.5f)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DAS DEUTSCHE ALPHABET",
                            color = GermanGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "30 Buchstaben & Laute",
                            color = AIElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الأبجدية والنطق الصوتي من الصفر",
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDarija) {
                            "ضغط على أي حرف باش تسمع النطق الألماني الفصيح وتشوف الكلمة والشرح بالدارجة."
                        } else {
                            "اضغط على أي حرف للاستماع إلى النطق الألماني الأصيل مع مثال توضيحي وترجمة."
                        },
                        color = Slate300,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section Title: Letters
        item {
            Text(
                text = "1. Die 26 Standardbuchstaben & Umlaute (الحروف الأساسية)",
                color = PureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Letters Grid Cards
        items(alphabetList) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.speakGermanText("${item.letter}. ${item.exampleDe}")
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (item.isSpecial) Slate850 else Slate900),
                border = if (item.isSpecial) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan))) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (item.isSpecial) GermanGold.copy(alpha = 0.2f) else Slate800),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.letter.split(" ").firstOrNull() ?: item.letter,
                            color = if (item.isSpecial) GermanGold else PureWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.letter,
                                color = PureWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.phoneticAr,
                                color = GermanGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Bsp: ${item.exampleDe}",
                                color = AIElectricCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${if (isDarija) item.exampleMeaningDarija else item.exampleMeaningAr})",
                                color = Slate400,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.speakGermanText("${item.letter}. ${item.exampleDe}") }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Pronounce",
                            tint = GermanGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Section Title: Diphthongs & Special Combinations
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "2. Die Diphthonge & Ausspracheregeln (التراكيب الصوتية)",
                color = PureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "أهم التراكيب الصوتية التي تميز النطق الألماني الصحيح",
                color = Slate400,
                fontSize = 12.sp
            )
        }

        items(diphthongs) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AIElectricCyan.copy(alpha = 0.4f), Slate800)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rule.combination,
                            color = GermanGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = rule.pronunciationDe,
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isDarija) rule.explanationDarija else rule.explanationAr,
                        color = PureWhite,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Beispiele (أمثلة مع النطق):",
                        color = Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rule.examples.forEach { (word, trans) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Slate850)
                                    .clickable { viewModel.speakGermanText(word) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(word, color = AIElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = GermanGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. NUMBERS SECTION (0 - 100)
// -------------------------------------------------------------
@Composable
private fun NumbersSection(viewModel: MainViewModel, isDarija: Boolean) {
    val coreNumbers = remember {
        listOf(
            NumberItem(0, "null", "صفر", "زيرو / صفر"),
            NumberItem(1, "eins", "واحد", "واحد"),
            NumberItem(2, "zwei", "اثنان", "جوج"),
            NumberItem(3, "drei", "ثلاثة", "تلاتة"),
            NumberItem(4, "vier", "أربعة", "ربعة"),
            NumberItem(5, "fünf", "خمسة", "خمسة"),
            NumberItem(6, "sechs", "ستة", "ستة"),
            NumberItem(7, "sieben", "سبعة", "سبعة"),
            NumberItem(8, "acht", "ثمانية", "تمنية"),
            NumberItem(9, "neun", "تسعة", "تسعود"),
            NumberItem(10, "zehn", "عشرة", "عشرة"),
            NumberItem(11, "elf", "أحد عشر", "حضاش"),
            NumberItem(12, "zwölf", "اثنا عشر", "طناش")
        )
    }

    val teenNumbers = remember {
        listOf(
            NumberItem(13, "dreizehn", "ثلاثة عشر (3+10)", "تلطاش"),
            NumberItem(14, "vierzehn", "أربعة عشر (4+10)", "ربعطاش"),
            NumberItem(15, "fünfzehn", "خمسة عشر (5+10)", "خمسطاش"),
            NumberItem(16, "sechszehn -> sechzehn", "ستة عشر (حذف حرف s)", "سطاش", "💡 انتبه: تحذف s من sechs لتصبح sechzehn"),
            NumberItem(17, "siebenzehn -> siebzehn", "سبعة عشر (حذف en)", "سبعطاش", "💡 انتبه: تحذف en من sieben لتصبح siebzehn"),
            NumberItem(18, "achtzehn", "ثمانية عشر (8+10)", "تمنطاش"),
            NumberItem(19, "neunzehn", "تسعة عشر (9+10)", "تسعطاش")
        )
    }

    val tensNumbers = remember {
        listOf(
            NumberItem(20, "zwanzig", "عشرون", "عشرين"),
            NumberItem(30, "dreißig", "ثلاثون (تكتب بـ ß)", "تلاتين", "💡 انتبه: الوحيدة التي تكتب بـ ß بدلاً من zig"),
            NumberItem(40, "vierzig", "أربعون", "ربعين"),
            NumberItem(50, "fünfzig", "خمسون", "خمسين"),
            NumberItem(60, "sechzig", "ستون (حذف s)", "ستين", "💡 انتبه: تحذف s لتصبح sechzig"),
            NumberItem(70, "siebzig", "سبعون (حذف en)", "سبعين", "💡 انتبه: تحذف en لتصبح siebzig"),
            NumberItem(80, "achtzig", "ثمانون", "تمنين"),
            NumberItem(90, "neunzig", "تسعون", "تسعين"),
            NumberItem(100, "(ein)hundert", "مئة", "مية")
        )
    }

    val compoundExamples = remember {
        listOf(
            Triple(21, "einundzwanzig", "1 + und + 20 (واحد وعشرون)"),
            Triple(25, "fünfundzwanzig", "5 + und + 20 (خمسة وعشرون)"),
            Triple(37, "siebenunddreißig", "7 + und + 30 (سبعة وثلاثون)"),
            Triple(48, "achtundvierzig", "8 + und + 40 (ثمانية وأربعون)"),
            Triple(99, "neunundneunzig", "9 + und + 90 (تسعة وتسعون)")
        )
    }

    var quizTargetNumber by remember { mutableIntStateOf(21) }
    var quizUserGuessed by remember { mutableStateOf<String?>(null) }
    var quizFeedback by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            GlowingCard(borderColor = GermanGold.copy(alpha = 0.5f)) {
                Column {
                    Text("DIE ZAHLEN 0 - 100", color = GermanGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("الأرقام وقاعدة العشرات المعكوسة", style = MaterialTheme.typography.titleLarge, color = PureWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDarija) {
                            "الألمانية كتشبه للعربية تماماً فقراءة الأرقام: كانقراو الوحدات أولاً عاد العشرات مع كلمة und (و)، بحال 21 = ein-und-zwanzig!"
                        } else {
                            "في الألمانية تقرأ الأرقام تماماً مثل اللغة العربية: نبدأ بالآحاد أولاً ثم العشرات ونربط بينهما بكلمة und (و)، مثل 21 = einundzwanzig (واحد وعشرون)."
                        },
                        color = Slate300,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section 1: Core 0-12
        item {
            Text(
                text = "1. Grundzahlen (0 bis 12) - الأرقام الأساسية",
                color = PureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(coreNumbers) { item ->
            NumberRowCard(item, isDarija) { viewModel.speakGermanText("${item.digit}. ${item.wordDe}") }
        }

        // Section 2: Teens 13-19
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "2. Die Zehner (13 bis 19: الرقم + zehn)",
                color = PureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(teenNumbers) { item ->
            NumberRowCard(item, isDarija) { viewModel.speakGermanText("${item.digit}. ${item.wordDe}") }
        }

        // Section 3: Tens 20-100
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "3. Die Zehnerzahlen (20 bis 100)",
                color = PureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(tensNumbers) { item ->
            NumberRowCard(item, isDarija) { viewModel.speakGermanText("${item.digit}. ${item.wordDe}") }
        }

        // Section 4: German Inverted Rule Explained
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "4. Zusammengesetzte Zahlen (الأرقام المركبة: الآحاد + und + العشرات)",
                color = GermanGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(compoundExamples) { (digit, wordDe, explanation) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.speakGermanText("$digit. $wordDe") },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AIElectricCyan, GermanGold)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GermanGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$digit", color = GermanGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(wordDe, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(explanation, color = Slate400, fontSize = 12.sp)
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = GermanGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Section 5: Interactive Number Quiz Box
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan)))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎯 Interactive Number Challenge", color = GermanGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        IconButton(onClick = {
                            val options = listOf(7, 14, 21, 35, 42, 59, 68, 73, 84, 99)
                            quizTargetNumber = options.random()
                            quizUserGuessed = null
                            quizFeedback = null
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Next number", tint = Slate300)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ما هو المقابل الألماني للرقم التالي؟",
                        color = PureWhite,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate900)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$quizTargetNumber",
                                color = GermanGold,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            IconButton(onClick = {
                                val germanWords = mapOf(
                                    7 to "sieben", 14 to "vierzehn", 21 to "einundzwanzig",
                                    35 to "fünfunddreißig", 42 to "zweiundvierzig", 59 to "neunundfünfzig",
                                    68 to "achtundsechzig", 73 to "dreiundsiebzig", 84 to "vierundachtzig", 99 to "neunundneunzig"
                                )
                                val w = germanWords[quizTargetNumber] ?: "$quizTargetNumber"
                                viewModel.speakGermanText(w)
                            }) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen to answer", tint = AIElectricCyan)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val quizOptions = remember(quizTargetNumber) {
                        val correct = when (quizTargetNumber) {
                            7 -> "sieben"
                            14 -> "vierzehn"
                            21 -> "einundzwanzig"
                            35 -> "fünfunddreißig"
                            42 -> "zweiundvierzig"
                            59 -> "neunundfünfzig"
                            68 -> "achtundsechzig"
                            73 -> "dreiundsiebzig"
                            84 -> "vierundachtzig"
                            99 -> "neunundneunzig"
                            else -> "einundzwanzig"
                        }
                        val dist1 = "siebzehn"
                        val dist2 = "zwanzigein"
                        val dist3 = "dreiundfünfzig"
                        listOf(correct, dist1, dist2, dist3).shuffled()
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        quizOptions.forEach { opt ->
                            val isCorrectOpt = opt in listOf("sieben", "vierzehn", "einundzwanzig", "fünfunddreißig", "zweiundvierzig", "neunundfünfzig", "achtundsechzig", "dreiundsiebzig", "vierundachtzig", "neunundneunzig")
                            val isChosen = quizUserGuessed == opt

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isChosen) (if (isCorrectOpt) SuccessEmerald.copy(alpha = 0.2f) else GermanCrimson.copy(alpha = 0.2f)) else Slate900)
                                    .border(1.dp, if (isChosen) (if (isCorrectOpt) SuccessEmerald else GermanCrimson) else Slate800, RoundedCornerShape(10.dp))
                                    .clickable {
                                        quizUserGuessed = opt
                                        viewModel.speakGermanText(opt)
                                        if (isCorrectOpt) {
                                            quizFeedback = "✅ Richtig! أحسنت، نطق ممتاز وإجابة صحيحة."
                                        } else {
                                            quizFeedback = "❌ Falsch! تذكر قاعدة الآحاد أولاً مع und."
                                        }
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(opt, color = PureWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    if (isChosen && isCorrectOpt) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    if (quizFeedback != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(quizFeedback!!, color = if (quizFeedback!!.startsWith("✅")) SuccessEmerald else GermanCrimson, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberRowCard(item: NumberItem, isDarija: Boolean, onSpeak: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSpeak() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate800),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${item.digit}",
                    color = GermanGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.wordDe,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = if (isDarija) item.meaningDarija else item.meaningAr,
                    color = Slate400,
                    fontSize = 12.sp
                )
                if (item.ruleNote != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.ruleNote,
                        color = AIElectricCyan,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(onClick = onSpeak) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Speak",
                    tint = GermanGold,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 3. GREETINGS & INTRODUCTIONS SECTION
// -------------------------------------------------------------
@Composable
private fun GreetingsSection(viewModel: MainViewModel, isDarija: Boolean) {
    val greetingsList = remember {
        listOf(
            GreetingPhrase("Hallo!", "مرحباً", "أهلاً / السلام", "تحية غير رسمية تستخدم في أي وقت ومع الجميع."),
            GreetingPhrase("Guten Morgen!", "صباح الخير", "صباح الخير", "تستخدم في الصباح حتى الساعة 11:00."),
            GreetingPhrase("Guten Tag!", "طاب يومك / مرحباً", "نهاركم مبروك", "التحية العامة الأكثر استخداماً خلال النهار."),
            GreetingPhrase("Guten Abend!", "مساء الخير", "مساء الخير", "تستخدم ابتداءً من الساعة 18:00."),
            GreetingPhrase("Gute Nacht!", "تصبح على خير", "تصبح على خير", "💡 انتبه: تستخدم فقط عند الذهاب للنوم وليس للترحيب!"),
            GreetingPhrase("Tschüss!", "إلى اللقاء (غير رسمي)", "بسلامة", "وداع غير رسمي بين الأصدقاء والعائلة."),
            GreetingPhrase("Auf Wiedersehen!", "إلى اللقاء (رسمي)", "بالسلامة (رسمي)", "وداع رسمي في المكاتب والمحلات والمطاعم.", isFormal = true),
            GreetingPhrase("Wie geht es Ihnen?", "كيف حال سيادتكم؟ (رسمي)", "كيداير حضرتك؟", "سؤال رسمي عن الحال بصيغة الاحترام.", isFormal = true),
            GreetingPhrase("Wie geht's? / Wie geht es dir?", "كيف حالك؟ (غير رسمي)", "كيداير؟ / لاباس عليك؟", "سؤال ودي عن الحال بين الأصدقاء."),
            GreetingPhrase("Mir geht es gut, danke!", "أنا بخير، شكراً لك!", "أنا بخير، شكراً!", "الرد المثالي والمهذب على السؤال عن الحال.")
        )
    }

    val dialogueSimulationSteps = remember {
        listOf(
            Triple("Tutor: Hallo! Wie heißt du?", "المعلم: مرحباً! ما اسمك؟ (شنو سميتك؟)", "Ich heiße Alex. / Mein Name ist Alex."),
            Triple("Tutor: Woher kommst du?", "المعلم: من أين أنت؟ (منين نتا؟)", "Ich komme aus Marokko. / Ich komme aus Ägypten."),
            Triple("Tutor: Wo wohnst du?", "المعلم: أين تسكن؟ (فين ساكن؟)", "Ich wohne in Berlin. / Ich wohne in Casablanca."),
            Triple("Tutor: Wie alt bist du?", "المعلم: كم عمرك؟ (شحال فعمرك؟)", "Ich bin 24 Jahre alt."),
            Triple("Tutor: Welche Sprachen sprichst du?", "المعلم: ما اللغات التي تتحدث بها؟", "Ich spreche Arabisch und lerne Deutsch.")
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlowingCard(borderColor = GermanGold.copy(alpha = 0.5f)) {
                Column {
                    Text("BEGRÜSSUNG & SICH VORSTELLEN", color = GermanGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("التحيات والتعريف بالنفس", style = MaterialTheme.typography.titleLarge, color = PureWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDarija) {
                            "تعلم كيفاش تقدم راسك وتعرف بالاسم، العمر، والبلاد بطريقة ألمانية صحيحة ومحترفة."
                        } else {
                            "تعلم كيف تقدم نفسك وتبدأ أولى محادثاتك باللغة الألمانية بثقة وسلاسة."
                        },
                        color = Slate300,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "1. Wichtige Begrüßungsformeln (عبارات التحية والوداع)",
                color = PureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(greetingsList) { greeting ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.speakGermanText(greeting.phraseDe) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = if (greeting.isFormal) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AIElectricCyan, Slate800))) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = greeting.phraseDe,
                                color = PureWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (greeting.isFormal) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AIElectricCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Formell / رسمي", color = AIElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isDarija) greeting.phraseDarija else greeting.phraseAr,
                            color = GermanGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = greeting.contextNote,
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = { viewModel.speakGermanText(greeting.phraseDe) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Pronounce",
                            tint = GermanGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "2. Interaktive Vorstellungssimulation (محاكاة التعريف بالنفس)",
                color = PureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "اضغط للاستماع إلى سؤال المعلم والرد المقترح",
                color = Slate400,
                fontSize = 12.sp
            )
        }

        items(dialogueSimulationSteps) { (question, translation, answer) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold.copy(alpha = 0.4f), AIElectricCyan.copy(alpha = 0.4f))))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Question from AI
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(question, color = AIElectricCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(translation, color = Slate400, fontSize = 12.sp)
                        }
                        IconButton(onClick = { viewModel.speakGermanText(question) }) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen Question", tint = AIElectricCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Answer Template
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate900)
                            .clickable { viewModel.speakGermanText(answer) }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Du (أنت):", color = GermanGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(answer, color = PureWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen Answer",
                                tint = GermanGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. ESSENTIAL PRONOUNS SECTION
// -------------------------------------------------------------
@Composable
private fun PronounsSection(viewModel: MainViewModel, isDarija: Boolean) {
    val pronounsList = remember {
        listOf(
            PronounItem("ich", "أنا", "أنا / أنايا", "1. Person Singular", "Ich lerne Deutsch."),
            PronounItem("du", "أنتَ / أنتِ (غير رسمي)", "نتا / نتي", "2. Person Singular (informell)", "Du sprichst gut Deutsch."),
            PronounItem("er", "هو (للمذكر)", "هو / هوّا", "3. Person Singular (maskulin)", "Er wohnt in Berlin."),
            PronounItem("sie", "هي (للمؤنث)", "هي / هيّا", "3. Person Singular (feminin)", "Sie kommt aus Marokko."),
            PronounItem("es", "هو/هي (للمحايد)", "هو/هي للمحايد", "3. Person Singular (neutral)", "Es ist kalt heute."),
            PronounItem("wir", "نحن", "حنا / نحن", "1. Person Plural", "Wir sind glücklich."),
            PronounItem("ihr", "أنتم (جمع غير رسمي)", "نتوما", "2. Person Plural (informell)", "Ihr lernt sehr fleißig."),
            PronounItem("sie", "هم / هن (جمع الغائب)", "هُما", "3. Person Plural", "Sie spielen Fußball."),
            PronounItem("Sie", "حضرتك / سيادتكم (رسمي بحرف كبير S)", "سيادتك / حضرتك", "Höflichkeitsform (immer groß)", "Kommen Sie aus Deutschland?")
        )
    }

    var selectedPronounIndex by remember { mutableStateOf<Int?>(null) }
    var selectedArabicIndex by remember { mutableStateOf<Int?>(null) }
    var matchScore by remember { mutableIntStateOf(0) }
    var matchStatusText by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlowingCard(borderColor = GermanGold.copy(alpha = 0.5f)) {
                Column {
                    Text("PERSONALPRONOMEN", color = GermanGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("الضمائر الشخصية في الألمانية", style = MaterialTheme.typography.titleLarge, color = PureWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDarija) {
                            "الضمائر أساس تكوين أي جملة. لاحظ الفرق بين du (صاحبك) و Sie (الاحترام والرسمي بحرف كبير دائماً)."
                        } else {
                            "الضمائر الشخصية هي حجر الأساس لتصريف الأفعال. لاحظ أن صيغة الاحترام Sie تبدأ دائماً بحرف كبير."
                        },
                        color = Slate300,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "1. Übersicht der Personalpronomen (جدول الضمائر مع النطق)",
                color = PureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(pronounsList) { pronoun ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.speakGermanText("${pronoun.pronounDe}. ${pronoun.exampleSentence}") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = if (pronoun.pronounDe == "Sie") CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan))) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (pronoun.pronounDe == "Sie") GermanGold.copy(alpha = 0.25f) else Slate800),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pronoun.pronounDe,
                            color = if (pronoun.pronounDe == "Sie") GermanGold else PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isDarija) pronoun.pronounDarija else pronoun.pronounAr,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pronoun.personType,
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Bsp: ${pronoun.exampleSentence}",
                            color = AIElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(onClick = { viewModel.speakGermanText("${pronoun.pronounDe}. ${pronoun.exampleSentence}") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Pronounce",
                            tint = GermanGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Section 2: Interactive Pronoun Matching Game
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan)))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎮 Pronoun Matcher Game", color = GermanGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GermanGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Score: $matchScore", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "اضغط على الضمير الألماني ثم طابقه مع معناه بالعربية / الدارجة:",
                        color = Slate300,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // German Pronoun Buttons
                    val matchOptionsDe = listOf("ich", "du", "wir", "ihr", "Sie")
                    val matchOptionsAr = if (isDarija) {
                        listOf("أنايا", "نتا / نتي", "حنا", "نتوما", "حضرتك (رسمي)")
                    } else {
                        listOf("أنا", "أنتَ / أنتِ", "نحن", "أنتم", "حضرتك (رسمي)")
                    }

                    Text("Wähle ein deutsches Pronomen:", color = Slate400, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        matchOptionsDe.forEachIndexed { idx, p ->
                            val isSel = selectedPronounIndex == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) GermanGold else Slate900)
                                    .clickable {
                                        selectedPronounIndex = idx
                                        viewModel.speakGermanText(p)
                                        matchStatusText = null
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(p, color = if (isSel) GermanBlack else PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("طابقه مع المعنى الصحيح:", color = Slate400, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Arabic equivalents
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        matchOptionsAr.forEachIndexed { idx, ar ->
                            val isSel = selectedArabicIndex == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) AIElectricCyan else Slate900)
                                    .clickable {
                                        selectedArabicIndex = idx
                                        if (selectedPronounIndex != null) {
                                            if (selectedPronounIndex == idx) {
                                                matchScore += 10
                                                matchStatusText = "✅ ممتاز! تطابق صحيح 100%"
                                                selectedPronounIndex = null
                                                selectedArabicIndex = null
                                            } else {
                                                matchStatusText = "❌ حاول مجدداً! تذكر معنى الضمير بدقة."
                                            }
                                        } else {
                                            matchStatusText = "💡 اختر الضمير الألماني أولاً!"
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(ar, color = if (isSel) GermanBlack else PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    if (matchStatusText != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = matchStatusText!!,
                            color = if (matchStatusText!!.startsWith("✅")) SuccessEmerald else if (matchStatusText!!.startsWith("💡")) GermanGold else GermanCrimson,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. A1.1 STARTER DRILLS & PRACTICE SECTION
// -------------------------------------------------------------
@Composable
private fun BasicsPracticeSection(viewModel: MainViewModel, isDarija: Boolean) {
    data class DrillQuestion(
        val questionDe: String,
        val questionAr: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanationDe: String,
        val explanationAr: String
    )

    val drills = remember {
        listOf(
            DrillQuestion(
                questionDe = "Wie spricht man das Wort 'Deutsch' aus?",
                questionAr = "كيف ينطق المقطع 'eu' في كلمة 'Deutsch'؟",
                options = listOf("أوْيْ [ɔʏ] (مثل دويتش)", "إِي [iː] (مثل ديتش)", "أَيْ [aɪ] (مثل دايتش)", "أُو [uː]"),
                correctIndex = 0,
                explanationDe = "'eu' und 'äu' werden im Deutschen immer wie [ɔʏ] ausgesprochen.",
                explanationAr = "التركيب eu و äu ينطق دائماً كصوت 'أوْيْ' (دويتش)."
            ),
            DrillQuestion(
                questionDe = "Wie lautet die Zahl 45 auf Deutsch?",
                questionAr = "ما هو المقابل الألماني الصحيح للرقم 45؟",
                options = listOf("fünfundvierzig (5 + und + 40)", "vierzigfünf", "fünfvierzig", "vierundfünfzig"),
                correctIndex = 0,
                explanationDe = "Im Deutschen nennt man die Einerstelle zuerst: fünf-und-vierzig.",
                explanationAr = "في الألمانية ننطق الآحاد أولاً ثم العشرات: fünf-und-vierzig."
            ),
            DrillQuestion(
                questionDe = "Was sagt man höflich am Abend zur Begrüßung?",
                questionAr = "ما هي التحية المسائية المهذبة المناسبة؟",
                options = listOf("Guten Abend!", "Gute Nacht!", "Guten Morgen!", "Tschüss!"),
                correctIndex = 0,
                explanationDe = "'Guten Abend' ist die Begrüßung am Abend ab 18:00 Uhr.",
                explanationAr = "'Guten Abend' هي التحية المسائية، بينما 'Gute Nacht' تستخدم عند النوم فقط."
            ),
            DrillQuestion(
                questionDe = "Welches Personalpronomen bedeutet 'نحن' (we)?",
                questionAr = "أي من الضمائر التالية يعني 'نحن' (حنا)؟",
                options = listOf("wir", "ihr", "sie", "er"),
                correctIndex = 0,
                explanationDe = "'wir' ist die 1. Person Plural (نحن).",
                explanationAr = "'wir' هو ضمير المتكلم للجمع (نحن / حنا)."
            ),
            DrillQuestion(
                questionDe = "Wie antwortet man auf 'Wie heißen Sie?'",
                questionAr = "كيف تجيب على السؤال الرسمي 'Wie heißen Sie?'",
                options = listOf("Ich heiße Alex.", "Ich komme aus Berlin.", "Ich bin 25 Jahre alt.", "Danke gut!"),
                correctIndex = 0,
                explanationDe = "'Wie heißen Sie?' fragt nach dem Namen. Antwort: 'Ich heiße...'",
                explanationAr = "السؤال يسأل عن الاسم، والإجابة تبدأ بـ 'Ich heiße...' أو 'Mein Name ist...'"
            )
        )
    }

    var currentDrillIndex by remember { mutableIntStateOf(0) }
    var selectedDrillOption by remember { mutableStateOf<Int?>(null) }
    var drillAnswered by remember { mutableStateOf(false) }
    var drillScore by remember { mutableIntStateOf(0) }

    val currentDrill = drills.getOrNull(currentDrillIndex)

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlowingCard(borderColor = AIElectricCyan.copy(alpha = 0.5f)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("A1.1 STARTER DRILLS", color = AIElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Frage ${currentDrillIndex + 1} von ${drills.size}", color = GermanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("اختبار تثبيت الأساسيات من الصفر", style = MaterialTheme.typography.titleLarge, color = PureWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (currentDrill != null) {
            item {
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
                            Text(currentDrill.questionDe, color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.speakGermanText(currentDrill.questionDe) }) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = GermanGold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(currentDrill.questionAr, color = Slate400, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        currentDrill.options.forEachIndexed { index, option ->
                            val isSelected = selectedDrillOption == index
                            val isCorrect = index == currentDrill.correctIndex

                            val bgColor = when {
                                drillAnswered && isCorrect -> SuccessEmerald.copy(alpha = 0.2f)
                                drillAnswered && isSelected && !isCorrect -> GermanCrimson.copy(alpha = 0.2f)
                                isSelected -> GermanGold.copy(alpha = 0.2f)
                                else -> Slate850
                            }

                            val borderBrush = when {
                                drillAnswered && isCorrect -> Brush.horizontalGradient(listOf(SuccessEmerald, SuccessEmerald))
                                drillAnswered && isSelected && !isCorrect -> Brush.horizontalGradient(listOf(GermanCrimson, GermanCrimson))
                                isSelected -> Brush.horizontalGradient(listOf(GermanGold, AIElectricCyan))
                                else -> Brush.horizontalGradient(listOf(Slate800, Slate800))
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(enabled = !drillAnswered) {
                                        selectedDrillOption = index
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = bgColor),
                                border = CardDefaults.outlinedCardBorder().copy(brush = borderBrush)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${('A' + index)}. ", color = GermanGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(option, color = PureWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                    if (drillAnswered && isCorrect) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessEmerald)
                                    }
                                }
                            }
                        }

                        if (drillAnswered) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Slate850)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("💡 Erklärung / الشرح:", color = GermanGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(currentDrill.explanationDe, color = Slate300, fontSize = 12.sp)
                                    Text(currentDrill.explanationAr, color = PureWhite, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (!drillAnswered) {
                                    drillAnswered = true
                                    if (selectedDrillOption == currentDrill.correctIndex) {
                                        drillScore += 20
                                    }
                                } else {
                                    if (currentDrillIndex < drills.size - 1) {
                                        currentDrillIndex++
                                        selectedDrillOption = null
                                        drillAnswered = false
                                    } else {
                                        // Completed all drills
                                        currentDrillIndex = 0
                                        selectedDrillOption = null
                                        drillAnswered = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GermanGold),
                            shape = RoundedCornerShape(12.dp),
                            enabled = selectedDrillOption != null
                        ) {
                            Text(
                                text = if (!drillAnswered) "Antwort prüfen (تحقق من الإجابة)" else if (currentDrillIndex < drills.size - 1) "Nächste Frage (التالي)" else "Drills wiederholen (إعادة التمارين)",
                                color = GermanBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
