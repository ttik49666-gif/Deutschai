package com.example.ai

import com.example.data.model.ArabicTranslationDetail
import com.example.data.model.ConversationMode
import com.example.data.model.CorrectionLevel
import com.example.data.model.TargetDialect
import com.example.data.model.TutorPersonality
import com.example.data.model.WordMeaning
import java.util.Locale

object LocalSmartTutorEngine {

    fun analyzeAndRespond(
        userText: String,
        personality: TutorPersonality,
        correctionLevel: CorrectionLevel,
        userLevel: String,
        mode: ConversationMode,
        dialect: TargetDialect = TargetDialect.MSA,
        scenarioContext: String? = null,
        history: List<Pair<String, String>> = emptyList()
    ): TutorAnalysis {
        val trimmed = userText.trim()

        // 1. Linguistic deep validation
        val linguisticIssues = LinguisticValidator.validateGermanText(trimmed)
        val primaryIssue = linguisticIssues.firstOrNull()
        val hasCorrection = primaryIssue != null && correctionLevel != CorrectionLevel.MINIMAL

        val correctedUtterance = primaryIssue?.correctedForm
        val grammarExplanationDe = primaryIssue?.explanationDe
        val grammarExplanationAr = primaryIssue?.explanationAr

        // 2. Generate contextual German response
        val responseBody = generateContextualReply(trimmed, mode, scenarioContext, userLevel, personality)

        // 3. Generate structured Arabic Translation & Breakdown
        val arabicDetail = generateStructuredArabicTranslation(responseBody, dialect)

        // 4. Pedagogical feedback phrasing
        val formattedExplanationDe = if (hasCorrection) {
            when (personality) {
                TutorPersonality.STRICT -> "Achtung: $grammarExplanationDe Richtig: \"$correctedUtterance\"."
                TutorPersonality.FRIENDLY -> "Ein kleiner Tipp für dich 😊: $grammarExplanationDe"
                TutorPersonality.PROFESSIONAL -> "Grammatik-Hinweis: $grammarExplanationDe Korrekt: \"$correctedUtterance\"."
                TutorPersonality.PATIENT -> "Kein Problem 🌿: $grammarExplanationDe"
                TutorPersonality.ENERGETIC -> "Fast perfekt! ⚡ $grammarExplanationDe"
                TutorPersonality.CALM -> "Ganz entspannt ☕. Merk dir: $grammarExplanationDe"
            }
        } else null

        val naturalAlt = if (hasCorrection) {
            "Natürlicher: \"$correctedUtterance\""
        } else if (trimmed.length > 5) {
            suggestPoliteOrNativeAlternative(trimmed)
        } else null

        val (followUpDe, followUpAr) = generateFollowUpQuestion(mode, scenarioContext)

        return TutorAnalysis(
            responseText = responseBody,
            translation = arabicDetail.contextualTranslation,
            arabicDetail = arabicDetail,
            hasCorrection = hasCorrection,
            userOriginalUtterance = if (hasCorrection) trimmed else null,
            correctedUtterance = correctedUtterance,
            grammarExplanation = formattedExplanationDe,
            grammarExplanationAr = grammarExplanationAr,
            naturalAlternative = naturalAlt,
            targetedGrammarCategory = primaryIssue?.category,
            suggestedFollowUp = followUpDe,
            suggestedFollowUpAr = followUpAr,
            cefrLevel = userLevel
        )
    }

