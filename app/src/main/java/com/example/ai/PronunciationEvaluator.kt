package com.example.ai

import java.util.Locale
import kotlin.math.max
import kotlin.math.min

object PronunciationEvaluator {

    fun evaluateSpeech(
        targetSentence: String,
        userSpokenText: String
    ): ShadowingAnalysis {
        val cleanTarget = cleanPunctuation(targetSentence)
        val cleanSpoken = cleanPunctuation(userSpokenText)

        val targetWords = cleanTarget.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val spokenWords = cleanSpoken.split("\\s+".toRegex()).filter { it.isNotBlank() }

        if (targetWords.isEmpty()) {
            return ShadowingAnalysis(
                accuracyScore = 90,
                rhythmScore = 85,
                fluencyScore = 88,
                recognizedText = userSpokenText,
                wordScores = emptyList(),
                missingOrMispronouncedWords = emptyList(),
                praiseOrTip = "Ausgezeichnet!",
                praiseOrTipAr = "ممتاز! نطق دقيق وسليم."
            )
        }

        val wordScores = mutableListOf<PronunciationWordScore>()
        val missingOrMispronounced = mutableListOf<String>()
        var totalWordAccuracy = 0

        var spokenIndex = 0
        for (tw in targetWords) {
            val targetLower = tw.lowercase(Locale.GERMAN)
            var bestScore = 0
            var matchedSpokenWord: String? = null
            var matchedIdx = -1

            // Lookahead up to 3 words in spoken list to handle skips
            val lookaheadLimit = min(spokenWords.size, spokenIndex + 4)
            for (i in spokenIndex until lookaheadLimit) {
                val sw = spokenWords[i].lowercase(Locale.GERMAN)
                val sim = calculateSimilarity(targetLower, sw)
                if (sim > bestScore) {
                    bestScore = sim
                    matchedSpokenWord = sw
                    matchedIdx = i
                }
            }

            if (matchedIdx != -1) {
                spokenIndex = matchedIdx + 1
            }

            val (status, tipDe, tipAr) = when {
                bestScore >= 85 -> Triple(
                    WordPronunciationStatus.CORRECT,
                    "Klar und fehlerfrei ausgesprochen.",
                    "نطق سليم وواضح تماماً."
                )
                bestScore >= 60 -> {
                    missingOrMispronounced.add(tw)
                    val tip = generatePhoneticTip(tw)
                    Triple(
                        WordPronunciationStatus.MINOR_FLAW,
                        "Leichte Aussprache-Ungenauigkeit: $tip",
                        "دقة متوسطة: انتبه لنطق الحروف الخاصة مثل Umlaut أو ch."
                    )
                }
                bestScore > 20 -> {
                    missingOrMispronounced.add(tw)
                    val tip = generatePhoneticTip(tw)
                    Triple(
                        WordPronunciationStatus.MISPRONOUNCED,
                        "Wort ungenau: $tip",
                        "نطق غير دقيق للكلمة، يرجى الاستماع مرة أخرى وإعادة النطق."
                    )
                }
                else -> {
                    missingOrMispronounced.add(tw)
                    Triple(
                        WordPronunciationStatus.MISSED,
                        "Wort übersprungen oder nicht verstanden.",
                        "تم تخطي الكلمة أو لم يتم التقاطها."
                    )
                }
            }

            wordScores.add(
                PronunciationWordScore(
                    word = tw,
                    accuracy = bestScore,
                    status = status,
                    phoneticTip = tipDe,
                    phoneticTipAr = tipAr
                )
            )
            totalWordAccuracy += bestScore
        }

        val accuracyScore = (totalWordAccuracy / targetWords.size).coerceIn(10, 99)
        val lengthRatio = if (targetWords.isNotEmpty()) spokenWords.size.toFloat() / targetWords.size else 1f
        val rhythmScore = ((accuracyScore * 0.9f) * min(1f, lengthRatio)).toInt().coerceIn(30, 98)
        val fluencyScore = ((accuracyScore * 0.95f) + (if (missingOrMispronounced.isEmpty()) 5 else -5)).toInt().coerceIn(35, 99)

        val (praiseDe, praiseAr) = when {
            accuracyScore >= 90 -> Pair(
                "Ausgezeichnete Aussprache! Klare Vokale und natürliche Sprachmelodie.",
                "ممتاز جداً! نطق ألماني طبيعي ومخارج حروف واضحة ونبرة متقنة."
            )
            accuracyScore >= 75 -> Pair(
                "Sehr gut gesprochen! Achte noch etwas auf Umlaute (ä, ö, ü) und das weiche 'ch'.",
                "جيد جداً! انتبه لنطق الحركات المُمَالة (ä, ö, ü) وحرف ch الناعم."
            )
            accuracyScore >= 55 -> Pair(
                "Guter Versuch! Höre dir die Audioaufnahme nochmals in reduzierter Geschwindigkeit an.",
                "محاولة جيدة! استمع إلى الجملة بالسرعة البطيئة وركز على نهايات الكلمات."
            )
            else -> Pair(
                "Versuche es noch einmal. Sprich jedes Wort langsam und deutlich ins Mikrofon.",
                "أعد المحاولة وتحدث ببطء ووضوح أمام الميكروفون."
            )
        }

        return ShadowingAnalysis(
            accuracyScore = accuracyScore,
            rhythmScore = rhythmScore,
            fluencyScore = fluencyScore,
            recognizedText = userSpokenText,
            wordScores = wordScores,
            missingOrMispronouncedWords = missingOrMispronounced.take(4),
            praiseOrTip = praiseDe,
            praiseOrTipAr = praiseAr
        )
    }

    private fun generatePhoneticTip(word: String): String {
        val lower = word.lowercase(Locale.GERMAN)
        return when {
            lower.contains("ch") -> "Achte auf das 'ch' (weich wie in 'ich' oder kehlig wie in 'Bach')."
            lower.contains("ü") -> "Das 'ü': Lippen spitzen wie bei 'u', aber 'i' sagen."
            lower.contains("ö") -> "Das 'ö': Lippen runden wie bei 'o', aber 'e' sagen."
            lower.contains("ä") -> "Das 'ä': Offenes langes 'e'."
            lower.contains("st") || lower.contains("sp") -> "Am Wortanfang 'st' und 'sp' als 'scht' und 'schp' aussprechen."
            lower.contains("ß") -> "Das 'ß' wird als scharfes 's' gesprochen."
            else -> "Deutlich silbenweise betonen."
        }
    }

    private fun cleanPunctuation(text: String): String {
        return text.replace(Regex("[.,!?;:\"'()\\-\\[\\]]"), " ").trim()
    }

    private fun calculateSimilarity(s1: String, s2: String): Int {
        if (s1 == s2) return 100
        val n1 = normalizeUmlauts(s1)
        val n2 = normalizeUmlauts(s2)
        if (n1 == n2) return 92

        val dist = levenshtein(n1, n2)
        val maxLen = max(n1.length, n2.length)
        if (maxLen == 0) return 100
        val sim = ((maxLen - dist).toFloat() / maxLen * 100).toInt()
        return sim.coerceIn(0, 100)
    }

    private fun normalizeUmlauts(s: String): String {
        return s.lowercase(Locale.GERMAN)
            .replace("ä", "ae")
            .replace("ö", "oe")
            .replace("ü", "ue")
            .replace("ß", "ss")
    }

    private fun levenshtein(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = Array(lhsLength + 1) { it }
        var newCost = Array(lhsLength + 1) { 0 }

        for (i in 1..rhsLength) {
            newCost[0] = i
            for (j in 1..lhsLength) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }
        return cost[lhsLength]
    }
}
