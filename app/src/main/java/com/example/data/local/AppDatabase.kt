package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.GrammarTopic
import com.example.data.model.Lesson
import com.example.data.model.MistakeItem
import com.example.data.model.RoleplayScenario
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        Lesson::class,
        VocabularyItem::class,
        GrammarTopic::class,
        MistakeItem::class,
        RoleplayScenario::class,
        ChatMessage::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun lessonDao(): LessonDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun grammarDao(): GrammarDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun scenarioDao(): ScenarioDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "deutsch_ai_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            database.userDao().insertOrUpdateUser(SeedData.initialUser)
                            database.lessonDao().insertLessons(SeedData.initialLessons)
                            database.vocabularyDao().insertVocabulary(SeedData.initialVocabulary)
                            database.grammarDao().insertGrammarTopics(SeedData.initialGrammarTopics)
                            SeedData.initialMistakes.forEach { database.mistakeDao().insertMistake(it) }
                            database.scenarioDao().insertScenarios(SeedData.initialScenarios)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
