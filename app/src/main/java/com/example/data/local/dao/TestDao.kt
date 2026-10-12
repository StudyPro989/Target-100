package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.TestAttemptEntity
import com.example.data.local.entities.TestQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: TestAttemptEntity): Long

    @Query("SELECT * FROM test_attempts WHERE id = :id")
    suspend fun getAttemptById(id: Long): TestAttemptEntity?

    @Query("SELECT * FROM test_attempts ORDER BY attemptDate DESC")
    fun getAllAttempts(): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts WHERE chapterId = :chapterId ORDER BY attemptDate DESC")
    fun getAttemptsForChapter(chapterId: Long): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts ORDER BY attemptDate DESC LIMIT :limit")
    fun getRecentAttempts(limit: Int): Flow<List<TestAttemptEntity>>

    @Query("SELECT AVG(scorePercent) FROM test_attempts")
    fun getAverageScorePercent(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<TestQuestionEntity>)

    @Query("SELECT * FROM test_questions WHERE attemptId = :attemptId ORDER BY questionIndex ASC")
    suspend fun getQuestionsForAttempt(attemptId: Long): List<TestQuestionEntity>
}
