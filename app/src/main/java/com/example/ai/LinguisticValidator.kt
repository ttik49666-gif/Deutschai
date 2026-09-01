package com.example.ai

import java.util.Locale

data class LinguisticIssue(
    val category: String,
    val userMatch: String,
    val correctedForm: String,
    val explanationDe: String,
    val explanationAr: String,
    val naturalAlternativeDe: String? = null
)

object LinguisticValidator {

    private val motionVerbs = setOf(
        "gegangen", "gefahren", "geflogen", "gekommen", "gelaufen",
        "gewandert", "gereist", "gerannt", "geschwommen", "gestiegen",
        "gefallen", "aufgestanden", "eingeschlafen", "aufgewacht", "passiert",
        "geschehen", "gewachsen", "gestorben", "geworden", "geblieben", "gewesen"
    )

    private val dativVerbs = setOf(
        "helfen", "hilft", "half", "geholfen",
        "danken", "dankt", "dankte", "gedankt",
        "gefallen", "gefällt", "gefiel",
        "gehören", "gehört", "gehörte",
        "schmecken", "schmeckt", "schmeckte",
        "passen", "passt", "passte",
        "antworten", "antwortet", "antwortete",
        "gratulieren", "gratuliert", "gratulierte",
        "vertrauen", "vertraut", "vertraute",
        "fehlen", "fehlt", "fehlte"
    )

    private val subConjunctions = setOf(
        "weil", "dass", "obwohl", "wenn", "als", "da", "während",
        "nachdem", "bevor", "damit", "ob", "falls", "sobald"
    )

    private val modalVerbs = setOf(
        "will", "willst", "wollen", "wollt", "wollte",
        "kann", "kannst", "können", "könnt", "konnte",
        "muss", "musst", "müssen", "müsst", "musste",
        "soll", "sollst", "sollen", "sollt", "sollte",
        "darf", "darfst", "dürfen", "dürft", "durfte",
        "möchte", "möchtest", "möchten", "möchtet"
    )

    fun validateGermanText(text: String): List<LinguisticIssue> {
        val issues = mutableListOf<LinguisticIssue>()
        val trimmed = text.trim()
        val lower = trimmed.lowercase(Locale.GERMAN)

        // 1. Perfekt Auxiliary Check (haben vs sein)
        val habenRegex = Regex("(?i)\\b(ich|du|er|sie|es|wir|ihr|sie|Sie)\\s+(habe|hast|hat|haben|habt)\\s+.*?(nach|zu|in|ins|nach Hause|am|im)?\\s*(\\w+)\\b")
        val habenMatches = habenRegex.findAll(trimmed)
        for (m in habenMatches) {
            val lastWord = m.groupValues[4].lowercase(Locale.GERMAN)
            if (motionVerbs.contains(lastWord)) {
                val subject = m.groupValues[1]
                val auxiliary = m.groupValues[2]
                val correctAux = when (subject.lowercase()) {
                    "ich" -> "bin"
                    "du" -> "bist"
                    "er", "sie", "es" -> "ist"
                    "wir", "sie", "Sie" -> "sind"
                    "ihr" -> "seid"
                    else -> "ist"
                }
                val corrected = m.value.replaceFirst(auxiliary, correctAux, ignoreCase = true)
                issues.add(
                    LinguisticIssue(
                        category = "Perfekt Hilfsverb (sein vs. haben)",
                        userMatch = m.value,
                        correctedForm = corrected,
                        explanationDe = "Verben der Fortbewegung und Zustandsänderung ($lastWord) bilden das Perfekt mit 'sein', nicht mit 'haben'.",
                        explanationAr = "أفعال الحركة وتغيير الحالة في الماضي المركب (Perfekt) تأخذ الفعل المساعد 'sein' بدلاً من 'haben'. مثال: Ich bin gefahren وليس Ich habe gefahren.",
                        naturalAlternativeDe = corrected
                    )
                )
                break
            }
        }

        // 2. Dativ Verb Objects Check
        val dativCheckRegex = Regex("(?i)\\b(helfe|hilft|helfen|helft|geholfen|danke|dankt|danken|gratuliere|gratuliert)\\s+(den|der|die|das|dich|ihn|sie)\\s*(\\w*)")
        val dativMatch = dativCheckRegex.find(trimmed)
        if (dativMatch != null) {
            val verb = dativMatch.groupValues[1]
            val article = dativMatch.groupValues[2]
            val noun = dativMatch.groupValues[3]
            val correctArticle = when (article.lowercase()) {
                "den", "der" -> "dem"
                "die" -> if (noun.endsWith("n") || noun.endsWith("en")) "den" else "der"
                "dich" -> "dir"
                "ihn" -> "ihm"
                "sie" -> "ihr"
                "das" -> "dem"
                else -> "dem"
            }
            val correctedPart = "$verb $correctArticle $noun".trim()
            issues.add(
                LinguisticIssue(
                    category = "Dativ-Objekt",
                    userMatch = dativMatch.value,
                    correctedForm = correctedPart,
                    explanationDe = "Das Verb '$verb' verlangt immer ein Dativ-Objekt (dem Mann, der Frau, den Kindern, dir, mir, ihm).",
                    explanationAr = "الفعل '$verb' يتطلب دائماً حالة المجرور (Dativ) للمفعول به غير المباشر (مثل: dem Mann, der Frau, dir, mir).",
                    naturalAlternativeDe = correctedPart
                )
            )
        }

        // 3. Subordinate Clause Word Order (Verb am Satzende)
        for (conj in subConjunctions) {
            val subRegex = Regex("(?i)\\b$conj\\s+(ich|du|er|sie|es|wir|ihr|Sie|der|die|das|man)\\s+(habe|bin|ist|hat|kann|muss|will|soll|darf|werde|war|hatte)\\s+([^,.?!;]+)")
            val subMatch = subRegex.find(trimmed)
            if (subMatch != null) {
                val subject = subMatch.groupValues[1]
                val conjugatedVerb = subMatch.groupValues[2]
                val predicate = subMatch.groupValues[3].trim()
                val correctedClause = "$conj $subject $predicate $conjugatedVerb"
                issues.add(
                    LinguisticIssue(
                        category = "Nebensatz-Satzbau (Verb am Ende)",
                        userMatch = subMatch.value,
                        correctedForm = correctedClause,
                        explanationDe = "Im Nebensatz mit '$conj' muss das konjugierte Verb ('$conjugatedVerb') ganz am Ende stehen.",
                        explanationAr = "في الجمل التابعة (Nebensatz) التي تبدأ بـ '$conj'، يجب أن ينتقل الفعل المصرف ('$conjugatedVerb') إلى نهاية الجملة تماماً.",
                        naturalAlternativeDe = correctedClause
                    )
                )
                break
            }
        }

        // 4. Modal Verb Bracket (Satzklammer)
        val modalRegex = Regex("(?i)\\b(will|kann|muss|soll|darf|möchte|können|müssen)\\s+(essen|trinken|kaufen|lernen|sprechen|sehen|machen|besuchen|fahren|gehen|bleiben)\\s+((?:ein|eine|einen|das|den|die|der|etwas|nach|in|im|nach Hause)\\s*\\w*)")
        val modalMatch = modalRegex.find(trimmed)
        if (modalMatch != null) {
            val modal = modalMatch.groupValues[1]
            val infinitive = modalMatch.groupValues[2]
            val obj = modalMatch.groupValues[3].trim()
            val correctedModal = "$modal $obj $infinitive"
            issues.add(
                LinguisticIssue(
                    category = "Modalverb Satzklammer",
                    userMatch = modalMatch.value,
                    correctedForm = correctedModal,
                    explanationDe = "Das Hauptverb im Infinitiv ('$infinitive') bildet mit dem Modalverb eine Satzklammer und steht ganz am Satzende.",
                    explanationAr = "مع الأفعال المساعدة (Modalverben)، يبقى الفعل الرئيسي في صيغة المصدر (Infinitiv) في نهاية الجملة ليشكل القوس اللغوي (Satzklammer).",
                    naturalAlternativeDe = correctedModal
                )
            )
        }

        // 5. Common Diminutive Gender (-chen -> das)
        val madchenRegex = Regex("(?i)\\b(die|der|eine)\\s+(Mädchen|Brötchen|Häuschen|Kindchen)\\b")
        val madchenMatch = madchenRegex.find(trimmed)
        if (madchenMatch != null) {
            val noun = madchenMatch.groupValues[2]
            val correctedNoun = "das $noun"
            issues.add(
                LinguisticIssue(
                    category = "Genus / Diminutiv (-chen)",
                    userMatch = madchenMatch.value,
                    correctedForm = correctedNoun,
                    explanationDe = "Nomen mit der Diminutiv-Endung '-chen' sind im Deutschen ausnahmslos neutral: 'das $noun'.",
                    explanationAr = "الأسماء المنتهية بلاحقة التصغير '-chen' تكون دائماً محايدة (Neutral) وتأخذ أداة التعريف 'das'.",
                    naturalAlternativeDe = correctedNoun
                )
            )
        }

        return issues
    }

