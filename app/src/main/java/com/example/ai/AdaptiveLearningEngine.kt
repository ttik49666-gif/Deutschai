package com.example.ai

import com.example.data.model.GrammarTopic
import com.example.data.model.MistakeItem
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import kotlin.math.max
import kotlin.math.roundToInt

data class SrsCalculationResult(
    val repetition: Int,
    val intervalDays: Int,
    val easeFactor: Float,
    val nextReviewTimestamp: Long,
    val newMasteryScore: Int
)

data class DiagnosisReport(
    val primaryWeakArea: String,
    val primaryWeakAreaAr: String,
    val estimatedMasteryScore: Int,
    val suggestedDailyGoalMinutes: Int,
    val urgentMistakesToReview: List<MistakeItem>,
    val prioritizedGrammarTopics: List<GrammarTopic>,
    val dueSrsVocabularyCount: Int
)

object AdaptiveLearningEngine {

    /**
     * Genuine SuperMemo-2 (SM-2) Spaced Repetition Calculation
     * @param quality Review rating from 0 (complete blackout) to 5 (perfect recall with no hesitation)
     *                0: Complete blackout
     *                1: Incorrect response, but familiar upon seeing answer
     *                2: Incorrect response, but easy recall upon seeing answer
     *                3: Correct response, recalled with significant difficulty
     *                4: Correct response after a hesitation
     *                5: Perfect recall
     */
    fun calculateSM2(
        currentRepetition: Int,
        currentIntervalDays: Int,
        currentEaseFactor: Float,
        quality: Int,
        now: Long = System.currentTimeMillis()
    ): SrsCalculationResult {
        val q = quality.coerceIn(0, 5)

        val newRepetition: Int
        val newInterval: Int

        if (q >= 3) {
            when (currentRepetition) {
                0 -> {
                    newRepetition = 1
                    newInterval = 1
                }
                1 -> {
                    newRepetition = 2
                    newInterval = 6
                }
                else -> {
                    newRepetition = currentRepetition + 1
                    newInterval = (currentIntervalDays * currentEaseFactor).roundToInt()
                }
            }
        } else {
            // Failed recall resets repetitions
            newRepetition = 0
            newInterval = 1
        }

        // SM-2 Ease Factor formula: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val newEaseFactor = max(
            1.3f,
            currentEaseFactor + (0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f))
        )

        val nextReview = now + (newInterval.toLong() * 24 * 60 * 60 * 1000L)

        // Calculate 0-100 mastery score based on repetition and quality
        val mastery = when {
            newRepetition >= 5 -> (90 + (q * 2)).coerceIn(0, 100)
            newRepetition == 4 -> 85
            newRepetition == 3 -> 75
            newRepetition == 2 -> 60
            newRepetition == 1 -> 45
            else -> if (q >= 3) 35 else 20
        }

        return SrsCalculationResult(
            repetition = newRepetition,
            intervalDays = newInterval,
            easeFactor = newEaseFactor,
            nextReviewTimestamp = nextReview,
            newMasteryScore = mastery
        )
    }

    /**
     * Diagnose current user weaknesses and build an adaptive diagnosis report
     */
    fun diagnoseCurriculum(
        user: UserProfile,
        mistakes: List<MistakeItem>,
        vocabList: List<VocabularyItem>,
        grammarList: List<GrammarTopic>,
        now: Long = System.currentTimeMillis()
    ): DiagnosisReport {
        val unresolvedMistakes = mistakes.filter { !it.isResolved }
        val dueVocabCount = vocabList.count { it.nextReview <= now }

        // Find most frequent error categories
        val mistakeCategories = unresolvedMistakes.groupingBy { it.grammarCategory }.eachCount()
        val topWeakCategory = mistakeCategories.maxByOrNull { it.value }?.key ?: "Dativ & Satzbau"

        val weakGrammar = grammarList.filter { it.isWeakArea || it.masteryScore < 60 }
            .sortedBy { it.masteryScore }

        val topCategoryAr = when {
            topWeakCategory.contains("Dativ", ignoreCase = true) -> "حالة المجرور (Dativ) واستخداماتها"
            topWeakCategory.contains("Perfekt", ignoreCase = true) -> "الماضي المركب والفعل المساعد (sein / haben)"
            topWeakCategory.contains("Nebensatz", ignoreCase = true) || topWeakCategory.contains("Word Order", ignoreCase = true) -> "ترتيب الكلمات وموضع الفعل في الجمل الجانبية (Nebensatz)"
            topWeakCategory.contains("Modal", ignoreCase = true) -> "الأفعال المساعدة والقوس اللغوي (Satzklammer)"
            else -> "قواعد اللغة الألمانية وبناء الجمل"
        }

        val baseMastery = if (vocabList.isNotEmpty()) {
            vocabList.map { it.masteryScore }.average().toInt()
        } else {
            user.estimatedScorePercent
        }

        return DiagnosisReport(
            primaryWeakArea = topWeakCategory,
            primaryWeakAreaAr = topCategoryAr,
            estimatedMasteryScore = baseMastery.coerceIn(20, 98),
            suggestedDailyGoalMinutes = if (unresolvedMistakes.size > 3) 20 else 15,
            urgentMistakesToReview = unresolvedMistakes.take(5),
            prioritizedGrammarTopics = weakGrammar.take(3),
            dueSrsVocabularyCount = dueVocabCount
        )
    }

    /**
     * Generate dynamic daily curriculum plan
     */
    fun generateDailyPlan(
        user: UserProfile,
        diagnosis: DiagnosisReport
    ): AdaptiveCurriculumPlan {
        val focusDe = "Fokus heute: ${diagnosis.primaryWeakArea} & ${diagnosis.dueSrsVocabularyCount} SRS-Wiederholungen"
        val focusAr = "التركيز اليوم: ${diagnosis.primaryWeakAreaAr} ومراجعة ${diagnosis.dueSrsVocabularyCount} بطاقة تكرار متباعد"

        val insightsDe = mutableListOf<String>()
        val insightsAr = mutableListOf<String>()

        if (diagnosis.urgentMistakesToReview.isNotEmpty()) {
            insightsDe.add("Du hast ${diagnosis.urgentMistakesToReview.size} offene Grammatik-Punkte zum Üben.")
            insightsAr.add("لديك ${diagnosis.urgentMistakesToReview.size} نقاط نحوية تحتاج إلى تثبيت.")
        }

        if (diagnosis.dueSrsVocabularyCount > 0) {
            insightsDe.add("${diagnosis.dueSrsVocabularyCount} Vokabeln sind heute für das Spaced-Repetition-System fällig.")
            insightsAr.add("هناك ${diagnosis.dueSrsVocabularyCount} كلمات مستحقة للمراجعة الذكية اليوم.")
        } else {
            insightsDe.add("Dein Vokabel-Gedächtnis ist optimal auf Stand!")
            insightsAr.add("ذاكرتك للكلمات الألمانية في حالة ممتازة ومحدثة!")
        }

        val priorityLessons = diagnosis.prioritizedGrammarTopics.map { it.title }.ifEmpty {
            listOf("Dativ Cases & Verbs", "Nebensatz mit weil & dass")
        }

        return AdaptiveCurriculumPlan(
            recommendedFocus = focusDe,
            recommendedFocusAr = focusAr,
            dueSrsCount = diagnosis.dueSrsVocabularyCount,
            weaknessInsights = insightsDe,
            weaknessInsightsAr = insightsAr,
            priorityLessons = priorityLessons
        )
    }
}