    private fun generateContextualReply(
        input: String,
        mode: ConversationMode,
        scenarioContext: String?,
        userLevel: String,
        personality: TutorPersonality
    ): String {
        val lower = input.lowercase(Locale.GERMAN)

        if (scenarioContext != null) {
            if (scenarioContext.contains("Restaurant", ignoreCase = true) || scenarioContext.contains("Linde", ignoreCase = true) || scenarioContext.contains("Café", ignoreCase = true)) {
                return when {
                    lower.contains("tisch") || lower.contains("platz") || lower.contains("hallo") ->
                        "Sehr gern! Wir haben einen schönen Tisch am Fenster frei. Möchten Sie vorab schon etwas zu trinken bestellen, zum Beispiel ein Mineralwasser oder ein bayerisches Bier?"
                    lower.contains("karte") || lower.contains("speisekarte") || lower.contains("empfehlen") ->
                        "Heute empfehle ich Ihnen unser frisches Kalbsschnitzel mit Bratkartoffeln oder hausgemachte Käsespätzle mit Röstzwiebeln. Was möchten Sie probieren?"
                    lower.contains("schnitzel") || lower.contains("spätzle") || lower.contains("hätte gern") || lower.contains("nehme") || lower.contains("bestellen") ->
                        "Hervorragende Wahl! Das wird frisch für Sie zubereitet. Darf es noch ein Beilagensalat dazu sein?"
                    lower.contains("zahlen") || lower.contains("rechnung") || lower.contains("bezahlen") ->
                        "Sehr gerne. Das macht zusammen 24 Euro und 50 Cent. Zahlen Sie mit Karte oder bar?"
                    else ->
                        "Wunderbar. Guten Appetit! Wenn Sie noch etwas brauchen, geben Sie mir einfach ein Zeichen."
                }
            } else if (scenarioContext.contains("Train", ignoreCase = true) || scenarioContext.contains("Bahn", ignoreCase = true) || scenarioContext.contains("Bahnhof", ignoreCase = true)) {
                return when {
                    lower.contains("verspätung") || lower.contains("wann") || lower.contains("nächste") || lower.contains("fährt") ->
                        "Ja, der ICE 572 hat leider 20 Minuten Verspätung wegen einer Signalstörung. Der nächste reguläre Zug nach Frankfurt fährt um 14:38 Uhr von Gleis 7 ab."
                    lower.contains("gleis") || lower.contains("bahnsteig") ->
                        "Der Zug fährt planmäßig von Gleis 7 ab. Bitte beachten Sie die Durchsagen am Bahnsteig."
                    lower.contains("ticket") || lower.contains("fahrkarte") || lower.contains("reservierung") ->
                        "Ihre Fahrkarte ist auch für den Ersatzzug gültig. Möchten Sie noch eine Sitzplatzreservierung dazu buchen?"
                    else ->
                        "Gute Reise und danke, dass Sie mit der Deutschen Bahn reisen! Haben Sie noch eine Frage zu Ihrer Verbindung?"
                }
            } else if (scenarioContext.contains("Job", ignoreCase = true) || scenarioContext.contains("Interview", ignoreCase = true) || scenarioContext.contains("Vorstellungsgespräch", ignoreCase = true)) {
                return "Vielen Dank für diese Vorstellung! Das klingt nach sehr relevanter Erfahrung. Können Sie mir ein konkretes Beispiel nennen, bei dem Sie eine technische Herausforderung im Team erfolgreich gelöst haben?"
            } else if (scenarioContext.contains("Doctor", ignoreCase = true) || scenarioContext.contains("Arzt", ignoreCase = true) || scenarioContext.contains("Krankenhaus", ignoreCase = true)) {
                return "Ich verstehe. Ich untersuche jetzt kurz Ihren Hals und messe die Temperatur. Bitte sagen Sie einmal 'Ah'. Nehmen Sie aktuell bereits irgendwelche Medikamente ein?"
            } else if (scenarioContext.contains("Bürgeramt", ignoreCase = true) || scenarioContext.contains("Anmeldung", ignoreCase = true)) {
                return "Vielen Dank. Ich habe Ihre Wohnungsgeberbestätigung und Ihren Pass geprüft. Hier ist Ihre offizielle Meldebestätigung. Brauchen Sie auch direkt Informationen zur steuerlichen Erfassung?"
            }
        }

        return when (mode) {
            ConversationMode.FREE_TALK -> {
                when {
                    lower.contains("hallo") || lower.contains("guten tag") || lower.contains("hi") ->
                        "Guten Tag! Wie geht es dir heute? Worüber möchtest du sprechen – über deinen Tag, deine Hobbys oder ein spannendes Thema?"
                    lower.contains("wetter") ->
                        "In Deutschland wechselt das Wetter im Frühling oft schnell. Wie ist das Wetter heute bei dir? Scheint die Sonne oder regnet es?"
                    lower.contains("deutsch") || lower.contains("lernen") ->
                        "Du machst wirklich super Fortschritte! Was fällt dir beim Deutschlernen am leichtesten: Vokabeln oder das freie Sprechen?"
                    else ->
                        "Das ist ein interessanter Gedanke! Erzähl mir gern mehr darüber: Warum denkst du so darüber?"
                }
            }
            ConversationMode.TEACHER -> {
                "Lass uns das gleich im Satz anwenden! Bilde bitte einen Satz mit dem Wort 'Entscheidung' oder benutze das Perfekt mit 'fahren'."
            }
            ConversationMode.DEBATE -> {
                "Ein starkes Argument! Allerdings könnte man einwenden, dass Flexibilität im Alltag oft wichtiger ist als starre Regeln. Wie stehst du zu diesem Gegenargument?"
            }
            ConversationMode.PRONUNCIATION_COACH -> {
                "Sehr deutlich gesprochen! Achte besonders auf das lange deutsche 'ä' und das weiche 'ch' wie in 'ich' und 'möchte'. Wiederhole bitte: 'Ich möchte gerne Deutsch sprechen.'"
            }
            else -> {
                "Sehr gut ausgedrückt! Lass uns diesen Gedanken vertiefen. Wie würdest du das in einer formellen E-Mail formulieren?"
            }
        }
    }

