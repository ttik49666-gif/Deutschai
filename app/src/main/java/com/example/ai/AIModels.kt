package com.example.ai

import com.example.data.model.ArabicTranslationDetail
import com.example.data.model.CorrectionLevel
import com.example.data.model.TargetDialect
import com.example.data.model.TutorPersonality

data class TutorAnalysis(
    val responseText: String,
    val translation: String,
    val arabicDetail: ArabicTranslationDetail? = null,
    val hasCorrection: Boolean = false,
    val userOriginalUtterance: String? = null,
    val correctedUtterance: String? = null,
    val grammarExplanation: String? = null,
    val grammarExplanationAr: String? = null,
    val naturalAlternative: String? = null,
    val targetedGrammarCategory: String? = null,
    val suggestedFollowUp: String? = null,
    val suggestedFollowUpAr: String? = null,
    val cefrLevel: String = "A2"
)

data class PlacementEvaluationResult(
    val estimatedLevel: String, // e.g. "A2.2"
    val confidenceScore: Int, // e.g. 87%
    val grammarScore: String, // "A2"
    val vocabularyScore: String, // "B1"
    val readingScore: String, // "B1"
    val listeningScore: String, // "B1"
    val writingScore: String, // "A2"
    val speakingScore: String, // "A2"
    val pronunciationScore: String, // "A2"
    val summaryFeedback: String,
    val summaryFeedbackAr: String = "",
    val recommendedFirstTopic: String,
    val recommendedFirstTopicAr: String = ""
)

data class WritingEvaluationReport(
    val overallScorePercent: Int,
    val estimatedCEFR: String,
    val grammarScore: Int,
    val vocabularyScore: Int,
    val coherenceScore: Int,
    val taskFulfillmentScore: Int = 80,
    val originalText: String,
    val correctedText: String,
    val detailedFeedback: String,
    val detailedFeedbackAr: String = "",
    val improvedNativeVersion: String,
    val keyStrengths: List<String> = emptyList(),
    val keyStrengthsAr: List<String> = emptyList(),
    val areasForImprovement: List<String> = emptyList(),
    val areasForImprovementAr: List<String> = emptyList()
)

enum class WordPronunciationStatus {
    CORRECT,
    MINOR_FLAW,
    MISPRONOUNCED,
    MISSED
}

data class PronunciationWordScore(
    val word: String,
    val accuracy: Int, // 0 - 100
    val status: WordPronunciationStatus,
    val phoneticTip: String? = null,
    val phoneticTipAr: String? = null
)

data class ShadowingAnalysis(
    val accuracyScore: Int, // 0 - 100
    val rhythmScore: Int,
    val fluencyScore: Int,
    val recognizedText: String,
    val wordScores: List<PronunciationWordScore> = emptyList(),
    val missingOrMispronouncedWords: List<String> = emptyList(),
    val praiseOrTip: String,
    val praiseOrTipAr: String = ""
)

data class TutorPromptRequest(
    val userInput: String,
    val personality: TutorPersonality,
    val correctionLevel: CorrectionLevel,
    val userLevel: String,
    val dialect: TargetDialect = TargetDialect.MSA,
    val modeTitle: String,
    val scenarioContext: String? = null,
    val history: List<Pair<String, String>> = emptyList()
)

data class AdaptiveCurriculumPlan(
    val recommendedFocus: String,
    val recommendedFocusAr: String,
    val dueSrsCount: Int,
    val weaknessInsights: List<String>,
    val weaknessInsightsAr: List<String>,
    val priorityLessons: List<String>
)
