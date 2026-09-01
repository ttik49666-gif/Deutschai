package com.example.data.local

import com.example.data.model.CorrectionLevel
import com.example.data.model.GrammarTopic
import com.example.data.model.Lesson
import com.example.data.model.MistakeItem
import com.example.data.model.RoleplayScenario
import com.example.data.model.TutorPersonality
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem

object SeedData {
    val initialUser = UserProfile(
        id = "primary_user",
        name = "Alex",
        currentLevel = "A2.2",
        estimatedScorePercent = 64,
        confidencePercent = 87,
        dailyGoalMinutes = 15,
        todayMinutesLearned = 12,
        streakDays = 14,
        totalXp = 2850,
        tutorPersonality = TutorPersonality.FRIENDLY,
        correctionLevel = CorrectionLevel.BALANCED,
        voiceSpeed = 1.0f,
        handsFreeEnabled = false,
        isOnboarded = true,
        primaryGoal = "Speak German confidently",
        weakSkillsSummary = "Dativ Cases & Nebensatz Word Order",
        strongSkillsSummary = "Daily Vocabulary & Reading Comprehension"
    )

    val initialLessons = listOf(
        // A1 Lessons
        Lesson("l_a1_1", "A1", 1, "Basics & Greetings", "Hallo & Auf Wiedersehen", "Learn formal & informal German greetings, numbers, and personal pronouns.", "Speaking", 100, isUnlocked = true, isCompleted = true, estimatedMinutes = 8, xpReward = 50),
        Lesson("l_a1_2", "A1", 2, "Articles & Gender", "Der, Die, Das & Plural", "Master the three German grammatical genders and definite/indefinite articles.", "Grammar", 100, isUnlocked = true, isCompleted = true, estimatedMinutes = 10, xpReward = 50),
        Lesson("l_a1_3", "A1", 3, "Direct Objects", "Der Akkusativ im Alltag", "Learn the accusative case: den Apfel, die Pizza, das Brot.", "Grammar", 95, isUnlocked = true, isCompleted = true, estimatedMinutes = 10, xpReward = 50),
        Lesson("l_a1_4", "A1", 4, "Food & Cafe", "Im Café bestellen", "Roleplay ordering coffee, water, and pastries in German cafes.", "Speaking", 100, isUnlocked = true, isCompleted = true, estimatedMinutes = 12, xpReward = 60),

        // A2 Lessons
        Lesson("l_a2_1", "A2", 5, "Past Tense", "Perfekt: haben oder sein?", "Master forming conversational past tense. When to use haben vs sein.", "Grammar", 72, isUnlocked = true, isCompleted = true, estimatedMinutes = 12, xpReward = 60),
        Lesson("l_a2_2", "A2", 6, "Indirect Objects", "Der Dativ: dem, der, den", "Understand Dativ cases with verbs like helfen, danken, gefallen and prepositions.", "Grammar", 43, isUnlocked = true, isCompleted = false, estimatedMinutes = 15, xpReward = 70),
        Lesson("l_a2_3", "A2", 7, "Subordinate Clauses", "Satzbau mit weil & dass", "Master German word order: verb kick to the end in subordinate clauses.", "Grammar", 51, isUnlocked = true, isCompleted = false, estimatedMinutes = 12, xpReward = 65),
        Lesson("l_a2_4", "A2", 8, "City Directions", "Nach dem Weg fragen", "Ask for directions at the Bahnhof or Alexanderplatz in Berlin.", "Listening", 80, isUnlocked = true, isCompleted = true, estimatedMinutes = 10, xpReward = 50),
        Lesson("l_a2_5", "A2", 9, "Modal Verbs", "Können, Müssen, Wollen", "Express obligations, wishes, and abilities with modal verbs.", "Vocabulary", 85, isUnlocked = true, isCompleted = true, estimatedMinutes = 10, xpReward = 55),

        // B1 Lessons
        Lesson("l_b1_1", "B1", 10, "Hypotheticals", "Konjunktiv II (Wünsche & Höflichkeit)", "Polite requests: Ich hätte gern, Ich würde gern, Könnten Sie...", "Grammar", 40, isUnlocked = true, isCompleted = false, estimatedMinutes = 15, xpReward = 75),
        Lesson("l_b1_2", "B1", 11, "Two-Way Prepositions", "Wechselpräpositionen (Wohin vs Wo)", "Akkusativ for movement vs Dativ for static location (in, an, auf, unter).", "Grammar", 45, isUnlocked = true, isCompleted = false, estimatedMinutes = 15, xpReward = 75),
        Lesson("l_b1_3", "B1", 12, "Passive Voice", "Vorgangspassiv: Das Auto wird repariert", "Forming the passive voice with werden + Partizip II.", "Grammar", 30, isUnlocked = true, isCompleted = false, estimatedMinutes = 15, xpReward = 80),
        Lesson("l_b1_4", "B1", 13, "Formal Writing", "Offizielle E-Mails & Beschwerden", "Write formal German letters with appropriate opening & closing formulas.", "Writing", 50, isUnlocked = true, isCompleted = false, estimatedMinutes = 18, xpReward = 85),

        // B2 Lessons
        Lesson("l_b2_1", "B2", 14, "Business German", "Nomen-Verb-Verbindungen", "Advanced idiomatic structures: zur Verfügung stehen, in Betracht ziehen.", "Vocabulary", 20, isUnlocked = false, isCompleted = false, estimatedMinutes = 15, xpReward = 90),
        Lesson("l_b2_2", "B2", 15, "Complex Clauses", "Relativsätze mit Genitiv (dessen, deren)", "Sophisticated relative clauses with Genitive pronouns.", "Grammar", 15, isUnlocked = false, isCompleted = false, estimatedMinutes = 15, xpReward = 90),
        Lesson("l_b2_3", "B2", 16, "Debate & Argumentation", "Argumentieren & Überzeugen", "Express nuanced perspectives, rebuttals, and concessions.", "Speaking", 10, isUnlocked = false, isCompleted = false, estimatedMinutes = 20, xpReward = 100),

        // C1 Lessons
        Lesson("l_c1_1", "C1", 17, "Academic German", "Gehobene Sprache & Wissenschaftsdeutsch", "Academic discourse, Nominalstil, and subjective modal verb nuances.", "Grammar", 0, isUnlocked = false, isCompleted = false, estimatedMinutes = 20, xpReward = 120),
        Lesson("l_c1_2", "C1", 18, "Mastery Nuances", "Redewendungen & Feinheiten", "Deep cultural idioms and German proverbs in professional settings.", "Vocabulary", 0, isUnlocked = false, isCompleted = false, estimatedMinutes = 20, xpReward = 120)
    )