    fun generateStructuredArabicTranslation(
        germanText: String,
        dialect: TargetDialect = TargetDialect.MSA
    ): ArabicTranslationDetail {
        val wordBreakdown = mutableListOf<WordMeaning>()

        val contextual: String
        val literal: String
        var grammarNote: String? = null
        var darija: String? = null

        when {
            germanText.contains("Sehr gern! Wir haben einen schönen Tisch") -> {
                contextual = "بكل سرور! لدينا طاولة جميلة شاغرة بجانب النافذة. هل تود أن تطلب شيئاً لتشربه مسبقاً، مثل مياه معدنية أو مشروب؟"
                literal = "بكل سرور جداً! نحن نملك طاولة جميلة عند النافذة شاغرة. هل ترغب مسبقاً شيئاً للشرب تطلب..."
                grammarNote = "تعبير 'Sehr gern' يستخدم للموافقة والترحيب بكل سرور."
                darija = "مرحبا بيك! عندنا طابلة زوينة خاوية حدا الشرجم. بغيتي تشرب شي حاجة دابا بحال ما معدني؟"
                wordBreakdown.addAll(listOf(
                    WordMeaning("Sehr gern", "بكل سرور", "Phrase"),
                    WordMeaning("Tisch", "طاولة", "Nomen (maskulin)"),
                    WordMeaning("Fenster", "نافذة / شباك", "Nomen (neutral)"),
                    WordMeaning("bestellen", "يطلب / يوصي", "Verb"),
                    WordMeaning("Mineralwasser", "مياه معدنية", "Nomen")
                ))
            }
            germanText.contains("Heute empfehle ich Ihnen") -> {
                contextual = "اليوم أنصحكم بطبق شنيتزل لحم العجل الطازج مع البطاطس المحمرة أو معكرونة كيزيشبتسله المنزلية. أيهما تفضل أن تجرب؟"
                literal = "اليوم أوصي أنا لحضرتكم شنيتزل طازج مع بطاطس مقلية..."
                grammarNote = "الفعل 'empfehlen' يأخذ Dativ للشخص (Ihnen) و Akkusativ للشيء المنصوح به."
                darija = "اليوم كنقترح عليك طبق شنيتزل طري مع بطاطا مقلية. شنو بغيتي تختار؟"
                wordBreakdown.addAll(listOf(
                    WordMeaning("empfehlen", "يوصي / يقترح", "Verb"),
                    WordMeaning("Ihnen", "لحضرتكم (مجرور Dativ)", "Pronomen"),
                    WordMeaning("frisch", "طازج", "Adjektiv"),
                    WordMeaning("probieren", "يجرب / يتذوق", "Verb")
                ))
            }
            germanText.contains("Das macht zusammen 24 Euro") -> {
                contextual = "المجموع 24 يورو و 50 سنتاً. هل ستدفع بالبطاقة المصرفية أم نقداً؟"
                literal = "ذلك يصنع معاً 24 يورو..."
                grammarNote = "تعبير 'Das macht zusammen' هو الصيغة القياسية لذكر إجمالي الحساب."
                darija = "المجموع كامل هو 24 أورو و 50 سنتيم. غادي تخلص بلاكارط ولا كاش؟"
                wordBreakdown.addAll(listOf(
                    WordMeaning("zusammen", "معاً / إجمالاً", "Adverb"),
                    WordMeaning("zahlen", "يدفع الحساب", "Verb"),
                    WordMeaning("Karte", "بطاقة بنكية", "Nomen"),
                    WordMeaning("bar", "نقداً / كاش", "Adverb")
                ))
            }
            germanText.contains("ICE 572") -> {
                contextual = "نعم، للأسف قطار ICE 572 متأخر 20 دقيقة بسبب عطل في الإشارات. القطار النظامي التالي إلى فرانكفورت يغادر في الساعة 14:38 من الرصيف 7."
                literal = "نعم، القطار يملك للأسف 20 دقيقة تأخير بسبب عطل إشارات..."
                grammarNote = "حرف الجر 'wegen' (بسبب) يأتي عادة مع حالة المضاف إليه (Genitiv)."
                darija = "التران معطل بـ 20 دقيقة بسباب مشكل فالسينيال. التران الجاي لفرانكفورت غادي مع 14:38 من الرصيف 7."
                wordBreakdown.addAll(listOf(
                    WordMeaning("Verspätung", "تأخير", "Nomen (feminin)"),
                    WordMeaning("Zug", "قطار", "Nomen (maskulin)"),
                    WordMeaning("Gleis", "رصيف القطار", "Nomen (neutral)"),
                    WordMeaning("abfahren", "يغادر / ينطلق", "Verb")
                ))
            }
            germanText.contains("Guten Tag! Wie geht es dir") -> {
                contextual = "طاب يومك! كيف حالك اليوم؟ عن ماذا ترغب في التحدث – عن يومك، هواياتك، أو موضوع شيق؟"
                literal = "يوماً طيباً! كيف يذهب الأمر لك اليوم؟..."
                grammarNote = "السؤال 'Wie geht es dir?' يستخدم ضمير المجرور (dir) بدلاً من (du)."
                darija = "نهار مبروك! كيف داير اليوم؟ فاش باغي تهضر – على نهارك، الهوايات ديالك ولا شي موضوع زوين؟"
                wordBreakdown.addAll(listOf(
                    WordMeaning("Guten Tag", "طاب يومك / مرحباً", "Gruß"),
                    WordMeaning("Wie geht es dir", "كيف حالك", "Frage"),
                    WordMeaning("sprechen", "يتحدث / يتكلم", "Verb"),
                    WordMeaning("Hobbys", "هوايات", "Nomen (Plural)")
                ))
            }
            germanText.contains("Vielen Dank für diese Vorstellung") -> {
                contextual = "شكراً جزيلاً لك على هذا التقديم! يبدو أن لديك خبرة مفيدة جداً. هل يمكنك إعطائي مثالاً عملياً قمت فيه بحل تحدٍ تقني بنجاح ضمن الفريق؟"
                literal = "شكراً جزيلاً لهذا التقديم! ذلك يبدو بعد خبرة ذات صلة..."
                grammarNote = "حرف الجر 'für' يتطلب دائماً حالة النصب (Akkusativ)."
                darija = "شكراً بزاف على هاد التقديم! كتبان تجربة مهمة بزاف. واش تقدر تعطيني شي مثال واقعي حليتي فيه مشكل تقني فالخدمة؟"
                wordBreakdown.addAll(listOf(
                    WordMeaning("Vorstellung", "تقديم النفس / عرض", "Nomen"),
                    WordMeaning("Erfahrung", "خبرة / تجربة", "Nomen"),
                    WordMeaning("Herausforderung", "تحدي / صعوبة", "Nomen"),
                    WordMeaning("lösen", "يحل / يعالج", "Verb")
                ))
            }
            else -> {
                contextual = "أفهمك تماماً ويسعدني استمرارنا في المحادثة! دعنا نواصل استكشاف هذا الموضوع وتطوير لغتك الألمانية."
                literal = "أنا أفهمك بوضوح. دعنا نواصل..."
                grammarNote = "تذكر وضع الفعل المصرف في الموضع الثاني دائماً في الجمل الرئيسية."
                darija = "فهمتك مزيان ومزيان نكملو الهضرة! يلاه نزيدو نتدربو ونطورو الألمانية ديالك."
                wordBreakdown.addAll(listOf(
                    WordMeaning("verstehen", "يفهم / يستوعب", "Verb"),
                    WordMeaning("weiter", "متابعة / للأمام", "Adverb"),
                    WordMeaning("Deutsch", "اللغة الألمانية", "Nomen")
                ))
            }
        }

        return ArabicTranslationDetail(
            contextualTranslation = contextual,
            literalTranslation = literal,
            wordByWord = wordBreakdown,
            grammarNotes = grammarNote,
            darijaAlternative = darija
        )
    }

