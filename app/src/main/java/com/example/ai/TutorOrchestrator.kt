package com.example.ai

import com.example.data.model.ConversationMode
import com.example.data.model.CorrectionLevel
import com.example.data.model.TargetDialect
import com.example.data.model.TutorPersonality
import com.example.data.model.UserProfile

class TutorOrchestrator(
    private val aiProxyRepository: AIProxyRepository = AIProxyRepositoryImpl()
) {

    suspend fun processUserUtterance(
        userText: String,
        userProfile: UserProfile,
        mode: ConversationMode,
        scenarioContext: String? = null,
        history: List<Pair<String, String>> = emptyList()
    ): TutorAnalysis {
        val request = TutorPromptRequest(
            userInput = userText,
            personality = userProfile.tutorPersonality,
            correctionLevel = userProfile.correctionLevel,
            userLevel = userProfile.currentLevel,
            dialect = userProfile.targetDialect,
            modeTitle = mode.title,
            scenarioContext = scenarioContext,
            history = history
        )
        return aiProxyRepository.generateTutorResponse(request, mode)
    }

    suspend fun evaluateWritingSubmission(
        essayText: String,
        prompt: String,
        targetCEFR: String
    ): WritingEvaluationReport {
        return aiProxyRepository.evaluateWriting(essayText, prompt, targetCEFR)
    }

    suspend fun evaluatePlacementTest(
        grammarScore: Int,
        vocabScore: Int,
        readingScore: Int,
        listeningScore: Int,
        speakingScore: Int,
        writingScore: Int
    ): PlacementEvaluationResult {
        return aiProxyRepository.evaluatePlacement(
            grammarScore = grammarScore,
            vocabScore = vocabScore,
            readingScore = readingScore,
            listeningScore = listeningScore,
            speakingScore = speakingScore,
            writingScore = writingScore
        )
    }

    suspend fun evaluateShadowingAudio(
        targetSentence: String,
        userSpokenText: String
    ): ShadowingAnalysis {
        return aiProxyRepository.evaluatePronunciation(targetSentence, userSpokenText)
    }
}