    val initialVocabulary = listOf(
        VocabularyItem("v1", "Bahnhof", "der", "die Bahnhöfe", "train station", "A1", "Ich treffe dich am Hauptbahnhof.", "I will meet you at the central station.", 90, 0, category = "Travel"),
        VocabularyItem("v2", "Entscheidung", "die", "die Entscheidungen", "decision", "A2", "Das war eine schwierige Entscheidung.", "That was a difficult decision.", 75, 1, category = "General"),
        VocabularyItem("v3", "Krankenhaus", "das", "die Krankenhäuser", "hospital", "A1", "Er arbeitet im städtischen Krankenhaus.", "He works in the municipal hospital.", 88, 0, category = "Health"),
        VocabularyItem("v4", "Termin", "der", "die Termine", "appointment / date", "A1", "Ich habe morgen einen Arzttermin.", "I have a doctor appointment tomorrow.", 95, 0, category = "Daily Life"),
        VocabularyItem("v5", "Erfahrung", "die", "die Erfahrungen", "experience", "A2", "Ich habe viel Erfahrung im Projektmanagement.", "I have a lot of experience in project management.", 82, 0, category = "Career"),
        VocabularyItem("v6", "Gelegenheit", "die", "die Gelegenheiten", "opportunity", "B1", "Das ist eine hervorragende Gelegenheit.", "That is an outstanding opportunity.", 60, 2, category = "Career"),
        VocabularyItem("v7", "Unterschied", "der", "die Unterschiede", "difference", "A2", "Was ist der Unterschied zwischen diesen Wörtern?", "What is the difference between these words?", 70, 1, category = "Language"),
        VocabularyItem("v8", "Ergebnis", "das", "die Ergebnisse", "result / outcome", "A2", "Wir warten noch auf das Testergebnis.", "We are still waiting for the test result.", 65, 1, category = "General"),
        VocabularyItem("v9", "Bewerbung", "die", "die Bewerbungen", "job application", "A2", "Ich habe meine Bewerbung abgeschickt.", "I sent off my job application.", 80, 0, category = "Career"),
        VocabularyItem("v10", "Vorstellungsgespräch", "das", "die Vorstellungsgespräche", "job interview", "B1", "Das Vorstellungsgespräch lief sehr gut.", "The job interview went very well.", 55, 2, category = "Career"),
        VocabularyItem("v11", "Verzögerung", "die", "die Verzögerungen", "delay", "B1", "Der Zug hat 20 Minuten Verzögerung.", "The train has a 20-minute delay.", 68, 1, category = "Travel"),
        VocabularyItem("v12", "Ausbildung", "die", "die Ausbildungen", "apprenticeship / training", "A2", "Sie macht eine Ausbildung zur Fachinformatikerin.", "She is doing an apprenticeship in IT.", 85, 0, category = "Education"),
        VocabularyItem("v13", "Zusammenhang", "der", "die Zusammenhänge", "context / correlation", "B2", "In welchem Zusammenhang steht das?", "In what context does that stand?", 45, 3, category = "Academic"),
        VocabularyItem("v14", "Wohnung", "die", "die Wohnungen", "apartment / flat", "A1", "Die Wohnung hat einen großen Balkon.", "The apartment has a large balcony.", 98, 0, category = "Housing"),
        VocabularyItem("v15", "Sehenswürdigkeit", "die", "die Sehenswürdigkeiten", "sight / attraction", "A2", "Berlin hat viele berühmte Sehenswürdigkeiten.", "Berlin has many famous sights.", 70, 1, category = "Travel"),
        VocabularyItem("v16", "Verabredung", "die", "die Verabredungen", "appointment / date with friends", "A2", "Ich habe eine Verabredung mit Lisa.", "I have a date with Lisa.", 80, 0, category = "Social")
    )

