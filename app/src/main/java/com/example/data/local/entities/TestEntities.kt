package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "test_attempts",
    indices = [
        Index(value = ["chapterId"]),
        Index(value = ["subjectId"]),
        Index(value = ["attemptDate"])
    ]
)
data class TestAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chapterId: Long,
    val subjectId: Long,
    val attemptDate: Long = System.currentTimeMillis(),
    val totalQuestions: Int,
    val correctAnswers: Int,
    val scorePercent: Int,
    val passingThreshold: Int,
    val isPassed: Boolean,
    val difficulty: String, // EASY, MEDIUM, HARD
    val providerUsed: String, // GEMINI, OPENAI, OFFLINE_BANK
    val timeSpentSeconds: Int = 0,
    val isRevisionTest: Boolean = false
)

@Entity(
    tableName = "test_questions",
    foreignKeys = [
        ForeignKey(
            entity = TestAttemptEntity::class,
            parentColumns = ["id"],
            childColumns = ["attemptId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["attemptId"])]
)
data class TestQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val attemptId: Long,
    val questionIndex: Int,
    val questionText: String,
    val optionsJson: String, // JSON Array of 4 strings
    val correctOptionIndex: Int,
    val selectedOptionIndex: Int?,
    val explanation: String,
    val isCorrect: Boolean
)

@Entity(
    tableName = "revision_sessions",
    indices = [Index(value = ["chapterId"]), Index(value = ["scheduledDate"])]
)
data class RevisionSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chapterId: Long,
    val subjectId: Long,
    val scheduledDate: String, // YYYY-MM-DD
    val notes: String = "",
    val isCompleted: Boolean = false,
    val quizAttemptId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
