package com.example.ai

class WritingEvaluator(
    private val tutorOrchestrator: TutorOrchestrator
) {

    suspend fun evaluateEssay(
        text: String,
        prompt: String,
        targetCEFR: String
    ): WritingEvaluationReport {
        return tutorOrchestrator.evaluateWritingSubmission(text, prompt, targetCEFR)
    }
}