    private fun suggestPoliteOrNativeAlternative(text: String): String? {
        val lower = text.lowercase(Locale.GERMAN)
        if (lower.contains("ich will")) {
            return "Höflicher im Deutschen: \"Ich hätte gern...\" oder \"Ich möchte bitte...\" (بدلاً من 'ich will' الفجة، استخدم صيغة الأدب 'Ich hätte gern')"
        }
        if (lower.contains("was ist das")) {
            return "Alternative für den Alltag: \"Könnten Sie mir kurz erklären, was das bedeutet?\" (صيغة أكثر لباقة)"
        }
        return null
    }

    private fun generateFollowUpQuestion(mode: ConversationMode, scenarioContext: String?): Pair<String, String> {
        if (scenarioContext?.contains("Restaurant") == true) {
            return Pair("Möchtest du auch nach der Nachspeise (Dessert) fragen?", "هل ترغب في السؤال عن قائمة الحلويات (Dessert) أيضاً؟")
        }
        return when (mode) {
            ConversationMode.ROLEPLAY -> Pair("Wie antwortest du darauf?", "كيف ترد على هذا؟")
            ConversationMode.DEBATE -> Pair("Welches Gegenargument fällt dir ein?", "ما هي الحجة المضادة التي تخطر ببالك؟")
            else -> Pair("Was möchtest du als Nächstes sagen?", "ما الذي تود قوله بعد ذلك؟")
        }
    }

