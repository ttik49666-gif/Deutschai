package com.example.ai

import com.example.data.model.ConversationMode
import com.example.data.model.CorrectionLevel
import com.example.data.model.TutorPersonality
import java.util.Locale

object LocalSmartTutorEngine {

    data class ErrorPattern(
        val regex: Regex,
        val correctReplacement: (MatchResult) -> String,
        val explanation: String,
        val category: String,
        val naturalAlternative: String? = null
    )

    private val errorPatterns = listOf(
        // 1. Perfekt Auxiliary: Motion verbs using 'haben' instead of 'sein'
        ErrorPattern(
            regex = Regex("(?i)\\b(ich|du|er|sie|es|wir|ihr|sie|Sie)\\s+habe?\\s+.*?(nach|zu|in|ins|nach Hause)?\\s*(gegangen|gefahren|geflogen|gekommen|gelaufen|gewandert|gereist|gerannt)\\b"),
            correctReplacement = { match ->
                val full = match.value
                full.replace(Regex("(?i)\\bhabe?\\b"), "bin")
            },
            explanation = "Verben der Ortsveränderung (gehen, fahren, fliegen, kommen) bilden das Perfekt im Deutschen immer mit dem Hilfsverb 'sein' (ich bin gefahren, nicht: ich habe gefahren).",
            category = "Perfekt Hilfsverb (sein vs. haben)"
        ),
        // 2. Dativ verb: helfen + Accusative (der Mann -> dem Mann, mich -> mir)
        ErrorPattern(
            regex = Regex("(?i)\\bhelfe?\\s+(den|der|die|das|dich|ihn|sie)\\s+(\\w+)?"),
            correctReplacement = { match ->
                val target = match.value
                target.replace(Regex("(?i)\\b(den|der)\\b"), "dem")
                      .replace(Regex("(?i)\\bdich\\b"), "dir")
                      .replace(Regex("(?i)\\bihn\\b"), "ihm")
            },
            explanation = "Das Verb 'helfen' regiert im Deutschen immer den Dativ (helfen + Dativ: dem Mann, der Frau, dem Kind, dir, mir, ihm).",
            category = "Dativ-Objekt"
        ),
        // 3. Subordinate clause: weil + verb in second position
        ErrorPattern(
            regex = Regex("(?i)\\bweil\\s+(ich|du|er|sie|wir|ihr|Sie)\\s+(habe|bin|kann|muss|will|habe|möchte|ist|hat)\\s+(.*)"),
            correctReplacement = { match ->
                val subj = match.groupValues[1]
                val verb = match.groupValues[2]
                val rest = match.groupValues[3].trim()
                "weil $subj $rest $verb"
            },
            explanation = "Die Konjunktion 'weil' leitet einen Nebensatz ein. Im Nebensatz steht das konjugierte Verb immer ganz am Ende.",
            category = "Nebensatz-Satzbau (Verb am Ende)"
        ),
        // 4. Modal verb word order: modal verb followed immediately by infinitive in middle
        ErrorPattern(
            regex = Regex("(?i)\\b(will|kann|muss|soll|darf|möchte)\\s+(essen|trinken|kaufen|lernen|sprechen|sehen|machen|besuchen)\\s+(ein|eine|einen|das|den|die|der|etwas)?\\s*(\\w+)"),
            correctReplacement = { match ->
                val modal = match.groupValues[1]
                val infinitive = match.groupValues[2]
                val article = match.groupValues[3]
                val noun = match.groupValues[4]
                "$modal $article $noun $infinitive".replace("  ", " ")
            },
            explanation = "Bei Modalverben steht das Modalverb auf Position 2 und das Hauptverb (Infinitiv) bildet die Satzklammer ganz am Ende des Satzes.",
            category = "Modalverb Satzklammer"
        ),
        // 5. Common article: das Mädchen (often confused with die Mädchen)
        ErrorPattern(
            regex = Regex("(?i)\\b(die|der|eine)\\s+Mädchen\\b"),
            correctReplacement = { "das Mädchen" },
            explanation = "Nomen mit der Endung '-chen' (Diminutiv) sind im Deutschen immer neutral: das Mädchen.",
            category = "Genus / Artikel (das Mädchen)"
        )
    )

