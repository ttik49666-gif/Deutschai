package com.example.ai

import com.example.data.model.CorrectionLevel
import com.example.data.model.TutorPersonality

data class TutorAnalysis(
    val responseText: String,
    val translation: String,
    val hasCorrection: Boolean = false,
    val userOriginalUtterance: String? = null,
    val correctedUtterance: String? = null,
    val grammarExplanation: String? = null,
    val naturalAlternative: String? = null,
    val targetedGrammarCategory: String? = null,
    val suggestedFollowUp: String? = null,
    val cefrLevel: String = "A2"
)

data class PlacementEvaluationResult(
    val estimatedLevel: String, // e.g. "A2.2"
    val confidenceScore: Int, // e.g. 87%
    val speakingScore: String, // "A2"
    val listeningScore: String, // "B1"
    val grammarScore: String, // "A2"
    val vocabularyScore: String, // "B1"
    val writingScore: String, // "A2"
    val pronunciationScore: String, // "A2"
    val summaryFeedback: String,
    val recommendedFirstTopic: String
)

data class WritingEvaluationReport(
    val overallScorePercent: Int,
    val estimatedCEFR: String,
    val grammarScore: Int,
    val vocabularyScore: Int,
    val coherenceScore: Int,
    val originalText: String,
    val correctedText: String,
    val detailedFeedback: String,
    val improvedNativeVersion: String
)

data class ShadowingAnalysis(
    val accuracyScore: Int, // 0 - 100
    val rhythmScore: Int,
    val fluencyScore: Int,
    val recognizedText: String,
    val missingOrMispronouncedWords: List<String>,
    val praiseOrTip: String
)