    fun evaluatePlacementAnswers(
        grammarScore: Int,
        vocabScore: Int,
        readingScore: Int,
        listeningScore: Int,
        speakingScore: Int,
        writingScore: Int
    ): PlacementEvaluationResult {
        // Multi-skill independent proficiency calculation (no fake offsets)
        val weightedScore = (
            (grammarScore * 0.20) +
            (vocabScore * 0.20) +
            (readingScore * 0.15) +
            (listeningScore * 0.15) +
            (writingScore * 0.15) +
            (speakingScore * 0.15)
        ).toInt()

        val (level, conf) = when {
            weightedScore >= 90 -> "C1.1" to 94
            weightedScore >= 82 -> "B2.2" to 92
            weightedScore >= 74 -> "B2.1" to 90
            weightedScore >= 65 -> "B1.2" to 88
            weightedScore >= 55 -> "B1.1" to 86
            weightedScore >= 45 -> "A2.2" to 87
            weightedScore >= 35 -> "A2.1" to 85
            weightedScore >= 20 -> "A1.2" to 84
            else -> "A1.1" to 90
        }

        fun scoreToCefr(s: Int): String = when {
            s >= 88 -> "C1"
            s >= 72 -> "B2"
            s >= 55 -> "B1"
            s >= 38 -> "A2"
            else -> "A1"
        }

        val feedbackDe = "Du hast eine solide Grundlage. Dein Stärkenbereich liegt in ${if (vocabScore >= grammarScore) "Wortschatz und Leseverstehen" else "Grammatik und Satzstrukturen"}, während dein größtes Wachstumspotenzial bei ${if (grammarScore <= vocabScore) "Dativ-Objekten und Nebensatz-Verbstellung" else "idiomatischen Ausdrücken"} liegt."
        val feedbackAr = "لديك أساس متين في اللغة الألمانية. تكمن نقاط قوتك في ${if (vocabScore >= grammarScore) "المفردات والفهم القرائي" else "القواعد والتركيب اللغوي"}، بينما فرصة التطوير الأكبر تتمثل في ${if (grammarScore <= vocabScore) "حالات المجرور (Dativ) وترتيب الأفعال في الجمل التابعة" else "المفردات الاصطلاحية المتقدمة والتعبير الشفهي"}."

        val firstTopicDe = if (grammarScore < 60) "A2.2 Dativ Cases & Nebensatz-Satzbau" else "B1.1 Konjunktiv II & Wechselpräpositionen"
        val firstTopicAr = if (grammarScore < 60) "A2.2 إتقان حالة المجرور (Dativ) وترتيب الجمل التابعة" else "B1.1 صيغة اللباقة والافتراض (Konjunktiv II) وحروف الجر المزدوجة"

        return PlacementEvaluationResult(
            estimatedLevel = level,
            confidenceScore = conf,
            grammarScore = scoreToCefr(grammarScore),
            vocabularyScore = scoreToCefr(vocabScore),
            readingScore = scoreToCefr(readingScore),
            listeningScore = scoreToCefr(listeningScore),
            writingScore = scoreToCefr(writingScore),
            speakingScore = scoreToCefr(speakingScore),
            pronunciationScore = scoreToCefr(speakingScore),
            summaryFeedback = feedbackDe,
            summaryFeedbackAr = feedbackAr,
            recommendedFirstTopic = firstTopicDe,
            recommendedFirstTopicAr = firstTopicAr
        )
    }

