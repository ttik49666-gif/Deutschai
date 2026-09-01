package com.example.ai

import com.example.data.model.ConversationMode
import com.example.data.model.CorrectionLevel
import com.example.data.model.TutorPersonality

class TutorOrchestrator(
    private val geminiService: GeminiService = GeminiService()
) {

    suspend fun getTutorResponse(
        userText: String,
        personality: TutorPersonality,
        correctionLevel: CorrectionLevel,
        userLevel: String,
        mode: ConversationMode,
        scenarioContext: String? = null,
        history: List<Pair<String, String>> = emptyList()
    ): TutorAnalysis {
        // Try Gemini AI first
        val geminiResult = geminiService.generateTutorResponse(
            userInput = userText,
            personality = personality,
            correctionLevel = correctionLevel,
            userLevel = userLevel,
            mode = mode,
            scenarioContext = scenarioContext,
            history = history
        )

        if (geminiResult != null) {
            return geminiResult
        }

        // Resilient, deep local German NLP engine fallback
        return LocalSmartTutorEngine.analyzeAndRespond(
            userText = userText,
            personality = personality,
            correctionLevel = correctionLevel,
            userLevel = userLevel,
            mode = mode,
            scenarioContext = scenarioContext,
            history = history
        )
    }
}