    val initialGrammarTopics = listOf(
        GrammarTopic(
            id = "g_dativ",
            title = "Dativ Cases & Verbs",
            cefrLevel = "A2",
            category = "Cases",
            explanation = "Dativ is used for the indirect object (receiver of the action) and with specific verbs (helfen, danken, gefallen, gehören, antworten, vertrauen) and prepositions (aus, bei, mit, nach, seit, von, zu).",
            formulaRule = "der/das -> dem, die -> der, die (Plural) -> den (+n)",
            exampleRight = "Ich helfe dem Mann und der Frau.",
            exampleWrong = "Ich helfe den Mann und die Frau.",
            whyWrong = "'helfen' always demands Dativ case. Masculine 'der' becomes 'dem', feminine 'die' becomes 'der'.",
            masteryScore = 43,
            isWeakArea = true
        ),
        GrammarTopic(
            id = "g_word_order",
            title = "Nebensatz Word Order (weil, dass, wenn)",
            cefrLevel = "A2",
            category = "Word Order",
            explanation = "Subordinating conjunctions (weil, dass, wenn, ob, obwohl, da) kick the conjugated verb to the very end of the sub-clause.",
            formulaRule = "Hauptsatz (Verb Pos 2) + , weil + Subjekt + ... + KONJUGIERTES VERB AM ENDE.",
            exampleRight = "Ich lerne Deutsch, weil ich in Berlin arbeiten will.",
            exampleWrong = "Ich lerne Deutsch, weil ich will in Berlin arbeiten.",
            whyWrong = "In German, 'weil' sends the conjugated modal verb 'will' to the absolute end of the clause.",
            masteryScore = 51,
            isWeakArea = true
        ),
        GrammarTopic(
            id = "g_perfekt",
            title = "Perfekt: haben vs. sein",
            cefrLevel = "A2",
            category = "Tenses",
            explanation = "Use 'sein' with verbs of motion (gehen, fahren, fliegen, kommen) or change of state (aufwachen, sterben), plus 'sein' and 'bleiben'. Use 'haben' for almost everything else.",
            formulaRule = "Subjekt + konjugiertes haben/sein + ... + Partizip II (ge...t / ge...en).",
            exampleRight = "Ich bin gestern nach München gefahren.",
            exampleWrong = "Ich habe gestern nach München gefahren.",
            whyWrong = "'fahren' is a verb of movement indicating location change, so it requires 'sein' as auxiliary.",
            masteryScore = 72,
            isWeakArea = false
        ),
        GrammarTopic(
            id = "g_akkusativ",
            title = "Der Akkusativ (Direct Object)",
            cefrLevel = "A1",
            category = "Cases",
            explanation = "The accusative case is used for the direct recipient of an action. Only the masculine changes: der -> den, ein -> einen.",
            formulaRule = "der -> den | die -> die | das -> das | die -> die",
            exampleRight = "Ich trinke den Kaffee und esse das Brot.",
            exampleWrong = "Ich trinke der Kaffee.",
            whyWrong = "'der Kaffee' is the direct object of 'trinken', so it changes to 'den Kaffee'.",
            masteryScore = 87,
            isWeakArea = false
        ),
        GrammarTopic(
            id = "g_modal",
            title = "Modal Verbs in Present & Past",
            cefrLevel = "A2",
            category = "Verbs",
            explanation = "Modal verbs (können, müssen, wollen, dürfen, sollen, möchten) take position 2 in present tense, and the main infinitive verb goes to the end.",
            formulaRule = "Subjekt + Modalverb (Pos 2) + ... + Infinitiv (Ende).",
            exampleRight = "Wir müssen heute pünktlich ankommen.",
            exampleWrong = "Wir müssen ankommen heute pünktlich.",
            whyWrong = "The second verb (infinitive) must be placed at the final bracket of the sentence.",
            masteryScore = 85,
            isWeakArea = false
        ),
        GrammarTopic(
            id = "g_konjunktiv",
            title = "Konjunktiv II (Politeness & Wishes)",
            cefrLevel = "B1",
            category = "Mood",
            explanation = "Used for polite requests, hypothetical scenarios, and dreams. Most commonly with 'würde + Infinitiv' or 'hätte' / 'wäre'.",
            formulaRule = "Ich würde gern... / Ich hätte gern... / Könnten Sie mir bitte...?",
            exampleRight = "Ich hätte gern eine Tasse Kaffee, bitte.",
            exampleWrong = "Ich will Kaffee.",
            whyWrong = "While grammatically understood, 'Ich will Kaffee' sounds blunt in German; 'Ich hätte gern...' is the standard polite form.",
            masteryScore = 60,
            isWeakArea = false
        )
    )

