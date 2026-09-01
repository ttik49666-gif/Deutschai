package com.example.ai

import com.example.data.model.RoleplayScenario
import com.example.data.model.UserProfile

data class RoleplayTurnResult(
    val aiResponse: TutorAnalysis,
    val completedMilestones: List<String>,
    val activeMilestoneIndex: Int,
    val isScenarioCompleted: Boolean
)

class RoleplayEngine(
    private val tutorOrchestrator: TutorOrchestrator
) {

    suspend fun processRoleplayTurn(
        userUtterance: String,
        scenario: RoleplayScenario,
        userProfile: UserProfile,
        completedMilestoneIndices: Set<Int>,
        history: List<Pair<String, String>>
    ): RoleplayTurnResult {
        val scenarioContext = buildString {
            append("Roleplay: ${scenario.title} (${scenario.cefrLevel}). ")
            append("Context: ${scenario.context}. ")
            append("User Goal: ${scenario.goal}. ")
            append("AI Persona: ${scenario.aiRole}. ")
            append("Milestones: ${scenario.milestones.joinToString(" -> ")}.")
        }

        val analysis = tutorOrchestrator.processUserUtterance(
            userText = userUtterance,
            userProfile = userProfile,
            mode = com.example.data.model.ConversationMode.ROLEPLAY,
            scenarioContext = scenarioContext,
            history = history
        )

        // Evaluate milestone progression
        val remainingIndices = scenario.milestones.indices.filter { it !in completedMilestoneIndices }
        val newCompleted = completedMilestoneIndices.toMutableSet()

        if (remainingIndices.isNotEmpty()) {
            val nextTargetIdx = remainingIndices.first()
            val milestoneText = scenario.milestones[nextTargetIdx].lowercase()

            val keywords = milestoneText.split(" ", "/", ",", "-")
                .map { it.trim().lowercase() }
                .filter { it.length > 3 }

            val userTextLower = userUtterance.lowercase()
            val aiTextLower = analysis.responseText.lowercase()

            val matchesUser = keywords.any { it in userTextLower }
            val matchesAi = keywords.any { it in aiTextLower }

            if (matchesUser || matchesAi || userUtterance.length > 15) {
                newCompleted.add(nextTargetIdx)
            }
        }

        val completedNames = newCompleted.mapNotNull { scenario.milestones.getOrNull(it) }
        val activeIndex = scenario.milestones.indices.firstOrNull { it !in newCompleted } ?: scenario.milestones.size
        val isAllCompleted = newCompleted.size >= scenario.milestones.size

        return RoleplayTurnResult(
            aiResponse = analysis,
            completedMilestones = completedNames,
            activeMilestoneIndex = activeIndex,
            isScenarioCompleted = isAllCompleted
        )
    }
}
