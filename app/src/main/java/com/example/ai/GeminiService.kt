package com.example.ai

import com.example.BuildConfig
import com.example.data.model.ConversationMode
import com.example.data.model.CorrectionLevel
import com.example.data.model.TutorPersonality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateTutorResponse(
        userInput: String,
        personality: TutorPersonality,
        correctionLevel: CorrectionLevel,
        userLevel: String,
        mode: ConversationMode,
        scenarioContext: String?,
        history: List<Pair<String, String>>
    ): TutorAnalysis? = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext null
        }

        try {
            val systemPrompt = """
                You are DeutschAI, the world's most capable personal German tutor.
                Target CEFR level: $userLevel.
                Conversation Mode: ${mode.title}.
                Scenario Context: ${scenarioContext ?: "General German Conversation"}.
                Tutor Personality: ${personality.title} (${personality.stylePrompt}).
                Correction Mode: ${correctionLevel.title}.

                Instructions:
                1. Always respond in natural German matched to $userLevel CEFR level.
                2. Analyze the user's input for German grammar errors (e.g., Dativ/Akkusativ, Perfekt haben/sein, word order in subordinate clauses with weil/dass, adjective endings, modal verb brackets).
                3. Return a STRICT JSON object with the following schema:
                {
                   "responseText": "Your German response to the user",
                   "translation": "English translation of your response",
                   "hasCorrection": true/false,
                   "correctedUtterance": "corrected German sentence if user made an error, otherwise null",
                   "grammarExplanation": "Clear pedagogical explanation of the mistake if any, otherwise null",
                   "naturalAlternative": "A more native idiomatic alternative phrasing if applicable, otherwise null",
                   "targetedGrammarCategory": "e.g. Dativ Case / Verb Placement / null",
                   "suggestedFollowUp": "A question or prompt to keep the German dialogue moving"
                }
            """.trimIndent()

            val contentsArray = JSONArray()

            // System instruction as first part
            val sysPart = JSONObject().put("text", systemPrompt)
            val sysContent = JSONObject().put("role", "user").put("parts", JSONArray().put(sysPart))
            contentsArray.put(sysContent)

            val sysAck = JSONObject().put("role", "model").put("parts", JSONArray().put(JSONObject().put("text", "Verstanden! Ich bin DeutschAI und antworte im geforderten JSON-Format.")))
            contentsArray.put(sysAck)

            // Conversation history
            for ((sender, text) in history.takeLast(4)) {
                val role = if (sender == "USER") "user" else "model"
                val contentObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(JSONObject().put("text", text)))
                contentsArray.put(contentObj)
            }

            // Current message
            val userPart = JSONObject().put("text", userInput)
            val userContent = JSONObject().put("role", "user").put("parts", JSONArray().put(userPart))
            contentsArray.put(userContent)

            val requestJson = JSONObject()
                .put("contents", contentsArray)
                .put("generationConfig", JSONObject().put("temperature", 0.7))

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext null
            }

            val bodyString = response.body?.string() ?: return@withContext null
            val root = JSONObject(bodyString)
            val candidates = root.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null

            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return@withContext null
            val parts = content.optJSONArray("parts") ?: return@withContext null
            if (parts.length() == 0) return@withContext null

            var rawText = parts.getJSONObject(0).optString("text", "")
            // Strip any markdown json wrappers if present
            if (rawText.contains("```json")) {
                rawText = rawText.substringAfter("```json").substringBefore("```").trim()
            } else if (rawText.contains("```")) {
                rawText = rawText.substringAfter("```").substringBefore("```").trim()
            }

            val parsedJson = JSONObject(rawText)
            return@withContext TutorAnalysis(
                responseText = parsedJson.optString("responseText", "Sehr gut! Lass uns weitermachen."),
                translation = parsedJson.optString("translation", "Very good! Let's continue."),
                hasCorrection = parsedJson.optBoolean("hasCorrection", false),
                userOriginalUtterance = if (parsedJson.optBoolean("hasCorrection", false)) userInput else null,
                correctedUtterance = parsedJson.optString("correctedUtterance").takeIf { it.isNotBlank() && it != "null" },
                grammarExplanation = parsedJson.optString("grammarExplanation").takeIf { it.isNotBlank() && it != "null" },
                naturalAlternative = parsedJson.optString("naturalAlternative").takeIf { it.isNotBlank() && it != "null" },
                targetedGrammarCategory = parsedJson.optString("targetedGrammarCategory").takeIf { it.isNotBlank() && it != "null" },
                suggestedFollowUp = parsedJson.optString("suggestedFollowUp").takeIf { it.isNotBlank() && it != "null" },
                cefrLevel = userLevel
            )
        } catch (e: Throwable) {
            // Graceful fallback to local engine
            return@withContext null
        }
    }
}
