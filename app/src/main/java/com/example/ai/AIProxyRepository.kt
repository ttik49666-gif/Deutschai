package com.example.ai

import android.util.Log
import com.example.data.model.ArabicTranslationDetail
import com.example.data.model.ConversationMode
import com.example.data.model.UserProfile
import com.example.data.model.WordMeaning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

interface AIProxyRepository {
    suspend fun generateTutorResponse(
        promptRequest: TutorPromptRequest,
        mode: ConversationMode
    ): TutorAnalysis

    suspend fun evaluateWriting(
        text: String,
        prompt: String,
        targetCEFR: String
    ): WritingEvaluationReport

    suspend fun evaluatePronunciation(
        targetSentence: String,
        userSpokenText: String
    ): ShadowingAnalysis

    suspend fun evaluatePlacement(
        grammarScore: Int,
        vocabScore: Int,
        readingScore: Int,
        listeningScore: Int,
        speakingScore: Int,
        writingScore: Int
    ): PlacementEvaluationResult
}

class AIProxyRepositoryImpl(
    private val proxyBaseUrl: String? = null
) : AIProxyRepository {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    override suspend fun generateTutorResponse(
        promptRequest: TutorPromptRequest,
        mode: ConversationMode
    ): TutorAnalysis = withContext(Dispatchers.IO) {
        // If a proxy backend URL is configured, try proxy first
        if (!proxyBaseUrl.isNullOrBlank()) {
            try {
                val jsonPayload = JSONObject().apply {
                    put("userInput", promptRequest.userInput)
                    put("personality", promptRequest.personality.name)
                    put("correctionLevel", promptRequest.correctionLevel.name)
                    put("userLevel", promptRequest.userLevel)
                    put("dialect", promptRequest.dialect.name)
                    put("mode", mode.name)
                    put("scenarioContext", promptRequest.scenarioContext ?: "")
                }

                val body = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("$proxyBaseUrl/api/v1/tutor/chat")
                    .post(body)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (!responseBody.isNullOrBlank()) {
                            val parsed = parseTutorJsonResponse(responseBody)
                            if (parsed != null) return@withContext parsed
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("AIProxyRepository", "Proxy chat call failed, falling back to local smart engine: ${e.message}")
            }
        }

        // Offline-first robust local smart engine fallback
        return@withContext LocalSmartTutorEngine.analyzeAndRespond(
            userText = promptRequest.userInput,
            personality = promptRequest.personality,
            correctionLevel = promptRequest.correctionLevel,
            userLevel = promptRequest.userLevel,
            mode = mode,
            dialect = promptRequest.dialect,
            scenarioContext = promptRequest.scenarioContext,
            history = promptRequest.history
        )
    }

    override suspend fun evaluateWriting(
        text: String,
        prompt: String,
        targetCEFR: String
    ): WritingEvaluationReport = withContext(Dispatchers.IO) {
        if (!proxyBaseUrl.isNullOrBlank()) {
            try {
                val jsonPayload = JSONObject().apply {
                    put("text", text)
                    put("prompt", prompt)
                    put("targetCEFR", targetCEFR)
                }

                val body = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("$proxyBaseUrl/api/v1/tutor/evaluate-writing")
                    .post(body)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (!responseBody.isNullOrBlank()) {
                            val parsed = parseWritingJsonResponse(responseBody, text, targetCEFR)
                            if (parsed != null) return@withContext parsed
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("AIProxyRepository", "Proxy writing call failed, using local evaluator: ${e.message}")
            }
        }

        return@withContext LocalSmartTutorEngine.evaluateWriting(text, prompt, targetCEFR)
    }

    override suspend fun evaluatePronunciation(
        targetSentence: String,
        userSpokenText: String
    ): ShadowingAnalysis = withContext(Dispatchers.Default) {
        return@withContext PronunciationEvaluator.evaluateSpeech(targetSentence, userSpokenText)
    }

    override suspend fun evaluatePlacement(
        grammarScore: Int,
        vocabScore: Int,
        readingScore: Int,
        listeningScore: Int,
        speakingScore: Int,
        writingScore: Int
    ): PlacementEvaluationResult = withContext(Dispatchers.Default) {
        return@withContext LocalSmartTutorEngine.evaluatePlacementAnswers(
            grammarScore = grammarScore,
            vocabScore = vocabScore,
            readingScore = readingScore,
            listeningScore = listeningScore,
            speakingScore = speakingScore,
            writingScore = writingScore
        )
    }

    private fun parseTutorJsonResponse(jsonString: String): TutorAnalysis? {
        return try {
            val root = JSONObject(jsonString)
            val responseText = when {
                root.has("germanResponse") && !root.getString("germanResponse").isNullOrBlank() -> root.getString("germanResponse")
                root.has("responseText") && !root.getString("responseText").isNullOrBlank() -> root.getString("responseText")
                else -> ""
            }
            if (responseText.isBlank()) return null

            // Support either flat contract or nested arabicTranslation object
            val literal = when {
                root.has("literalTranslation") -> root.optString("literalTranslation", "")
                root.has("arabicTranslation") -> root.optJSONObject("arabicTranslation")?.optString("literalTranslation", "") ?: ""
                else -> ""
            }

            val darija = when {
                root.has("darijaContextualTranslation") -> root.optString("darijaContextualTranslation", "")
                root.has("arabicTranslation") -> root.optJSONObject("arabicTranslation")?.optString("darijaAlternative", "") ?: ""
                else -> ""
            }

            val grammarNotes = when {
                root.has("grammarExplanation") -> root.optString("grammarExplanation", "")
                root.has("arabicTranslation") -> root.optJSONObject("arabicTranslation")?.optString("grammarNotes", "") ?: ""
                else -> ""
            }

            val wordsList = mutableListOf<WordMeaning>()
            val vocabArray = root.optJSONArray("vocabularyBreakdown") 
                ?: root.optJSONObject("arabicTranslation")?.optJSONArray("wordByWord")
            if (vocabArray != null) {
                for (i in 0 until vocabArray.length()) {
                    val w = vocabArray.getJSONObject(i)
                    val gWord = w.optString("word", w.optString("german", ""))
                    val aMeaning = w.optString("meaning", w.optString("arabic", ""))
                    val wType = w.optString("type", w.optString("pos", ""))
                    if (gWord.isNotBlank()) {
                        wordsList.add(WordMeaning(german = gWord, arabic = aMeaning, pos = wType.ifBlank { null }))
                    }
                }
            }

            val arabicDetail = ArabicTranslationDetail(
                contextualTranslation = if (darija.isNotBlank()) darija else literal,
                literalTranslation = literal,
                wordByWord = wordsList,
                grammarNotes = grammarNotes.ifBlank { null },
                darijaAlternative = darija.ifBlank { null }
            )

            val correctionVal = root.optString("correction", "").ifBlank { null }
            val hasCorr = correctionVal != null || root.optBoolean("hasCorrection", false)

            val followUp = root.optString("followUpQuestion", root.optString("suggestedFollowUp", "")).ifBlank { null }

            TutorAnalysis(
                responseText = responseText,
                translation = arabicDetail.contextualTranslation,
                arabicDetail = arabicDetail,
                hasCorrection = hasCorr,
                userOriginalUtterance = root.optString("userOriginalUtterance", null),
                correctedUtterance = correctionVal ?: root.optString("correctedUtterance", null),
                grammarExplanation = root.optString("grammarExplanation", null),
                grammarExplanationAr = root.optString("grammarExplanationAr", grammarNotes.ifBlank { null }),
                naturalAlternative = root.optString("naturalAlternative", null),
                targetedGrammarCategory = root.optString("targetedGrammarCategory", null),
                suggestedFollowUp = followUp,
                suggestedFollowUpAr = root.optString("suggestedFollowUpAr", null),
                cefrLevel = root.optString("cefrLevel", "A2")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseWritingJsonResponse(
        jsonString: String,
        originalText: String,
        targetCEFR: String
    ): WritingEvaluationReport? {
        return try {
            val root = JSONObject(jsonString)
            WritingEvaluationReport(
                overallScorePercent = root.optInt("overallScorePercent", 75),
                estimatedCEFR = root.optString("estimatedCEFR", targetCEFR),
                grammarScore = root.optInt("grammarScore", 70),
                vocabularyScore = root.optInt("vocabularyScore", 75),
                coherenceScore = root.optInt("coherenceScore", 80),
                taskFulfillmentScore = root.optInt("taskFulfillmentScore", 80),
                originalText = originalText,
                correctedText = root.optString("correctedText", originalText),
                detailedFeedback = root.optString("detailedFeedback", ""),
                detailedFeedbackAr = root.optString("detailedFeedbackAr", ""),
                improvedNativeVersion = root.optString("improvedNativeVersion", originalText)
            )
        } catch (e: Exception) {
            null
        }
    }
}
