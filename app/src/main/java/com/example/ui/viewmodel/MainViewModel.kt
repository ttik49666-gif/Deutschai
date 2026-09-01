package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AdaptiveCurriculumPlan
import com.example.ai.AdaptiveLearningEngine
import com.example.ai.DiagnosisReport
import com.example.ai.PlacementEvaluationResult
import com.example.ai.RoleplayEngine
import com.example.ai.RoleplayTurnResult
import com.example.ai.ShadowingAnalysis
import com.example.ai.TutorAnalysis
import com.example.ai.TutorOrchestrator
import com.example.ai.VoiceManager
import com.example.ai.WritingEvaluationReport
import com.example.ai.WritingEvaluator
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
import com.example.data.model.TargetDialect
import com.example.data.model.TutorPersonality
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import com.example.data.repository.DeutschAIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, LEARN, SPEAK, PROGRESS, PROFILE
}

enum class LearnLabMode {
    ZERO_BASICS, CURRICULUM, VOCABULARY, SRS_REVIEW, GRAMMAR, LISTENING, SHADOWING, WRITING, EXAM_CENTER
}

enum class PlacementQuestionType {
    MULTIPLE_CHOICE,
    LISTENING_COMPREHENSION,
    READING_PASSAGE,
    SPEAKING_PROMPT,
    WRITING_PROMPT
}

