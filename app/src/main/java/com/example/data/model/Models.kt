package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CEFRLevel(val label: String, val badgeColorHex: Long) {
    A1("A1 Beginner", 0xFF3B82F6),
    A2("A2 Elementary", 0xFF06B6D4),
    B1("B1 Intermediate", 0xFF10B981),
    B2("B2 Upper Intermediate", 0xFFF59E0B),
    C1("C1 Advanced", 0xFFEF4444)
}

enum class TutorPersonality(val title: String, val emoji: String, val tagline: String, val stylePrompt: String) {
    FRIENDLY("Friendly & Encouraging", "😊", "Warm, uplifting, praises every effort.", "Be very warm, encouraging, positive, and supportive."),
    PROFESSIONAL("Professional Coach", "💼", "Direct, structured, business-ready German.", "Be structured, concise, professional, and clear."),
    STRICT("Strict & Precise", "🎯", "Uncompromising on cases, endings, and word order.", "Be strict, exact, point out errors directly and demand correction."),
    PATIENT("Patient Mentor", "🌿", "Gentle pace, detailed explanations, no rush.", "Be very patient, explain step-by-step with clear examples."),
    ENERGETIC("Energetic & Dynamic", "⚡", "High energy, fast-paced challenges, fun analogies.", "Be enthusiastic, energetic, exciting, and dynamic."),
    CALM("Calm & Mindful", "☕", "Relaxed conversations, natural everyday flow.", "Be relaxed, mindful, calm, and focus on natural conversation flow.")
}

enum class CorrectionLevel(val title: String, val description: String) {
    MINIMAL("Minimal", "Only corrects severe errors that impede understanding."),
    BALANCED("Balanced", "Corrects key grammar, case, and word-order mistakes."),
    DETAILED("Detailed", "Comprehensive post-utterance analysis and natural alternatives.")
}

enum class ConversationMode(val title: String, val icon: String, val description: String) {
    FREE_TALK("Free Talk", "💬", "Open-ended natural conversation about any topic."),
    TEACHER("AI Teacher", "🎓", "Structured tutoring with live grammar and vocabulary drills."),
    ROLEPLAY("Real-World Roleplay", "🎭", "20+ immersive simulations in restaurants, train stations, jobs, etc."),
    DEBATE("Debate & Opinion", "⚔️", "Argue points, defend views, and use advanced connectors."),
    INTERVIEW("Job & University Interview", "👔", "Prepare for career and academic interviews in Germany."),
    STORY("Storyteller", "📖", "Interactive branching stories using past tenses (Perfekt / Präteritum)."),
    PRONUNCIATION_COACH("Pronunciation Coach", "🗣️", "Detailed phonetic, vowel clarity, and shadowing coaching."),
    EXAM_SPEAKING("Goethe / telc Speaking", "📝", "Official exam format speaking tasks with time limits and scoring.")
}

@Entity(tableName = "users")
data class UserProfile(
    @PrimaryKey val id: String = "primary_user",
    val name: String = "Alex",
    val currentLevel: String = "A2.2",
    val estimatedScorePercent: Int = 64,
    val confidencePercent: Int = 87,
    val dailyGoalMinutes: Int = 15,
    val todayMinutesLearned: Int = 12,
    val streakDays: Int = 14,
    val totalXp: Int = 2850,
    val tutorPersonality: TutorPersonality = TutorPersonality.FRIENDLY,
    val correctionLevel: CorrectionLevel = CorrectionLevel.BALANCED,
    val voiceSpeed: Float = 1.0f,
    val handsFreeEnabled: Boolean = false,
    val isOnboarded: Boolean = true,
    val primaryGoal: String = "Speak German confidently",
    val weakSkillsSummary: String = "Dativ Cases & Verb-End Word Order",
    val strongSkillsSummary: String = "Everyday Vocabulary & Reading Comprehension"
)

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val id: String,
    val cefrLevel: String, // A1, A2, B1, B2, C1
    val moduleNumber: Int,
    val moduleTitle: String,
    val lessonTitle: String,
    val description: String,
    val skillType: String, // Grammar, Vocabulary, Speaking, Listening, Writing
    val masteryScore: Int = 0, // 0 - 100
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val estimatedMinutes: Int = 10,
    val xpReward: Int = 50
)

@Entity(tableName = "vocabulary")
data class VocabularyItem(
    @PrimaryKey val id: String,
    val word: String,
    val article: String? = null, // "der", "die", "das"
    val plural: String? = null,
    val englishMeaning: String,
    val cefrLevel: String,
    val exampleDe: String,
    val exampleEn: String,
    val masteryScore: Int = 0, // 0 - 100
    val mistakeCount: Int = 0,
    val lastReviewed: Long = System.currentTimeMillis(),
    val nextReview: Long = System.currentTimeMillis(),
    val category: String = "General"
)

@Entity(tableName = "grammar_topics")
data class GrammarTopic(
    @PrimaryKey val id: String,
    val title: String,
    val cefrLevel: String,
    val category: String, // Articles, Cases, Tenses, Word Order, Modal Verbs, Prepositions, etc.
    val explanation: String,
    val formulaRule: String,
    val exampleRight: String,
    val exampleWrong: String,
    val whyWrong: String,
    val masteryScore: Int = 50,
    val isWeakArea: Boolean = false
)

@Entity(tableName = "mistakes")
data class MistakeItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userSaid: String,
    val correctVersion: String,
    val explanation: String,
    val grammarCategory: String, // e.g., "Perfekt Auxiliary (sein/haben)", "Dativ Case", "Word Order"
    val cefrLevel: String = "A2",
    val timestamp: Long = System.currentTimeMillis(),
    val mastery: Int = 40,
    val retryCount: Int = 1,
    val isResolved: Boolean = false
)

@Entity(tableName = "scenarios")
data class RoleplayScenario(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val category: String, // Daily Life, Travel, Career, Bureaucracy, Sports & Hobbies
    val cefrLevel: String,
    val context: String,
    val goal: String,
    val aiRole: String,
    val userRole: String,
    val initialMessage: String,
    val suggestedPhrases: List<String> = emptyList()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String = "default",
    val sender: String, // "USER" or "AI"
    val text: String,
    val translation: String? = null,
    val grammarFeedback: String? = null,
    val naturalAlternative: String? = null,
    val pronunciationScore: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class PlacementSkillScores(
    val speaking: String = "A2",
    val listening: String = "B1",
    val grammar: String = "A2",
    val vocabulary: String = "B1",
    val writing: String = "A2",
    val pronunciation: String = "A2",
    val estimatedCEFR: String = "A2.2",
    val confidence: Int = 87
)

data class ShadowingTask(
    val id: String,
    val cefrLevel: String,
    val germanSentence: String,
    val englishMeaning: String,
    val phoneticTip: String,
    val targetAudioSpeed: Float = 0.9f
)

data class WritingEvaluation(
    val originalText: String,
    val correctedText: String,
    val grammarScore: Int,
    val vocabularyScore: Int,
    val coherenceScore: Int,
    val cefrLevel: String,
    val feedback: String,
    val improvedNativeVersion: String
)