    fun generateArabicGlossary(germanText: String): List<Pair<String, String>> {
        val wordMap = mapOf(
            "hallo" to "مرحباً",
            "guten" to "طاب / جيد",
            "tag" to "يوم",
            "morgen" to "صباح",
            "abend" to "مساء",
            "danke" to "شكراً",
            "bitte" to "من فضلك / عفواً",
            "ja" to "نعم",
            "nein" to "لا",
            "auf wiedersehen" to "إلى اللقاء",
            "tschüss" to "مع السلامة",
            "wie geht es dir" to "كيف حالك",
            "sehr gut" to "جيد جداً",
            "bahnhof" to "محطة القطار",
            "zug" to "قطار",
            "gleis" to "رصيف القطار",
            "fahrkarte" to "تذكرة السفر",
            "verspätung" to "تأخير",
            "tisch" to "طاولة",
            "speisekarte" to "قائمة الطعام",
            "wasser" to "ماء",
            "kaffee" to "قهوة",
            "rechnung" to "الحساب / الفاتورة",
            "bezahlen" to "يدفع",
            "arzt" to "طبيب",
            "krankenhaus" to "مستشفى",
            "termin" to "موعد",
            "schmerzen" to "آلام",
            "medikament" to "دواء",
            "erfahrung" to "خبرة",
            "entscheidung" to "قرار",
            "bewerbung" to "طلب توظيف",
            "vorstellungsgespräch" to "مقابلة عمل",
            "deutsch" to "الألمانية",
            "lernen" to "يتعلم",
            "sprechen" to "يتكلم",
            "verstehen" to "يفهم",
            "wohnung" to "شقة",
            "bürgeramt" to "مكتب تسجيل المواطنين",
            "anmeldung" to "تسجيل السكن",
            "pass" to "جواز السفر"
        )

        val found = mutableListOf<Pair<String, String>>()
        val lower = germanText.lowercase(Locale.GERMAN)
        for ((de, ar) in wordMap) {
            if (lower.contains(de)) {
                found.add(de.replaceFirstChar { it.uppercase() } to ar)
            }
        }
        return found
    }
}
