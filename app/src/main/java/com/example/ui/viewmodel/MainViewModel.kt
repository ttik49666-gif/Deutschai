package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.LocalSmartTutorEngine
import com.example.ai.PlacementEvaluationResult
import com.example.ai.ShadowingAnalysis
import com.example.ai.TutorAnalysis
import com.example.ai.TutorOrchestrator
import com.example.ai.VoiceManager
import com.example.ai.WritingEvaluationReport
import com.example.data.local.AppDatabase
import com.example.data.local.SeedData
import com.example.data.model.ChatMessage
import com.example.data.model.ConversationMode
import com.example.data.model.CorrectionLevel
import com.example.data.model.GrammarTopic
import com.example.data.model.Lesson
import com.example.data.model.MistakeItem
import com.example.data.model.RoleplayScenario
import com.example.data.model.ShadowingTask
import com.example.data.model.TutorPersonality
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import com.example.data.repository.DeutschAIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class MainTab {
    HOME, LEARN, SPEAK, PROGRESS, PROFILE
}

enum class LearnLabMode {
    CURRICULUM, VOCABULARY, GRAMMAR, LISTENING, SHADOWING, WRITING, EXAM_CENTER
}

data class PlacementQuestion(
    val id: Int,
    val skill: String,
    val cefr: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DeutschAIRepository
    private val tutorOrchestrator = TutorOrchestrator()
    val voiceManager = VoiceManager(application)

    init {
        val db = AppDatabase.getInstance(application)
        repository = DeutschAIRepository(db)
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialUser)

    val allLessons: StateFlow<List<Lesson>> = repository.allLessons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialLessons)

    val allVocabulary: StateFlow<List<VocabularyItem>> = repository.allVocabulary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialVocabulary)

    val weakVocabulary: StateFlow<List<VocabularyItem>> = repository.weakVocabulary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGrammarTopics: StateFlow<List<GrammarTopic>> = repository.allGrammarTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialGrammarTopics)

    val weakGrammarTopics: StateFlow<List<GrammarTopic>> = repository.weakGrammarTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMistakes: StateFlow<List<MistakeItem>> = repository.allMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialMistakes)

    val allScenarios: StateFlow<List<RoleplayScenario>> = repository.allScenarios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialScenarios)

    // Navigation & View Flow State
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _showOnboarding = MutableStateFlow(false)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    private val _showPlacementTest = MutableStateFlow(false)
    val showPlacementTest: StateFlow<Boolean> = _showPlacementTest.asStateFlow()

    private val _activeLabMode = MutableStateFlow(LearnLabMode.CURRICULUM)
    val activeLabMode: StateFlow<LearnLabMode> = _activeLabMode.asStateFlow()

    private val _activeScenario = MutableStateFlow<RoleplayScenario?>(null)
    val activeScenario: StateFlow<RoleplayScenario?> = _activeScenario.asStateFlow()

    private val _activeMistakePractice = MutableStateFlow<MistakeItem?>(null)
    val activeMistakePractice: StateFlow<MistakeItem?> = _activeMistakePractice.asStateFlow()

    // Conversation State
    private val _conversationMode = MutableStateFlow(ConversationMode.FREE_TALK)
    val conversationMode: StateFlow<ConversationMode> = _conversationMode.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "AI",
                text = "Guten Tag Alex! Ich bin dein persönlicher Deutschlehrer. Worüber möchtest du heute sprechen oder was möchtest du üben?",
                translation = "Good day Alex! I am your personal German tutor. What would you like to talk about today or what would you like to practice?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isTutorThinking = MutableStateFlow(false)
    val isTutorThinking: StateFlow<Boolean> = _isTutorThinking.asStateFlow()

    private val _lastTutorAnalysis = MutableStateFlow<TutorAnalysis?>(null)
    val lastTutorAnalysis: StateFlow<TutorAnalysis?> = _lastTutorAnalysis.asStateFlow()

    private val _handsFreeMode = MutableStateFlow(false)
    val handsFreeMode: StateFlow<Boolean> = _handsFreeMode.asStateFlow()

    // Shadowing Lab State
    val shadowingTasks = listOf(
        ShadowingTask("sh1", "A2", "Ich fahre jeden Morgen mit der U-Bahn zur Arbeit.", "I ride the subway to work every morning.", "Stress 'U-Bahn' on the first syllable.", 0.85f),
        ShadowingTask("sh2", "A2", "Könnten Sie mir bitte sagen, wo der Hauptbahnhof ist?", "Could you please tell me where the central station is?", "Soft 'ch' in 'Ich' and 'Hauptbahnhof'.", 0.85f),
        ShadowingTask("sh3", "B1", "Weil das Wetter so schön war, haben wir einen Ausflug gemacht.", "Because the weather was so nice, we went on an excursion.", "Notice the verb 'gemacht' at the final position.", 0.9f),
        ShadowingTask("sh4", "B2", "Es steht außer Frage, dass wir eine nachhaltige Lösung finden müssen.", "It is out of the question that we must find a sustainable solution.", "Clear cadence on 'außer Frage'.", 0.95f)
    )
    private val _currentShadowingIndex = MutableStateFlow(0)
    val currentShadowingIndex: StateFlow<Int> = _currentShadowingIndex.asStateFlow()

    private val _shadowingReport = MutableStateFlow<ShadowingAnalysis?>(null)
    val shadowingReport: StateFlow<ShadowingAnalysis?> = _shadowingReport.asStateFlow()

    // Writing Lab State
    private val _writingReport = MutableStateFlow<WritingEvaluationReport?>(null)
    val writingReport: StateFlow<WritingEvaluationReport?> = _writingReport.asStateFlow()

    private val _isEvaluatingWriting = MutableStateFlow(false)
    val isEvaluatingWriting: StateFlow<Boolean> = _isEvaluatingWriting.asStateFlow()

    // Placement Test Questions
    val placementQuestions = listOf(
        PlacementQuestion(1, "Grammar (Articles)", "A1", "Wählen Sie den richtigen Artikel: ___ Tisch ist neu.", listOf("Der", "Die", "Das", "Den"), 0, "Tisch ist maskulin: 'Der Tisch'."),
        PlacementQuestion(2, "Grammar (Akkusativ)", "A1", "Ich kaufe ___ Apfel.", listOf("der", "den", "dem", "das"), 1, "Akkusativ maskulin: 'den Apfel'."),
        PlacementQuestion(3, "Grammar (Perfekt)", "A2", "Gestern ___ wir nach Hamburg gefahren.", listOf("haben", "sind", "wurden", "hatten"), 1, "Bewegungsverben bilden das Perfekt mit 'sein' (wir sind gefahren)."),
        PlacementQuestion(4, "Grammar (Dativ)", "A2", "Der Lehrer hilft ___ Schülern.", listOf("die", "den", "der", "dem"), 1, "Dativ Plural: 'den Schülern'."),
        PlacementQuestion(5, "Word Order (Nebensatz)", "A2", "Er lernt fleißig, weil er die Prüfung bestehen ___.", listOf("will", "wollen", "gewollt", "wollte"), 0, "Im Nebensatz mit 'weil' steht das konjugierte Verb 'will' am Satzende."),
        PlacementQuestion(6, "Vocabulary (B1)", "B1", "Welches Wort passt? Ich habe eine wichtige ___ getroffen.", listOf("Erfahrung", "Entscheidung", "Gelegenheit", "Verzögerung"), 1, "'Eine Entscheidung treffen' ist eine feste Nomen-Verb-Verbindung."),
        PlacementQuestion(7, "Reading Comprehension", "B1", "Text: 'Wegen Bauarbeiten entfallen alle Züge zwischen Gleis 3 und 5.' Was bedeutet das?", listOf("Züge fahren pünktlich.", "Züge fallen aus.", "Gleis 3 ist neu.", "Fahrkarten sind billiger."), 1, "'Entfallen' bedeutet 'ausfallen'."),
        PlacementQuestion(8, "Listening & Konjunktiv", "B1", "Welcher Satz ist am höflichsten?", listOf("Ich will einen Kaffee.", "Gib mir Kaffee.", "Ich hätte gern eine Tasse Kaffee.", "Ich trinke Kaffee jetzt."), 2, "'Ich hätte gern...' ist die höfliche Konjunktiv II Form.")
    )

    private val _placementResult = MutableStateFlow<PlacementEvaluationResult?>(null)
    val placementResult: StateFlow<PlacementEvaluationResult?> = _placementResult.asStateFlow()

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun openOnboarding() {
        _showOnboarding.value = true
    }

    fun closeOnboarding() {
        _showOnboarding.value = false
    }

    fun openPlacementTest() {
        _showPlacementTest.value = true
        _placementResult.value = null
    }

    fun closePlacementTest() {
        _showPlacementTest.value = false
    }

    fun setLabMode(mode: LearnLabMode) {
        _activeLabMode.value = mode
    }

    fun startScenario(scenario: RoleplayScenario) {
        _activeScenario.value = scenario
        _conversationMode.value = ConversationMode.ROLEPLAY
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "AI",
                text = scenario.initialMessage,
                translation = "Starting scenario: ${scenario.title}. Follow the conversation goal!"
            )
        )
        _currentTab.value = MainTab.SPEAK
    }

    fun startMistakePractice(mistake: MistakeItem) {
        _activeMistakePractice.value = mistake
        _conversationMode.value = ConversationMode.TEACHER
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "AI",
                text = "Lass uns deinen Fehler gezielt üben! Du hast gesagt: \"${mistake.userSaid}\". Korrekt heißt es: \"${mistake.correctVersion}\".\n\nErklärung: ${mistake.explanation}\n\nVersuche jetzt, einen ähnlichen Satz mit dieser Regel zu bilden!",
                translation = "Let's target and practice this mistake! Try to formulate a sentence applying the corrected rule."
            )
        )
        _currentTab.value = MainTab.SPEAK
    }

    fun setConversationMode(mode: ConversationMode) {
        _conversationMode.value = mode
        _activeScenario.value = null
        val greeting = when (mode) {
            ConversationMode.FREE_TALK -> "Willkommen im Free Talk Modus! Worüber möchtest du heute sprechen?"
            ConversationMode.TEACHER -> "Ich bin dein AI Lehrer. Welches Grammatik- oder Vokabelthema wollen wir vertiefen?"
            ConversationMode.ROLEPLAY -> "Wähle eines der 20 Alltagsszenarien oder lass uns eine freie Alltagssituation simulieren!"
            ConversationMode.DEBATE -> "Debatten-Modus: Sollte Homeoffice gesetzlich garantiert werden? Wie ist deine Meinung?"
            ConversationMode.INTERVIEW -> "Guten Tag! Willkommen zum Vorstellungsgespräch. Bitte stellen Sie sich kurz vor."
            ConversationMode.STORY -> "Es war ein regnerischer Abend in Berlin... Wie geht die Geschichte weiter?"
            ConversationMode.PRONUNCIATION_COACH -> "Aussprache-Coach bereit. Sprich mir nach und achte auf Vokale und Satzmelodie!"
            ConversationMode.EXAM_SPEAKING -> "Goethe B1 Sprechen Teil 2: Präsentieren Sie ein Thema Ihrer Wahl."
        }
        _chatMessages.value = listOf(ChatMessage(sender = "AI", text = greeting))
    }

    fun toggleHandsFree() {
        _handsFreeMode.value = !_handsFreeMode.value
    }

    fun sendUserMessage(userInput: String) {
        if (userInput.isBlank()) return
        val currentProfile = userProfile.value ?: SeedData.initialUser
        val userMsg = ChatMessage(sender = "USER", text = userInput)
        val updated = _chatMessages.value + userMsg
        _chatMessages.value = updated

        _isTutorThinking.value = true

        viewModelScope.launch {
            val historyPairs = updated.map { it.sender to it.text }
            val analysis = tutorOrchestrator.getTutorResponse(
                userText = userInput,
                personality = currentProfile.tutorPersonality,
                correctionLevel = currentProfile.correctionLevel,
                userLevel = currentProfile.currentLevel,
                mode = _conversationMode.value,
                scenarioContext = _activeScenario.value?.title,
                history = historyPairs
            )

            _isTutorThinking.value = false
            _lastTutorAnalysis.value = analysis

            val aiMsg = ChatMessage(
                sender = "AI",
                text = analysis.responseText,
                translation = analysis.translation,
                grammarFeedback = analysis.grammarExplanation,
                naturalAlternative = analysis.naturalAlternative
            )

            _chatMessages.value = _chatMessages.value + aiMsg

            // Auto-speak response using Android TTS
            voiceManager.speak(analysis.responseText, currentProfile.voiceSpeed)

            // If an error was diagnosed, save it in repository
            if (analysis.hasCorrection && analysis.correctedUtterance != null) {
                repository.recordMistake(
                    userSaid = userInput,
                    correct = analysis.correctedUtterance,
                    explanation = analysis.grammarExplanation ?: "Grammar adjustment",
                    category = analysis.targetedGrammarCategory ?: "German Grammar",
                    cefr = currentProfile.currentLevel
                )
            }

            // Reward XP for active conversation
            repository.updateUser(
                currentProfile.copy(
                    totalXp = currentProfile.totalXp + 15,
                    todayMinutesLearned = currentProfile.todayMinutesLearned + 1
                )
            )
        }
    }

    fun speakGermanText(text: String, speedMultiplier: Float = 1.0f) {
        val userSpeed = userProfile.value?.voiceSpeed ?: 1.0f
        voiceManager.speak(text, userSpeed * speedMultiplier)
    }

    fun evaluatePlacementTest(correctAnswersCount: Int, totalQuestions: Int) {
        val percentage = (correctAnswersCount * 100) / totalQuestions
        val result = LocalSmartTutorEngine.evaluatePlacementAnswers(
            grammarScore = percentage,
            vocabScore = percentage + 5,
            readingScore = percentage + 10,
            listeningScore = percentage - 5,
            speakingScore = percentage,
            writingScore = percentage
        )
        _placementResult.value = result

        viewModelScope.launch {
            repository.updatePlacementLevel(
                level = result.estimatedLevel,
                confidence = result.confidenceScore,
                speaking = result.speakingScore,
                listening = result.listeningScore,
                grammar = result.grammarScore,
                vocab = result.vocabularyScore,
                writing = result.writingScore
            )
        }
    }

    fun evaluateUserShadowing(userSpokenText: String) {
        val currentTask = shadowingTasks[_currentShadowingIndex.value]
        val result = LocalSmartTutorEngine.evaluateShadowing(currentTask.germanSentence, userSpokenText)
        _shadowingReport.value = result

        viewModelScope.launch {
            val currentProfile = userProfile.value ?: SeedData.initialUser
            repository.updateUser(currentProfile.copy(totalXp = currentProfile.totalXp + 20))
        }
    }

    fun nextShadowingTask() {
        if (_currentShadowingIndex.value < shadowingTasks.size - 1) {
            _currentShadowingIndex.value++
            _shadowingReport.value = null
        }
    }

    fun evaluateWriting(text: String, taskType: String) {
        _isEvaluatingWriting.value = true
        val level = userProfile.value?.currentLevel ?: "A2"
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            val report = LocalSmartTutorEngine.evaluateWriting(text, taskType, level)
            _writingReport.value = report
            _isEvaluatingWriting.value = false
            val currentProfile = userProfile.value ?: SeedData.initialUser
            repository.updateUser(currentProfile.copy(totalXp = currentProfile.totalXp + 35))
        }
    }

    fun updatePersonality(personality: TutorPersonality) {
        viewModelScope.launch {
            repository.updatePersonality(personality)
        }
    }

    fun updateCorrectionLevel(level: CorrectionLevel) {
        viewModelScope.launch {
            repository.updateCorrectionLevel(level)
        }
    }

    fun updateVoiceSpeed(speed: Float) {
        viewModelScope.launch {
            repository.updateVoiceSpeed(speed)
        }
    }

    fun completeLesson(lessonId: String) {
        viewModelScope.launch {
            repository.completeLesson(lessonId)
        }
    }

    fun resolveMistake(id: Int) {
        viewModelScope.launch {
            repository.resolveMistake(id)
        }
    }

    fun updateWordMastery(wordId: String, delta: Int) {
        viewModelScope.launch {
            val word = allVocabulary.value.find { it.id == wordId }
            if (word != null) {
                val newScore = (word.masteryScore + delta).coerceIn(0, 100)
                repository.updateWordMastery(wordId, newScore)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