    fun evaluateWriting(
        text: String,
        prompt: String,
        targetCEFR: String
    ): WritingEvaluationReport {
        val wordCount = text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        val issues = LinguisticValidator.validateGermanText(text)

        var grammarScore = (90 - (issues.size * 12)).coerceIn(40, 98)
        var vocabScore = when {
            wordCount >= 45 -> 88
            wordCount >= 25 -> 78
            wordCount >= 15 -> 65
            else -> 48
        }
        var coherenceScore = if (text.contains("weil") || text.contains("dass") || text.contains("deshalb") || text.contains("aber") || text.contains("und")) 85 else 68
        val taskFulfillment = if (wordCount >= 20) 85 else 60

        var corrected = text
        for (issue in issues) {
            corrected = corrected.replace(issue.userMatch, issue.correctedForm)
        }

        val nativeImproved = if (prompt.contains("Email", ignoreCase = true) || prompt.contains("Brief", ignoreCase = true)) {
            "Sehr geehrte Damen und Herren,\n\nich schreibe Ihnen bezüglich Ihrer Mitteilung. $corrected\n\nIch bedanke mich im Voraus für Ihre Unterstützung und freue mich auf Ihre Antwort.\n\nMit freundlichen Grüßen,\nAlex"
        } else {
            "$corrected Zusammenfassend lässt sich sagen, dass dies eine wertvolle Erfahrung für meinen Alltag darstellt."
        }

        val strengths = mutableListOf<String>()
        val strengthsAr = mutableListOf<String>()
        if (wordCount >= 20) {
            strengths.add("Gute Textlänge und ausreichende Ausführlichkeit.")
            strengthsAr.add("طول النص ملائم وتفاصيل الفكرة واضحة ومكتملة.")
        }
        if (issues.isEmpty()) {
            strengths.add("Fehlerfreie Grammatik und präzise Kasus-Verwendung.")
            strengthsAr.add("قواعد نحوية خالية من الأخطاء واستخدام دقيق للحالات الإعرابية.")
        } else {
            strengths.add("Verständliche und logische Gedankenführung.")
            strengthsAr.add("تسلسل أفكار مفهوم ومنطقي يسهل متابعته.")
        }

        val improvements = mutableListOf<String>()
        val improvementsAr = mutableListOf<String>()
        if (issues.isNotEmpty()) {
            val firstIssue = issues.first()
            improvements.add("Achte auf: ${firstIssue.category} (${firstIssue.explanationDe})")
            improvementsAr.add("ركز على: ${firstIssue.category} (${firstIssue.explanationAr})")
        }
        if (wordCount < 25) {
            improvements.add("Versuche mehr Konjunktionen wie 'weil', 'obwohl' oder 'deshalb' zu verwenden.")
            improvementsAr.add("حاول استخدام المزيد من الروابط مثل 'weil' (لأن) أو 'obwohl' (على الرغم من) لإثراء النص.")
        }

        val overallPercent = ((grammarScore + vocabScore + coherenceScore + taskFulfillment) / 4)

        return WritingEvaluationReport(
            overallScorePercent = overallPercent,
            estimatedCEFR = targetCEFR,
            grammarScore = grammarScore,
            vocabularyScore = vocabScore,
            coherenceScore = coherenceScore,
            taskFulfillmentScore = taskFulfillment,
            originalText = text,
            correctedText = corrected,
            detailedFeedback = "Dein Text ist verständlich aufgebaut. Achte besonders auf Nebensatzstrukturen und korrekte Höflichkeitsfloskeln im Schriftverkehr.",
            detailedFeedbackAr = "نصك مكتوب بشكل مفهوم ومنظم. انتبه لموضع الفعل في الجمل الجانبية واستخدام عبارات اللباقة الرسمية المناسبة.",
            improvedNativeVersion = nativeImproved,
            keyStrengths = strengths,
            keyStrengthsAr = strengthsAr,
            areasForImprovement = improvements,
            areasForImprovementAr = improvementsAr
        )
    }
}
