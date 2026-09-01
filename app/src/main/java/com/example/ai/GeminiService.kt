package com.example.ai

import com.example.data.model.ConversationMode
import com.example.data.model.CorrectionLevel
import com.example.data.model.TargetDialect
import com.example.data.model.TutorPersonality

/**
 * Deprecated client-side direct caller.
 * Routed securely through AIProxyRepository and LocalSmartTutorEngine.
 */
@Deprecated("Use AIProxyRepository or TutorOrchestrator for secure, proxied and offline-capable AI calls")
class GeminiService(
    private val proxyRepository: AIProxyRepository = AIProxyRepositoryImpl()
) {

    suspend fun generateTutorResponse(
        userInput: String,
        personality: TutorPersonality,
        correctionLevel: CorrectionLevel,
        userLevel: String,
        mode: ConversationMode,
        scenarioContext: String?,
        history: List<Pair<String, String>>
    ): TutorAnalysis {
        val request = TutorPromptRequest(
            userInput = userInput,
            personality = personality,
            correctionLevel = correctionLevel,
            userLevel = userLevel,
            dialect = TargetDialect.MSA,
            modeTitle = mode.title,
            scenarioContext = scenarioContext,
            history = history
        )
        return proxyRepository.generateTutorResponse(request, mode)
    }
}