    fun analyzeAndRespond(
        userText: String,
        personality: TutorPersonality,
        correctionLevel: CorrectionLevel,
        userLevel: String,
        mode: ConversationMode,
        scenarioContext: String? = null,
        history: List<Pair<String, String>> = emptyList()
    ): TutorAnalysis {
        val trimmed = userText.trim()
        var detectedError: ErrorPattern? = null
        var correctedUtterance: String? = null

        // Check for grammatical errors
        for (pattern in errorPatterns) {
            if (pattern.regex.containsMatchIn(trimmed)) {
                detectedError = pattern
                val match = pattern.regex.find(trimmed)
                if (match != null) {
                    correctedUtterance = trimmed.replace(match.value, pattern.correctReplacement(match))
                }
                break
            }
        }

        val hasCorrection = detectedError != null && correctionLevel != CorrectionLevel.MINIMAL

        // Build pedagogical reply based on mode, scenario, and personality
        val responseBody = generateContextualReply(trimmed, mode, scenarioContext, userLevel, personality)
        val translation = generateGermanToEnglishTranslation(responseBody)

        val explanation = if (hasCorrection) {
            when (personality) {
                TutorPersonality.STRICT -> "Achtung: ${detectedError!!.explanation} Sag es bitte richtig: \"$correctedUtterance\"."
                TutorPersonality.FRIENDLY -> "Ein kleiner Tipp für dich 😊: ${detectedError!!.explanation}"
                TutorPersonality.PROFESSIONAL -> "Grammatik-Analyse: ${detectedError!!.explanation} Korrekt: \"$correctedUtterance\"."
                TutorPersonality.PATIENT -> "Kein Problem, ein häufiger Schritt beim Lernen 🌿: ${detectedError!!.explanation}"
                TutorPersonality.ENERGETIC -> "Fast geschafft! ⚡ Achte kurz hierauf: ${detectedError!!.explanation}"
                TutorPersonality.CALM -> "Ganz entspannt ☕. Merk dir einfach: ${detectedError!!.explanation}"
            }
        } else null

        val naturalAlternative = if (hasCorrection) {
            "Natürlicher formuliert: \"$correctedUtterance\""
        } else if (trimmed.length > 5) {
            suggestPoliteOrNativeAlternative(trimmed)
        } else null

        return TutorAnalysis(
            responseText = responseBody,
            translation = translation,
            hasCorrection = hasCorrection,
            userOriginalUtterance = if (hasCorrection) trimmed else null,
            correctedUtterance = correctedUtterance,
            grammarExplanation = explanation,
            naturalAlternative = naturalAlternative,
            targetedGrammarCategory = detectedError?.category,
            suggestedFollowUp = generateFollowUpQuestion(mode, scenarioContext),
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

        // Scenario-based responses
        if (scenarioContext != null) {
            if (scenarioContext.contains("Restaurant", ignoreCase = true) || scenarioContext.contains("Linde", ignoreCase = true)) {
                return when {
                    lower.contains("tisch") || lower.contains("platz") ->
                        "Sehr gern! Wir haben einen schönen Tisch am Fenster frei. Möchten Sie vorab schon etwas zu trinken bestellen, zum Beispiel ein Mineralwasser oder ein bayerisches Helles?"
                    lower.contains("karte") || lower.contains("speisekarte") || lower.contains("empfehlen") ->
                        "Heute empfehle ich Ihnen unser frisches Kalbsschnitzel mit Bratkartoffeln oder hausgemachte Käsespätzle mit Röstzwiebeln. Was spricht Sie mehr an?"
                    lower.contains("schnitzel") || lower.contains("spätzle") || lower.contains("hätte gern") || lower.contains("nehme") || lower.contains("bestellen") ->
                        "Hervorragende Wahl! Das wird frisch für Sie zubereitet. Darf es noch ein Beilagensalat dazu sein?"
                    lower.contains("zahlen") || lower.contains("rechnung") || lower.contains("bezahlen") ->
                        "Sehr gerne. Das macht zusammen 24 Euro und 50 Cent. Zahlen Sie mit Karte oder bar?"
                    else ->
                        "Wunderbar. Guten Appetit! Wenn Sie noch etwas brauchen, geben Sie mir einfach ein Zeichen."
                }
            } else if (scenarioContext.contains("Train", ignoreCase = true) || scenarioContext.contains("Bahn", ignoreCase = true)) {
                return when {
                    lower.contains("verspätung") || lower.contains("wann") || lower.contains("nächste") ->
                        "Ja, der ICE 572 hat leider 25 Minuten Verspätung wegen einer Signalstörung. Der nächste reguläre Zug nach Frankfurt fährt um 14:38 Uhr von Gleis 7 ab."
                    lower.contains("gleis") || lower.contains("bahnsteig") ->
                        "Der Zug fährt planmäßig von Gleis 7 ab. Bitte beachten Sie die Durchsagen am Bahnsteig."
                    lower.contains("ticket") || lower.contains("fahrkarte") || lower.contains("reservierung") ->
                        "Ihre Fahrkarte ist auch für den Ersatzzug gültig. Möchten Sie noch eine Sitzplatzreservierung dazu buchen?"
                    else ->
                        "Gute Reise und danke, dass Sie mit der Deutschen Bahn reisen! Haben Sie noch eine Frage zu Ihrer Verbindung?"
                }
            } else if (scenarioContext.contains("Job", ignoreCase = true) || scenarioContext.contains("Interview", ignoreCase = true)) {
                return "Vielen Dank für diesen Einblick! Das klingt nach sehr relevanter Erfahrung. Können Sie mir ein konkretes Beispiel nennen, bei dem Sie eine technische Herausforderung im Team erfolgreich gelöst haben?"
            } else if (scenarioContext.contains("Doctor", ignoreCase = true) || scenarioContext.contains("Arzt", ignoreCase = true)) {
                return "Ich verstehe. Ich untersuche jetzt kurz Ihren Hals und messe die Temperatur. Bitte sagen Sie einmal 'Ah'. Nehmen Sie aktuell bereits irgendwelche Medikamente ein?"
            } else if (scenarioContext.contains("Bürgeramt", ignoreCase = true) || scenarioContext.contains("Anmeldung", ignoreCase = true)) {
                return "Vielen Dank. Ich habe Ihre Wohnungsgeberbestätigung und Ihren Pass geprüft. Hier ist Ihre offizielle Meldebestätigung. Brauchen Sie auch direkt Informationen zur steuerlichen Erfassung?"
            }
        }

        // Mode-based responses
        return when (mode) {
            ConversationMode.FREE_TALK -> {
                when {
                    lower.contains("hallo") || lower.contains("guten tag") || lower.contains("hi") ->
                        "Hallo! Wie geht es dir heute? Worüber möchtest du sprechen – über deinen Tag, deine Hobbys oder ein spannendes deutsches Thema?"
                    lower.contains("wetter") ->
                        "In Deutschland wechselt das Wetter oft schnell. Wie ist das Wetter heute bei dir? Scheint die Sonne oder regnet es?"
                    lower.contains("deutsch") || lower.contains("lernen") ->
                        "Du machst wirklich super Fortschritte! Was fällt dir beim Deutschlernen am leichtesten: Wörter merken oder das Sprechen?"
                    else ->
                        "Das ist ein interessanter Punkt! Erzähl mir gern mehr darüber: Warum denkst du so darüber?"
                }
            }
            ConversationMode.TEACHER -> {
                "Lass uns das gleich im Satz üben! Bilde bitte einen Satz mit dem Wort 'Entscheidung' oder benutze das Perfekt von 'fahren'."
            }
            ConversationMode.DEBATE -> {
                "Ein starkes Argument! Allerdings sehen viele Experten das anders: Man könnte einwenden, dass Flexibilität wichtiger ist als feste Regeln. Wie entgegnest du diesem Argument?"
            }
            ConversationMode.PRONUNCIATION_COACH -> {
                "Sehr deutlich gesprochen! Achte besonders auf das lange deutsche 'ä' und das weiche 'ch' wie in 'ich' und 'möchte'. Wiederhole bitte: 'Ich möchte gerne Deutsch sprechen.'"
            }
            else -> {
                "Sehr gut ausgedrückt! Lass uns diesen Gedanken weiter vertiefen. Wie würdest du das in einer formellen E-Mail formulieren?"
            }
        }
    }

    private fun generateGermanToEnglishTranslation(german: String): String {
        return when {
            german.contains("Sehr gern! Wir haben einen schönen Tisch") ->
                "Very gladly! We have a nice table free by the window. Would you like to order something to drink in advance, for example mineral water or a Bavarian beer?"
            german.contains("Heute empfehle ich Ihnen") ->
                "Today I recommend our fresh veal schnitzel with fried potatoes or homemade cheese spaetzle with fried onions. Which appeals to you more?"
            german.contains("Hervorragende Wahl!") ->
                "Excellent choice! That will be freshly prepared for you. Would you like a side salad with that?"
            german.contains("Das macht zusammen 24 Euro") ->
                "That makes a total of 24 euros and 50 cents. Are you paying by card or cash?"
            german.contains("ICE 572") ->
                "Yes, the ICE 572 is unfortunately 25 minutes delayed due to a signal malfunction. The next regular train to Frankfurt departs at 14:38 from Platform 7."
            german.contains("Hallo! Wie geht es dir") ->
                "Hello! How are you doing today? What would you like to talk about – your day, your hobbies, or an exciting German topic?"
            german.contains("Vielen Dank für diesen Einblick") ->
                "Thank you for this insight! That sounds like very relevant experience. Can you give me a concrete example where you successfully resolved a technical challenge in a team?"
            else ->
                "I understand clearly. Let's continue exploring this topic and practicing your spoken German!"
        }
    }

    private fun suggestPoliteOrNativeAlternative(text: String): String? {
        val lower = text.lowercase(Locale.GERMAN)
        if (lower.contains("ich will")) {
            return "Höflicher im Deutschen: \"Ich hätte gern...\" oder \"Ich möchte bitte...\""
        }
        if (lower.contains("was ist das")) {
            return "Alternative für den Alltag: \"Könnten Sie mir kurz erklären, was das bedeutet?\""
        }
        return null
    }

    private fun generateFollowUpQuestion(mode: ConversationMode, scenarioContext: String?): String {
        if (scenarioContext?.contains("Restaurant") == true) {
            return "Möchtest du auch nach der Nachspeise (Dessert) fragen?"
        }
        return when (mode) {
            ConversationMode.ROLEPLAY -> "Wie antwortest du darauf?"
            ConversationMode.DEBATE -> "Welches Gegenargument fällt dir ein?"
            else -> "Was möchtest du als Nächstes sagen?"
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
        val total = grammarScore + vocabScore + readingScore + listeningScore + speakingScore + writingScore
        val avg = total / 6

        val (level, conf) = when {
            avg >= 88 -> "B2.2" to 92
            avg >= 75 -> "B2.1" to 90
            avg >= 65 -> "B1.2" to 88
            avg >= 55 -> "B1.1" to 86
            avg >= 45 -> "A2.2" to 87
            avg >= 35 -> "A2.1" to 85
            avg >= 20 -> "A1.2" to 84
            else -> "A1.1" to 90
        }

        fun scoreToCefr(s: Int): String = when {
            s >= 80 -> "B2"
            s >= 60 -> "B1"
            s >= 40 -> "A2"
            else -> "A1"
        }

        return PlacementEvaluationResult(
            estimatedLevel = level,
            confidenceScore = conf,
            speakingScore = scoreToCefr(speakingScore),
            listeningScore = scoreToCefr(listeningScore),
            grammarScore = scoreToCefr(grammarScore),
            vocabularyScore = scoreToCefr(vocabScore),
            writingScore = scoreToCefr(writingScore),
            pronunciationScore = scoreToCefr(speakingScore),
            summaryFeedback = "You have solid everyday vocabulary and comprehension fundamentals. Your main growth opportunities lie in mastering Dativ prepositions and complex subordinate clause word order.",
            recommendedFirstTopic = "A2.2 Dativ Cases & Word Order Mastery"
        )
    }

    fun evaluateShadowing(targetSentence: String, userSpokenText: String): ShadowingAnalysis {
        val targetWords = targetSentence.lowercase(Locale.GERMAN).replace(Regex("[.,!?;:\"]"), "").split("\\s+".toRegex())
        val spokenWords = userSpokenText.lowercase(Locale.GERMAN).replace(Regex("[.,!?;:\"]"), "").split("\\s+".toRegex())

        var matchCount = 0
        val missing = mutableListOf<String>()

        for (tw in targetWords) {
            if (spokenWords.contains(tw)) {
                matchCount++
            } else {
                missing.add(tw)
            }
        }

        val accuracy = if (targetWords.isNotEmpty()) (matchCount * 100) / targetWords.size else 85
        val clampedAccuracy = accuracy.coerceIn(50, 98)

        val praise = when {
            clampedAccuracy >= 90 -> "Ausgezeichnete Aussprache! Natürliche Satzmelodie und klare Vokale."
            clampedAccuracy >= 75 -> "Sehr gut! Achte noch etwas auf das Tempo und die Wortendungen."
            else -> "Guter Versuch! Höre dir die Aussprache noch einmal in reduzierter Geschwindigkeit an."
        }

        return ShadowingAnalysis(
            accuracyScore = clampedAccuracy,
            rhythmScore = (clampedAccuracy - 5).coerceAtLeast(60),
            fluencyScore = (clampedAccuracy - 2).coerceAtLeast(65),
            recognizedText = userSpokenText,
            missingOrMispronouncedWords = missing.take(3),
            praiseOrTip = praise
        )
    }

    fun evaluateWriting(text: String, taskType: String, targetCEFR: String): WritingEvaluationReport {
        val wordCount = text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        var grammarScore = 80
        var vocabScore = 75
        var coherenceScore = 85

        if (wordCount < 15) {
            grammarScore = 60
            vocabScore = 60
            coherenceScore = 65
        } else if (wordCount > 40) {
            grammarScore = 88
            vocabScore = 85
            coherenceScore = 90
        }

        var corrected = text
            .replace("ich habe gefahren", "ich bin gefahren")
            .replace("weil ich habe", "weil ich Zeit habe")
            .replace("helfe der Mann", "helfe dem Mann")

        val improved = if (taskType.contains("Email", ignoreCase = true) || taskType.contains("Brief", ignoreCase = true)) {
            "Sehr geehrte Damen und Herren,\n\nich schreibe Ihnen bezüglich Ihrer Anzeige. $corrected\n\nIch freue mich auf Ihre baldige Rückmeldung.\n\nMit freundlichen Grüßen,\nAlex"
        } else {
            "$corrected Zusammenfassend lässt sich sagen, dass diese Erfahrung von großer Bedeutung war."
        }

        return WritingEvaluationReport(
            overallScorePercent = (grammarScore + vocabScore + coherenceScore) / 3,
            estimatedCEFR = targetCEFR,
            grammarScore = grammarScore,
            vocabularyScore = vocabScore,
            coherenceScore = coherenceScore,
            originalText = text,
            correctedText = corrected,
            detailedFeedback = "Guter Satzbau und logische Struktur. Achte bei Nebensätzen auf die Verbstellung am Satzende und verwende im Schriftlichen passende Höflichkeitsfloskeln.",
            improvedNativeVersion = improved
        )
    }
}
