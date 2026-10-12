package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ChapterDao
import com.example.data.local.dao.GamificationDao
import com.example.data.local.dao.RevisionDao
import com.example.data.local.dao.SettingsDao
import com.example.data.local.dao.SubjectDao
import com.example.data.local.dao.TargetDao
import com.example.data.local.dao.TestDao
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.ChapterEntity
import com.example.data.local.entities.DailyStreakEntity
import com.example.data.local.entities.RevisionSessionEntity
import com.example.data.local.entities.SubjectEntity
import com.example.data.local.entities.TargetEntity
import com.example.data.local.entities.TargetHistoryEntity
import com.example.data.local.entities.TestAttemptEntity
import com.example.data.local.entities.TestQuestionEntity
import com.example.data.local.entities.XpTransactionEntity
import com.example.data.prepopulate.RbseClass10Syllabus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SubjectEntity::class,
        ChapterEntity::class,
        TargetEntity::class,
        TargetHistoryEntity::class,
        TestAttemptEntity::class,
        TestQuestionEntity::class,
        RevisionSessionEntity::class,
        XpTransactionEntity::class,
        DailyStreakEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun chapterDao(): ChapterDao
    abstract fun targetDao(): TargetDao
    abstract fun testDao(): TestDao
    abstract fun revisionDao(): RevisionDao
    abstract fun gamificationDao(): GamificationDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "target100_rbse.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate authentic initial RBSE Class 10 Syllabus
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                val (subjects, chapters) = RbseClass10Syllabus.toEntities()
                                database.subjectDao().insertSubjects(subjects)
                                database.chapterDao().insertChapters(chapters)

                                // Populate default settings
                                val settingsDao = database.settingsDao()
                                settingsDao.setSetting(AppSettingsEntity("language", "en")) // default to English
                                settingsDao.setSetting(AppSettingsEntity("theme_mode", "SYSTEM"))
                                settingsDao.setSetting(AppSettingsEntity("accent_theme", "GOLD_RAJPUTANA"))
                                settingsDao.setSetting(AppSettingsEntity("exam_name", "RBSE Class 10 Board Exam 2026"))
                                settingsDao.setSetting(AppSettingsEntity("exam_date", "2026-03-05"))
                                settingsDao.setSetting(AppSettingsEntity("default_reminder_time", "19:00"))
                                settingsDao.setSetting(AppSettingsEntity("daily_ai_limit", "15"))
                                settingsDao.setSetting(AppSettingsEntity("ai_provider", "GEMINI"))
                                settingsDao.setSetting(AppSettingsEntity("ai_model", "gemini-3.5-flash"))
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