    val initialMistakes = listOf(
        MistakeItem(
            userSaid = "Ich habe gestern nach Berlin gefahren.",
            correctVersion = "Ich bin gestern nach Berlin gefahren.",
            explanation = "'fahren' implies motion / change of location, which requires the auxiliary 'sein' (ich bin) instead of 'haben'.",
            grammarCategory = "Perfekt Auxiliary (haben/sein)",
            cefrLevel = "A2",
            timestamp = System.currentTimeMillis() - 86400000L * 1,
            mastery = 50,
            retryCount = 2,
            isResolved = false
        ),
        MistakeItem(
            userSaid = "Ich helfe der Mann mit seinem Koffer.",
            correctVersion = "Ich helfe dem Mann mit seinem Koffer.",
            explanation = "The verb 'helfen' requires the Dativ case. Masculine 'der Mann' changes to 'dem Mann'.",
            grammarCategory = "Dativ Case",
            cefrLevel = "A2",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            mastery = 35,
            retryCount = 3,
            isResolved = false
        ),
        MistakeItem(
            userSaid = "Ich kann heute nicht kommen, weil ich habe keine Zeit.",
            correctVersion = "Ich kann heute nicht kommen, weil ich keine Zeit habe.",
            explanation = "The subordinating conjunction 'weil' kicks the conjugated verb 'habe' to the end of the sub-clause.",
            grammarCategory = "Subordinate Clause Word Order",
            cefrLevel = "A2",
            timestamp = System.currentTimeMillis() - 86400000L * 3,
            mastery = 45,
            retryCount = 2,
            isResolved = false
        ),
        MistakeItem(
            userSaid = "Ich interessiere mich an deutscher Geschichte.",
            correctVersion = "Ich interessiere mich für deutsche Geschichte.",
            explanation = "The reflexive verb 'sich interessieren' always pairs with the preposition 'für' + Akkusativ.",
            grammarCategory = "Verb-Preposition Collocations",
            cefrLevel = "B1",
            timestamp = System.currentTimeMillis() - 86400000L * 4,
            mastery = 60,
            retryCount = 1,
            isResolved = false
        )
    )