data class PlacementQuestion(
    val id: Int,
    val skill: String, // Grammar, Vocabulary, Reading, Listening, Speaking, Writing
    val questionType: PlacementQuestionType,
    val cefr: String,
    val prompt: String,
    val promptAr: String,
    val contextSnippet: String? = null,
    val audioSentence: String? = null,
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val explanation: String = "",
    val explanationAr: String = ""
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DeutschAIRepository
    private val tutorOrchestrator = TutorOrchestrator()
    private val roleplayEngine = RoleplayEngine(tutorOrchestrator)
    private val writingEvaluator = WritingEvaluator(tutorOrchestrator)
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

    val dueVocabulary: StateFlow<List<VocabularyItem>> = repository.dueVocabulary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weakVocabulary: StateFlow<List<VocabularyItem>> = repository.weakVocabulary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGrammarTopics: StateFlow<List<GrammarTopic>> = repository.allGrammarTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialGrammarTopics)

    val weakGrammarTopics: StateFlow<List<GrammarTopic>> = repository.weakGrammarTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMistakes: StateFlow<List<MistakeItem>> = repository.allMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialMistakes)

    val unresolvedMistakes: StateFlow<List<MistakeItem>> = repository.unresolvedMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allScenarios: StateFlow<List<RoleplayScenario>> = repository.allScenarios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeedData.initialScenarios)

    // Dynamic Adaptive Diagnosis & Daily Plan
    val adaptivePlan: StateFlow<AdaptiveCurriculumPlan> = combine(
        userProfile,
        allMistakes,
        allVocabulary,
        allGrammarTopics
    ) { user, mistakes, vocab, grammar ->
        val effectiveUser = user ?: SeedData.initialUser
        val diagnosis = AdaptiveLearningEngine.diagnoseCurriculum(
            user = effectiveUser,
            mistakes = mistakes,
            vocabList = vocab,
            grammarList = grammar
        )
        AdaptiveLearningEngine.generateDailyPlan(effectiveUser, diagnosis)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AdaptiveCurriculumPlan(
            recommendedFocus = "Dativ Cases & Satzbau",
            recommendedFocusAr = "حالة المجرور (Dativ) وبناء الجمل الألمانية",
            dueSrsCount = 3,
            weaknessInsights = listOf("Achte auf Dativ-Objekte"),
            weaknessInsightsAr = listOf("ركز على حالة المجرور مع الأفعال"),
            priorityLessons = listOf("Der Dativ: dem, der, den")
        )
    )

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

    private val _completedMilestones = MutableStateFlow<Set<Int>>(emptySet())
    val completedMilestones: StateFlow<Set<Int>> = _completedMilestones.asStateFlow()

    private val _activeMistakePractice = MutableStateFlow<MistakeItem?>(null)
    val activeMistakePractice: StateFlow<MistakeItem?> = _activeMistakePractice.asStateFlow()

    // Conversation State
    private val _conversationMode = MutableStateFlow(ConversationMode.FREE_TALK)
    val conversationMode: StateFlow<ConversationMode> = _conversationMode.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "AI",
                text = "Guten Tag! Ich bin DeutschAI, dein persönlicher Deutschlehrer. Worüber möchtest du heute sprechen oder was möchtest du üben?",
                translation = "طاب يومك! أنا DeutschAI، معلمك الشخصي للغة الألمانية. عن ماذا ترغب في التحدث أو التدرب اليوم؟"
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
        ShadowingTask("sh1", "A1", "Guten Morgen! Ich hätte gern einen Kaffee mit Milch.", "Good morning! I would like a coffee with milk.", "صباح الخير! أود الحصول على قهوة بالحليب.", "Betone 'Kaffee' auf der ersten Silbe und sprich das 'ch' in 'ich' weich aus.", "انتبه لنطق حرف ch الناعم في كلمة ich.", 0.85f),
        ShadowingTask("sh2", "A2", "Ich fahre jeden Morgen mit der U-Bahn zur Arbeit.", "I ride the subway to work every morning.", "أركب قطار الأنفاق كل صباح إلى العمل.", "Betone 'U-Bahn' auf der ersten Silbe.", "ركز على نطق حرف U الصريح والمشدد في U-Bahn.", 0.85f),
        ShadowingTask("sh3", "A2", "Könnten Sie mir bitte sagen, wo der Hauptbahnhof ist?", "Could you please tell me where the central station is?", "هل يمكنك إخباري من فضلك أين تقع محطة القطار المركزية؟", "Weiches 'ch' in 'Ich' und klares 'h' in 'Hauptbahnhof'.", "نطق مخارج الحروف الألمانية بدقة ونبرة الاستفهام اللبقة.", 0.85f),
        ShadowingTask("sh4", "B1", "Weil das Wetter so schön war, haben wir einen Ausflug gemacht.", "Because the weather was so nice, we went on an excursion.", "نظراً لأن الطقس كان جميلاً، قمنا بنزهة.", "Achte auf die Satzmelodie und das Verb 'gemacht' am Satzende.", "لاحظ موضع الفعل في نهاية الجملة بعد أداة weil.", 0.9f),
        ShadowingTask("sh5", "B2", "Es steht außer Frage, dass wir eine nachhaltige Lösung finden müssen.", "It is out of the question that we must find a sustainable solution.", "لا شك في أنه يجب علينا إيجاد حل مستدام.", "Präzise Kadenz auf 'außer Frage' und 'nachhaltige'.", "نبرة واثقة وواضحة للمصطلحات المتقدمة.", 0.95f)
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

    // Multi-Skill Placement Test Questions
    val placementQuestions = listOf(
        PlacementQuestion(
            id = 1,
            skill = "Grammar",
            questionType = PlacementQuestionType.MULTIPLE_CHOICE,
            cefr = "A1",
            prompt = "Wählen Sie den richtigen Artikel: ___ Tisch ist neu.",
            promptAr = "اختر أداة التعريف الصحيحة للاسم المذكر: ___ Tisch ist neu.",
            options = listOf("Der", "Die", "Das", "Den"),
            correctIndex = 0,
            explanation = "Nomen 'Tisch' ist maskulin im Nominativ: 'Der Tisch'.",
            explanationAr = "كلمة Tisch اسم مذكر في حالة الرفع، وتأخذ أداة التعريف Der."
        ),
        PlacementQuestion(
            id = 2,
            skill = "Grammar",
            questionType = PlacementQuestionType.MULTIPLE_CHOICE,
            cefr = "A1",
            prompt = "Ich kaufe ___ Apfel im Supermarkt.",
            promptAr = "اختر الأداة في حالة النصب (Akkusativ):",
            options = listOf("der", "den", "dem", "das"),
            correctIndex = 1,
            explanation = "'Apfel' ist maskulin und steht hier als direktes Objekt im Akkusativ: 'den Apfel'.",
            explanationAr = "كلمة Apfel مذكر وتأتي هنا مفعولاً به مباشراً (Akkusativ)، فتتحول der إلى den."
        ),
        PlacementQuestion(
            id = 3,
            skill = "Grammar",
            questionType = PlacementQuestionType.MULTIPLE_CHOICE,
            cefr = "A2",
            prompt = "Gestern ___ wir mit dem Zug nach Hamburg gefahren.",
            promptAr = "اختر الفعل المساعد المناسب للماضي المركب مع فعل الحركة (fahren):",
            options = listOf("haben", "sind", "wurden", "hatten"),
            correctIndex = 1,
            explanation = "'fahren' ist ein Fortbewegungsverb und bildet das Perfekt mit 'sein': 'wir sind gefahren'.",
            explanationAr = "أفعال الانتقال والحركة تأخذ الفعل المساعد sein في الماضي: wir sind gefahren."
        ),
        PlacementQuestion(
            id = 4,
            skill = "Grammar",
            questionType = PlacementQuestionType.MULTIPLE_CHOICE,
            cefr = "A2",
            prompt = "Der Arzt hilft ___ kranken Patienten.",
            promptAr = "اختر أداة المجرور المناسبة مع الفعل helfen:",
            options = listOf("der", "dem", "den", "das"),
            correctIndex = 1,
            explanation = "Das Verb 'helfen' verlangt immer Dativ: 'dem Patienten'.",
            explanationAr = "الفعل helfen يتطلب حالة المجرور Dativ، والمذكر المفرد يصبح dem."
        ),
        PlacementQuestion(
            id = 5,
            skill = "Vocabulary",
            questionType = PlacementQuestionType.MULTIPLE_CHOICE,
            cefr = "B1",
            prompt = "Welches Wort passt zur Nomen-Verb-Verbindung? Ich habe eine wichtige ___ getroffen.",
            promptAr = "اختر الكلمة المناسبة للمتلازمة اللفظية 'اتخاذ قرار':",
            options = listOf("Erfahrung", "Entscheidung", "Gelegenheit", "Verzögerung"),
            correctIndex = 1,
            explanation = "'Eine Entscheidung treffen' bedeutet einen Entschluss fassen.",
            explanationAr = "المتلازمة اللفظية الصحيحة في الألمانية هي: eine Entscheidung treffen (اتخاذ قرار)."
        ),
        PlacementQuestion(
            id = 6,
            skill = "Reading",
            questionType = PlacementQuestionType.READING_PASSAGE,
            cefr = "B1",
            prompt = "Was bedeutet die Bahn-Mitteilung?",
            promptAr = "ما معنى إشعار محطة القطار؟",
            contextSnippet = "Mitteilung der Deutschen Bahn: 'Wegen kurzfristiger Gleisarbeiten entfallen heute alle Regionalzüge zwischen Frankfurt und Wiesbaden. Bitte nutzen Sie die S-Bahn-Linie 1.'",
            options = listOf("Die Züge fahren wie gewohnt.", "Die Züge fallen aus und Passagiere sollen die S-Bahn nutzen.", "Es gibt Rabatte auf Fahrkarten.", "Der Hauptbahnhof ist geschlossen."),
            correctIndex = 1,
            explanation = "'entfallen' bedeutet im Bahnjargon 'ausfallen' (nicht fahren).",
            explanationAr = "كلمة entfallen تعني إلغاء الرحلات، والمطلوب استخدام قطار S-Bahn البديل."
        ),
        PlacementQuestion(
            id = 7,
            skill = "Listening",
            questionType = PlacementQuestionType.LISTENING_COMPREHENSION,
            cefr = "A2",
            prompt = "Höre dir die Durchsage an: Um wie viel Uhr fährt der Zug ab?",
            promptAr = "استمع إلى الإعلان الصوتي: في أي ساعة يغادر القطار؟",
            audioSentence = "Achtung an Gleis vier: Der Intercity nach Berlin Hauptbahnhof fährt heute um vierzehn Uhr dreißig ab.",
            options = listOf("14:15 Uhr", "14:30 Uhr", "15:30 Uhr", "16:00 Uhr"),
            correctIndex = 1,
            explanation = "In der Durchsage heißt es: 'vierzehn Uhr dreißig' (14:30 Uhr).",
            explanationAr = "الإعلان يذكر بوضوح: vierzehn Uhr dreißig (الساعة 14:30)."
        ),
        PlacementQuestion(
            id = 8,
            skill = "Writing",
            questionType = PlacementQuestionType.WRITING_PROMPT,
            cefr = "A2",
            prompt = "Schreibe 1-2 Sätze auf Deutsch: Was machst du am Wochenende gern?",
            promptAr = "اكتب جملة أو جملتين بالألمانية: ماذا تحب أن تفعل في عطلة نهاية الأسبوع؟",
            options = listOf("Beispiel: Am Wochenende spiele ich Fußball mit meinen Freunden.", "Beispiel: Ich gehe gern ins Kino und koche mit meiner Familie.", "Beispiel: Ich bleibe zu Hause und lese ein deutsches Buch."),
            correctIndex = 0,
            explanation = "Bewertet Satzbau, Verbstellung (Position 2) und Wortschatz.",
            explanationAr = "تقييم تكوين الجمل وموضع الفعل واستخدام المفردات."
        ),
        PlacementQuestion(
            id = 9,
            skill = "Speaking",
            questionType = PlacementQuestionType.SPEAKING_PROMPT,
            cefr = "A1",
            prompt = "Wie begrüßt man jemanden höflich am Morgen und fragt nach dem Namen?",
            promptAr = "كيف تحيي شخصاً بلباقة في الصباح وتسأله عن اسمه؟",
            options = listOf("Guten Morgen! Wie heißen Sie?", "Tschüss! Wo wohnst du?", "Gute Nacht! Wer ist das?", "Danke schön! Wie alt bist du?"),
            correctIndex = 0,
            explanation = "'Guten Morgen' ist die korrekte Morgengrüßformel und 'Wie heißen Sie?' die höfliche Frage.",
            explanationAr = "'Guten Morgen' هي التحية الصباحية و 'Wie heißen Sie?' هي صيغة السؤال المهذبة عن الاسم."
        ),
        PlacementQuestion(
            id = 10,
            skill = "Grammar",
            questionType = PlacementQuestionType.MULTIPLE_CHOICE,
            cefr = "B1",
            prompt = "Er geht zur Arbeit, ___ er krank ist.",
            promptAr = "اختر حرف العطف المناسب للجملة التبعية (يقصد: على الرغم من أنه مريض):",
            options = listOf("obwohl", "weil", "deshalb", "denn"),
            correctIndex = 0,
            explanation = "'obwohl' drückt einen Gegengrund (Konzessivsatz) aus und schickt das Verb ans Ende.",
            explanationAr = "تُستخدم أداة 'obwohl' للتعبير عن التناقض (على الرغم من)، وتضع الفعل في نهاية الجملة."
        ),
        PlacementQuestion(
            id = 11,
            skill = "Vocabulary",
            questionType = PlacementQuestionType.MULTIPLE_CHOICE,
            cefr = "B2",
            prompt = "Im Vorstellungsgespräch betonte sie ihre hohe ___ im Bereich Projektmanagement.",
            promptAr = "اختر الكلمة المناسبة لسياق العمل والمقابلات الوظيفية:",
            options = listOf("Kompetenz", "Verspätung", "Verletzung", "Abrechnung"),
            correctIndex = 0,
            explanation = "'Kompetenz' bedeutet Fachwissen und Fähigkeit im professionellen Kontext.",
            explanationAr = "كلمة 'Kompetenz' تعني الكفاءة والخبرة التخصصية في سياق العمل."
        ),
        PlacementQuestion(
            id = 12,
            skill = "Listening",
            questionType = PlacementQuestionType.LISTENING_COMPREHENSION,
            cefr = "B1",
            prompt = "Höre dir die Arztdurchsage an: Welche Anweisung gibt die Sprechstundenhilfe?",
            promptAr = "استمع إلى الإشعار الطبي: ما هي التعليمات المعطاة للمريض؟",
            audioSentence = "Bitte nehmen Sie diese Tropfen dreimal täglich vor den Mahlzeiten mit einem Glas Wasser ein.",
            options = listOf("Tropfen 3x täglich vor dem Essen einnehmen", "Tabletten nur abends nehmen", "Sofort ins Krankenhaus fahren", "Kein Wasser trinken"),
            correctIndex = 0,
            explanation = "'dreimal täglich vor den Mahlzeiten' bedeutet 3 Mal pro Tag vor dem Essen.",
            explanationAr = "التعليمات واضحة: أخذ القطرات ثلاث مرات يومياً قبل الوجبات."
        )
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

    fun onboardAsBeginner(
        name: String,
        goal: String,
        minutes: Int,
        personality: TutorPersonality,
        dialect: TargetDialect
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: SeedData.initialUser
            val updated = current.copy(
                name = if (name.isNotBlank()) name else current.name,
                currentLevel = "A1.1",
                estimatedScorePercent = 50,
                confidencePercent = 70,
                primaryGoal = goal,
                dailyGoalMinutes = minutes,
                tutorPersonality = personality,
                targetDialect = dialect,
                isOnboarded = true
            )
            repository.updateUser(updated)
            _showOnboarding.value = false
            _activeLabMode.value = LearnLabMode.ZERO_BASICS
            _currentTab.value = MainTab.LEARN
        }
    }

    fun onboardWithPriorKnowledge(
        name: String,
        goal: String,
        minutes: Int,
        personality: TutorPersonality,
        dialect: TargetDialect
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: SeedData.initialUser
            val updated = current.copy(
                name = if (name.isNotBlank()) name else current.name,
                primaryGoal = goal,
                dailyGoalMinutes = minutes,
                tutorPersonality = personality,
                targetDialect = dialect,
                isOnboarded = true
            )
            repository.updateUser(updated)
            _showOnboarding.value = false
            openPlacementTest()
        }
    }

    fun startZeroBasics() {
        _activeLabMode.value = LearnLabMode.ZERO_BASICS
        _currentTab.value = MainTab.LEARN
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
        _completedMilestones.value = emptySet()
        _conversationMode.value = ConversationMode.ROLEPLAY
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "AI",
                text = scenario.initialMessage,
                translation = "بدء سيناريو: ${scenario.titleAr.ifBlank { scenario.title }}. الهدف: ${scenario.goalAr.ifBlank { scenario.goal }}"
            )
        )
        _currentTab.value = MainTab.SPEAK
    }

    fun startMistakePractice(mistake: MistakeItem) {
        _activeMistakePractice.value = mistake
        _conversationMode.value = ConversationMode.TEACHER
        val expAr = mistake.explanationAr.ifBlank { mistake.explanation }
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "AI",
                text = "Lass uns deinen Fehler gezielt üben! Du hast gesagt: \"${mistake.userSaid}\".\nKorrekt: \"${mistake.correctVersion}\".\n\nErklärung: ${mistake.explanation}\n\nVersuche jetzt, einen ähnlichen Satz zu bilden!",
                translation = "دعنا نتدرب على تصحيح هذا الخطأ! قلت سابقاً: \"${mistake.userSaid}\" والصحيح: \"${mistake.correctVersion}\".\nالتوضيح: $expAr\nحاول الآن تكوين جملة مماثلة بتطبيق القاعدة!"
            )
        )
        _currentTab.value = MainTab.SPEAK
    }

    fun setConversationMode(mode: ConversationMode) {
        _conversationMode.value = mode
        _activeScenario.value = null
        val (greetingDe, greetingAr) = when (mode) {
            ConversationMode.FREE_TALK -> Pair(
                "Willkommen im Free Talk Modus! Worüber möchtest du heute sprechen?",
                "مرحباً بك في وضع المحادثة الحرة! عن ماذا ترغب في التحدث اليوم؟"
            )
            ConversationMode.TEACHER -> Pair(
                "Ich bin dein AI Lehrer. Welches Grammatik- oder Vokabelthema wollen wir vertiefen?",
                "أنا معلمك للغة الألمانية. ما هو موضوع القواعد أو المفردات الذي تود مراجعته؟"
            )
            ConversationMode.ROLEPLAY -> Pair(
                "Wähle eines der Alltagsszenarien oder lass uns eine freie Situation simulieren!",
                "اختر أحد السيناريوهات اليومية أو دعنا نحاكي موقفاً واقعياً!"
            )
            ConversationMode.DEBATE -> Pair(
                "Debatten-Modus: Sollte Homeoffice gesetzlich garantiert werden? Wie ist deine Meinung?",
                "وضع النقاش والمناظرة: هل يجب ضمان العمل عن بعد قانونياً؟ ما هو رأيك؟"
            )
            ConversationMode.INTERVIEW -> Pair(
                "Guten Tag! Willkommen zum Vorstellungsgespräch. Bitte stellen Sie sich kurz vor.",
                "طاب يومكم! مرحباً بكم في مقابلة العمل. تفضلوا بالتعريف عن أنفسكم بإيجاز."
            )
            ConversationMode.STORY -> Pair(
                "Es war ein regnerischer Abend in Berlin... Wie geht die Geschichte weiter?",
                "كانت أمسية ماطرة في برلين... كيف تستمر القصة برأيك؟"
            )
            ConversationMode.PRONUNCIATION_COACH -> Pair(
                "Aussprache-Coach bereit. Sprich mir nach und achte auf Vokale und Umlaute!",
                "مدرب النطق جاهز. ردد معي وانتبه للحركات المُمَالة ومخارج الحروف!"
            )
            ConversationMode.EXAM_SPEAKING -> Pair(
                "Goethe B1 Sprechen Teil 2: Präsentieren Sie ein Thema Ihrer Wahl.",
                "امتحان جوته للتحدث B1: تفضل بتقديم موضوع من اختيارك."
            )
        }
        _chatMessages.value = listOf(ChatMessage(sender = "AI", text = greetingDe, translation = greetingAr))
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
            val scenario = _activeScenario.value

            val analysis: TutorAnalysis
            if (scenario != null) {
                val roleplayResult = roleplayEngine.processRoleplayTurn(
                    userUtterance = userInput,
                    scenario = scenario,
                    userProfile = currentProfile,
                    completedMilestoneIndices = _completedMilestones.value,
                    history = historyPairs
                )
                analysis = roleplayResult.aiResponse
                val newIndices = _completedMilestones.value.toMutableSet()
                newIndices.add(roleplayResult.activeMilestoneIndex)
                _completedMilestones.value = newIndices
            } else {
                analysis = tutorOrchestrator.processUserUtterance(
                    userText = userInput,
                    userProfile = currentProfile,
                    mode = _conversationMode.value,
                    scenarioContext = _activeScenario.value?.title,
                    history = historyPairs
                )
            }

            _isTutorThinking.value = false
            _lastTutorAnalysis.value = analysis

            val aiMsg = ChatMessage(
                sender = "AI",
                text = analysis.responseText,
                translation = analysis.translation,
                translationDetail = analysis.arabicDetail,
                grammarFeedback = analysis.grammarExplanation,
                grammarFeedbackAr = analysis.grammarExplanationAr,
                naturalAlternative = analysis.naturalAlternative
            )

            _chatMessages.value = _chatMessages.value + aiMsg

            // Auto-speak response
            voiceManager.speak(analysis.responseText, currentProfile.voiceSpeed)

            // If an error was diagnosed, save it in repository
            if (analysis.hasCorrection && analysis.correctedUtterance != null) {
                repository.recordMistake(
                    userSaid = userInput,
                    correct = analysis.correctedUtterance,
                    explanation = analysis.grammarExplanation ?: "Grammatik-Korrektur",
                    explanationAr = analysis.grammarExplanationAr ?: "تصحيح نحوي للقاعدة الألمانية",
                    category = analysis.targetedGrammarCategory ?: "German Grammar",
                    cefr = currentProfile.currentLevel
                )
            }

            // Reward XP
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

    /**
     * Real Multi-Skill CEFR Assessment Scoring (Grammar, Vocab, Reading, Listening, Speaking, Writing)
     */
    fun evaluateMultiSkillPlacement(
        grammarCorrect: Int,
        grammarTotal: Int,
        vocabCorrect: Int,
        vocabTotal: Int,
        readingCorrect: Int,
        readingTotal: Int,
        listeningCorrect: Int,
        listeningTotal: Int,
        speakingScore: Int,
        writingScore: Int
    ) {
        val gScore = if (grammarTotal > 0) (grammarCorrect * 100) / grammarTotal else 70
        val vScore = if (vocabTotal > 0) (vocabCorrect * 100) / vocabTotal else 75
        val rScore = if (readingTotal > 0) (readingCorrect * 100) / readingTotal else 80
        val lScore = if (listeningTotal > 0) (listeningCorrect * 100) / listeningTotal else 75

        viewModelScope.launch {
            val result = tutorOrchestrator.evaluatePlacementTest(
                grammarScore = gScore,
                vocabScore = vScore,
                readingScore = rScore,
                listeningScore = lScore,
                speakingScore = speakingScore,
                writingScore = writingScore
            )
            _placementResult.value = result

            repository.updatePlacementLevel(
                level = result.estimatedLevel,
                confidence = result.confidenceScore,
                speaking = result.speakingScore,
                listening = result.listeningScore,
                grammar = result.grammarScore,
                vocab = result.vocabularyScore,
                reading = result.readingScore,
                writing = result.writingScore
            )
        }
    }

    fun evaluateUserShadowing(userSpokenText: String) {
        val currentTask = shadowingTasks[_currentShadowingIndex.value]
        viewModelScope.launch {
            val result = tutorOrchestrator.evaluateShadowingAudio(currentTask.germanSentence, userSpokenText)
            _shadowingReport.value = result
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
            val report = writingEvaluator.evaluateEssay(text, taskType, level)
            _writingReport.value = report
            _isEvaluatingWriting.value = false
            val currentProfile = userProfile.value ?: SeedData.initialUser
            repository.updateUser(currentProfile.copy(totalXp = currentProfile.totalXp + 35))
        }
    }

    /**
     * Spaced Repetition (SM-2) Flashcard Review Action
     */
    fun reviewVocabularySrs(item: VocabularyItem, quality: Int) {
        viewModelScope.launch {
            repository.reviewVocabularyItem(item, quality)
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

    fun updateDialect(dialect: TargetDialect) {
        viewModelScope.launch {
            repository.updateDialect(dialect)
        }
    }

    fun updateTargetDialect(dialect: TargetDialect) {
        updateDialect(dialect)
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
