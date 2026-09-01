package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ArabicTranslationDetail
import com.example.data.model.WordMeaning
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(separator = "|||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split("|||").filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromArabicTranslationDetail(detail: ArabicTranslationDetail?): String? {
        if (detail == null) return null
        val json = JSONObject()
        json.put("contextualTranslation", detail.contextualTranslation)
        json.put("literalTranslation", detail.literalTranslation)
        json.put("grammarNotes", detail.grammarNotes ?: "")
        json.put("darijaAlternative", detail.darijaAlternative ?: "")

        val wordsArray = JSONArray()
        for (w in detail.wordByWord) {
            val wObj = JSONObject()
            wObj.put("german", w.german)
            wObj.put("arabic", w.arabic)
            wObj.put("pos", w.pos ?: "")
            wordsArray.put(wObj)
        }
        json.put("wordByWord", wordsArray)
        return json.toString()
    }

    @TypeConverter
    fun toArabicTranslationDetail(jsonString: String?): ArabicTranslationDetail? {
        if (jsonString.isNullOrBlank()) return null
        return try {
            val json = JSONObject(jsonString)
            val wordsList = mutableListOf<WordMeaning>()
            val wordsArray = json.optJSONArray("wordByWord")
            if (wordsArray != null) {
                for (i in 0 until wordsArray.length()) {
                    val wObj = wordsArray.getJSONObject(i)
                    wordsList.add(
                        WordMeaning(
                            german = wObj.optString("german", ""),
                            arabic = wObj.optString("arabic", ""),
                            pos = wObj.optString("pos").takeIf { it.isNotBlank() }
                        )
                    )
                }
            }
            ArabicTranslationDetail(
                contextualTranslation = json.optString("contextualTranslation", ""),
                literalTranslation = json.optString("literalTranslation", ""),
                wordByWord = wordsList,
                grammarNotes = json.optString("grammarNotes").takeIf { it.isNotBlank() },
                darijaAlternative = json.optString("darijaAlternative").takeIf { it.isNotBlank() }
            )
        } catch (e: Exception) {
            null
        }
    }
}