    val initialScenarios = listOf(
        RoleplayScenario(
            id = "sc_restaurant",
            title = "Traditional German Restaurant",
            subtitle = "Wirtshaus & Biergarten",
            iconEmoji = "🍽️",
            category = "Daily Life",
            cefrLevel = "A2",
            context = "You are visiting 'Gasthaus zur Linde' in Munich. You want to request a table, ask for recommendations, order traditional food, and pay the bill.",
            goal = "Order a meal, ask if the dish contains pork, and request the bill politely.",
            aiRole = "Friendly Bavarian waiter (Kellner)",
            userRole = "Guest / Customer",
            initialMessage = "Grüß Gott! Willkommen in der Linde. Möchten Sie drinnen oder draußen im Biergarten sitzen?",
            suggestedPhrases = listOf("Haben Sie einen Tisch für zwei Personen?", "Was können Sie heute empfehlen?", "Ich hätte gern das Schnitzel.", "Zahlen bitte, zusammen.")
        ),
        RoleplayScenario(
            id = "sc_train_station",
            title = "Deutsche Bahn Train Station",
            subtitle = "Ticket counter & delays",
            iconEmoji = "🚆",
            category = "Travel",
            cefrLevel = "A2",
            context = "At Berlin Hauptbahnhof, your ICE train to Frankfurt is delayed. You need to check alternate connections and reserve a seat.",
            goal = "Inquire about the next connection to Frankfurt and ask from which platform it departs.",
            aiRole = "DB Information Desk Agent",
            userRole = "Passenger",
            initialMessage = "Guten Tag, DB Reisezentrum Berlin. Wie kann ich Ihnen bei Ihrer Reise helfen?",
            suggestedPhrases = listOf("Mein Zug hat Verspätung. Wann fährt der nächste Zug?", "Von welchem Gleis fährt die Regionalbahn ab?", "Muss ich umsteigen?", "Kann ich meinen Sitzplatz umbuchen?")
        ),
        RoleplayScenario(
            id = "sc_job_interview",
            title = "Tech Job Interview",
            subtitle = "Vorstellungsgespräch",
            iconEmoji = "💼",
            category = "Career",
            cefrLevel = "B1",
            context = "You are interviewing for a Software Engineer role at a tech startup in Hamburg. The recruiter asks about your background and strengths.",
            goal = "Describe your previous experience, why you want to work in Germany, and answer behavioral questions.",
            aiRole = "Hiring Manager (Herr Weber)",
            userRole = "Job Candidate",
            initialMessage = "Guten Tag! Schön, dass Sie da sind. Erzählen Sie mir doch kurz von Ihrem Werdegang und Ihren bisherigen Projekten.",
            suggestedPhrases = listOf("Ich habe drei Jahre Erfahrung in der Softwareentwicklung.", "Meine größte Stärke ist schnelles Problemlösen.", "Warum ich in Deutschland arbeiten möchte...", "Wie sieht die Teamstruktur aus?")
        ),
        RoleplayScenario(
            id = "sc_doctor",
            title = "Doctor's Appointment",
            subtitle = "Beim Arzt / Praxis",
            iconEmoji = "🩺",
            category = "Health",
            cefrLevel = "A2",
            context = "You woke up with severe throat pain and a fever. You visit an Allgemeinarzt (general practitioner) practice in Cologne.",
            goal = "Explain your symptoms, how long you've had them, and understand the prescription instructions.",
            aiRole = "General Practitioner (Frau Dr. Schmidt)",
            userRole = "Patient",
            initialMessage = "Guten Morgen. Nehmen Sie bitte Platz. Was fehlt Ihnen denn? Welche Beschwerden haben Sie?",
            suggestedPhrases = listOf("Ich habe seit zwei Tagen starke Halsschmerzen und Fieber.", "Mir ist oft schwindelig.", "Muss ich Antibiotika einnehmen?", "Brauche ich eine Krankschreibung für meinen Arbeitgeber?")
        ),
        RoleplayScenario(
            id = "sc_buergeramt",
            title = "Bürgeramt City Registration",
            subtitle = "Anmeldung einer Wohnung",
            iconEmoji = "🏛️",
            category = "Bureaucracy",
            cefrLevel = "B1",
            context = "You just moved to Germany and must register your address (Wohnsitzanmeldung) at the local municipal office within 14 days.",
            goal = "Present your Wohnungsgeberbestätigung and passport, and complete the official registration form.",
            aiRole = "Bürgeramt Official (Sachbearbeiter)",
            userRole = "New Resident",
            initialMessage = "Nummer 142 bitte! Guten Tag. Sie möchten eine Wohnung in Frankfurt anmelden? Haben Sie alle Unterlagen dabei?",
            suggestedPhrases = listOf("Hier ist mein Reisepass und die Wohnungsgeberbestätigung.", "Ich bin am ersten des Monats eingezogen.", "Bekomme ich direkt die Anmeldebestätigung?", "Muss ich auch meine Steuer-ID angeben?")
        ),
        RoleplayScenario(
            id = "sc_hotel",
            title = "Hotel Check-In & Requests",
            subtitle = "Hotelrezeption",
            iconEmoji = "🏨",
            category = "Travel",
            cefrLevel = "A1",
            context = "You arrive at a hotel in Vienna. You have a reservation for three nights with breakfast included.",
            goal = "Check into your room, ask for the Wi-Fi password, and ask what time breakfast is served.",
            aiRole = "Hotel Receptionist",
            userRole = "Hotel Guest",
            initialMessage = "Guten Abend und herzlich willkommen im Hotel Central. Haben Sie eine Reservierung?",
            suggestedPhrases = listOf("Ja, ich habe auf den Namen Alex reserviert.", "Ist das Frühstück im Preis inbegriffen?", "Wie lautet das WLAN-Passwort?", "Bis wann muss ich am Abreisetag auschecken?")
        ),
        RoleplayScenario(
            id = "sc_football_training",
            title = "Football Training & Club",
            subtitle = "Fußballtraining & Taktik",
            iconEmoji = "⚽",
            category = "Sports & Hobbies",
            cefrLevel = "A2",
            context = "You join a local German football club (Fußballverein) for your first training session. The coach gives instructions on passing drills and positioning.",
            goal = "Communicate with teammates on the pitch, understand the coach's tactical instructions, and discuss your preferred position.",
            aiRole = "German Football Coach (Trainer Markus)",
            userRole = "New Player",
            initialMessage = "Servus! Schön, dass du beim Probetraining dabei bist. Auf welcher Position spielst du am liebsten? Mittelfeld oder Sturm?",
            suggestedPhrases = listOf("Ich spiele am liebsten im zentralen Mittelfeld.", "Pass mir den Ball!", "Wie lautet die Taktik für das heutige Trainingsspiel?", "Guter Schuss! Weiter so!")
        ),
        RoleplayScenario(
            id = "sc_bakery",
            title = "German Bakery Morning",
            subtitle = "Bäckerei & Konditorei",
            iconEmoji = "🥐",
            category = "Daily Life",
            cefrLevel = "A1",
            context = "Sunday morning at a local German bakery. The scent of fresh Brötchen (bread rolls) and pretzels fills the air.",
            goal = "Order specific types of bread rolls, a pretzel, and a slice of apple cake.",
            aiRole = "Bakery Assistant (Bäckerin)",
            userRole = "Customer",
            initialMessage = "Guten Morgen! Der Nächste bitte. Was darf es für Sie sein?",
            suggestedPhrases = listOf("Ich hätte gern zwei Laugenbrötchen und eine Brezel.", "Haben Sie noch Vollkornbrot?", "Ein Stück Apfelkuchen dazu, bitte.", "Das wäre alles, danke.")
        ),
        RoleplayScenario(
            id = "sc_dating",
            title = "First Date at a Café",
            subtitle = "Kennenlernen & Hobbies",
            iconEmoji = "☕",
            category = "Social",
            cefrLevel = "A2",
            context = "You meet someone for coffee at a trendy café in Leipzig. You talk about your background, favorite music, travel, and everyday life in Germany.",
            goal = "Ask conversational questions, share stories about your hometown, and find common interests.",
            aiRole = "Friendly Date (Lena / Jonas)",
            userRole = "Alex",
            initialMessage = "Hallo Alex! Schön, dich kennenzulernen. Der Kaffee hier ist echt super. Wie gefällt dir das Café?",
            suggestedPhrases = listOf("Das Café ist wirklich gemütlich.", "Was machst du am Wochenende am liebsten?", "Ich reise sehr gern und lerne gern neue Sprachen.", "Welche Musik hörst du am liebsten?")
        ),
        RoleplayScenario(
            id = "sc_flatshare",
            title = "WG-Casting (Apartment Hunt)",
            subtitle = "Wohngemeinschaft Besichtigung",
            iconEmoji = "🏠",
            category = "Daily Life",
            cefrLevel = "B1",
            context = "You visit a student flatshare (WG) in Heidelberg to introduce yourself to current flatmates and see if you are a good match.",
            goal = "Present your lifestyle, cooking habits, cleanliness standards, and ask about the monthly warm rent.",
            aiRole = "WG Flatmate (Maximilian)",
            userRole = "Prospective Flatmate",
            initialMessage = "Hey! Komm doch rein, setz dich in die Küche. Möchtest du einen Tee? Erzähl mal, wie sieht dein WG-Alltag aus?",
            suggestedPhrases = listOf("Ich koche sehr gerne abends mit Freunden.", "Wie ist euer Putzplan organisiert?", "Wie hoch sind die Nebenkosten und das Internet?", "Gibt es feste Ruhezeiten?")
        )
    )
}
