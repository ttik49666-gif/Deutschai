package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.SeedData
import com.example.data.model.ChatMessage
import com.example.data.model.CorrectionLevel
import com.example.data.model.GrammarTopic
import com.example.data.model.Lesson
import com.example.data.model.MistakeItem
import com.example.data.model.RoleplayScenario
import com.example.data.model.TutorPersonality
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import kotlinx.coroutines.flow.Flow

class DeutschAIRepository(private val database: AppDatabase) {

    val userProfile: Flow<UserProfile?> = database.userDao().getUserProfile()
    val allLessons: Flow<List<Lesson>> = database.lessonDao().getAllLessons()
    val allVocabulary: Flow<List<VocabularyItem>> = database.vocabularyDao().getAllVocabulary()
    val weakVocabulary: Flow<List<VocabularyItem>> = database.vocabularyDao().getWeakVocabulary()
    val allGrammarTopics: Flow<List<GrammarTopic>> = database.grammarDao().getAllGrammarTopics()
    val weakGrammarTopics: Flow<List<GrammarTopic>> = database.grammarDao().getWeakGrammarTopics()
    val allMistakes: Flow<List<MistakeItem>> = database.mistakeDao().getAllMistakes()
    val unresolvedMistakes: Flow<List<MistakeItem>> = database.mistakeDao().getUnresolvedMistakes()
    val allScenarios: Flow<List<RoleplayScenario>> = database.scenarioDao().getAllScenarios()

    fun getChatMessages(sessionId: String): Flow<List<ChatMessage>> =
        database.chatMessageDao().getMessagesForSession(sessionId)

    suspend fun initializeIfEmpty() {
        val user = database.userDao().getUserProfileOnce()
        if (user == null) {
            database.userDao().insertOrUpdateUser(SeedData.initialUser)
            database.lessonDao().insertLessons(SeedData.initialLessons)
            database.vocabularyDao().insertVocabulary(SeedData.initialVocabulary)
            database.grammarDao().insertGrammarTopics(SeedData.initialGrammarTopics)
            SeedData.initialMistakes.forEach { database.mistakeDao().insertMistake(it) }
            database.scenarioDao().insertScenarios(SeedData.initialScenarios)
        }
    }

    suspend fun updateUser(user: UserProfile) {
        database.userDao().insertOrUpdateUser(user)
    }

    suspend fun updatePersonality(personality: TutorPersonality) {
        val current = database.userDao().getUserProfileOnce() ?: SeedData.initialUser
        database.userDao().insertOrUpdateUser(current.copy(tutorPersonality = personality))
    }

    suspend fun updateCorrectionLevel(level: CorrectionLevel) {
        val current = database.userDao().getUserProfileOnce() ?: SeedData.initialUser
        database.userDao().insertOrUpdateUser(current.copy(correctionLevel = level))
    }

    suspend fun updateVoiceSpeed(speed: Float) {
        val current = database.userDao().getUserProfileOnce() ?: SeedData.initialUser
        database.userDao().insertOrUpdateUser(current.copy(voiceSpeed = speed))
    }

    suspend fun updatePlacementLevel(level: String, confidence: Int, speaking: String, listening: String, grammar: String, vocab: String, writing: String) {
        val current = database.userDao().getUserProfileOnce() ?: SeedData.initialUser
        database.userDao().insertOrUpdateUser(
            current.copy(
                currentLevel = level,
                confidencePercent = confidence,
                isOnboarded = true,
                weakSkillsSummary = "$grammar Grammar & $speaking Speaking",
                strongSkillsSummary = "$vocab Vocabulary & $listening Comprehension"
            )
        )
    }

    suspend fun completeLesson(lessonId: String, score: Int = 100, xpGained: Int = 50) {
        database.lessonDao().markLessonCompleted(lessonId, true, score)
        val current = database.userDao().getUserProfileOnce() ?: SeedData.initialUser
        database.userDao().insertOrUpdateUser(
            current.copy(
                totalXp = current.totalXp + xpGained,
                todayMinutesLearned = current.todayMinutesLearned + 5,
                estimatedScorePercent = minOf(100, current.estimatedScorePercent + 2)
            )
        )
    }

    suspend fun recordMistake(userSaid: String, correct: String, explanation: String, category: String, cefr: String) {
        val mistake = MistakeItem(
            userSaid = userSaid,
            correctVersion = correct,
            explanation = explanation,
            grammarCategory = category,
            cefrLevel = cefr,
            timestamp = System.currentTimeMillis(),
            mastery = 30,
            isResolved = false
        )
        database.mistakeDao().insertMistake(mistake)
    }

    suspend fun resolveMistake(id: Int) {
        database.mistakeDao().deleteMistake(id)
        val current = database.userDao().getUserProfileOnce() ?: SeedData.initialUser
        database.userDao().insertOrUpdateUser(current.copy(totalXp = current.totalXp + 25))
    }

    suspend fun updateWordMastery(wordId: String, newScore: Int) {
        database.vocabularyDao().updateMastery(wordId, newScore)
    }

    suspend fun saveChatMessage(message: ChatMessage): Long {
        return database.chatMessageDao().insertMessage(message)
    }

    suspend fun clearChatSession(sessionId: String) {
        database.chatMessageDao().clearSession(sessionId)
    }
}
