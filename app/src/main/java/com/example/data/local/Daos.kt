package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessage
import com.example.data.model.GrammarTopic
import com.example.data.model.Lesson
import com.example.data.model.MistakeItem
import com.example.data.model.RoleplayScenario
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserProfile(id: String = "primary_user"): Flow<UserProfile?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserProfileOnce(id: String = "primary_user"): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserProfile)

    @Update
    suspend fun updateUser(user: UserProfile)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY moduleNumber ASC")
    fun getAllLessons(): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons WHERE cefrLevel = :level ORDER BY moduleNumber ASC")
    fun getLessonsByLevel(level: String): Flow<List<Lesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)

    @Update
    suspend fun updateLesson(lesson: Lesson)

    @Query("UPDATE lessons SET isCompleted = :completed, masteryScore = :mastery WHERE id = :id")
    suspend fun markLessonCompleted(id: String, completed: Boolean = true, mastery: Int = 100)
}

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary ORDER BY lastReviewed DESC")
    fun getAllVocabulary(): Flow<List<VocabularyItem>>

    @Query("SELECT * FROM vocabulary WHERE cefrLevel = :level")
    fun getVocabularyByLevel(level: String): Flow<List<VocabularyItem>>

    @Query("SELECT * FROM vocabulary WHERE masteryScore < 60 OR mistakeCount > 0 ORDER BY masteryScore ASC")
    fun getWeakVocabulary(): Flow<List<VocabularyItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(items: List<VocabularyItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWord(item: VocabularyItem)

    @Query("UPDATE vocabulary SET masteryScore = :score, lastReviewed = :time WHERE id = :id")
    suspend fun updateMastery(id: String, score: Int, time: Long = System.currentTimeMillis())
}

@Dao
interface GrammarDao {
    @Query("SELECT * FROM grammar_topics")
    fun getAllGrammarTopics(): Flow<List<GrammarTopic>>

    @Query("SELECT * FROM grammar_topics WHERE isWeakArea = 1")
    fun getWeakGrammarTopics(): Flow<List<GrammarTopic>>

    @Query("SELECT * FROM grammar_topics WHERE cefrLevel = :level")
    fun getTopicsByLevel(level: String): Flow<List<GrammarTopic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrammarTopics(topics: List<GrammarTopic>)

    @Update
    suspend fun updateGrammarTopic(topic: GrammarTopic)
}

@Dao
interface MistakeDao {
    @Query("SELECT * FROM mistakes ORDER BY timestamp DESC")
    fun getAllMistakes(): Flow<List<MistakeItem>>

    @Query("SELECT * FROM mistakes WHERE isResolved = 0 ORDER BY timestamp DESC")
    fun getUnresolvedMistakes(): Flow<List<MistakeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: MistakeItem)

    @Update
    suspend fun updateMistake(mistake: MistakeItem)

    @Query("DELETE FROM mistakes WHERE id = :id")
    suspend fun deleteMistake(id: Int)
}

@Dao
interface ScenarioDao {
    @Query("SELECT * FROM scenarios")
    fun getAllScenarios(): Flow<List<RoleplayScenario>>

    @Query("SELECT * FROM scenarios WHERE category = :category")
    fun getScenariosByCategory(category: String): Flow<List<RoleplayScenario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenarios(scenarios: List<RoleplayScenario>)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun clearSession(sessionId: String)
}
