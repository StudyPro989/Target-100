package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId AND isHidden = 0 ORDER BY displayOrder ASC, chapterNumber ASC")
    fun getVisibleChaptersBySubject(subjectId: Long): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY displayOrder ASC, chapterNumber ASC")
    fun getAllChaptersBySubject(subjectId: Long): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE isHidden = 0 ORDER BY displayOrder ASC, chapterNumber ASC")
    fun getAllVisibleChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters ORDER BY displayOrder ASC, chapterNumber ASC")
    fun getAllChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: Long): ChapterEntity?

    @Query("SELECT COUNT(*) FROM chapters WHERE isHidden = 0 AND status = 'VERIFIED_COMPLETED'")
    fun getVerifiedChaptersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM chapters WHERE isHidden = 0")
    fun getTotalVisibleChaptersCount(): Flow<Int>

    @Query("SELECT * FROM chapters WHERE isHidden = 0 AND (status = 'NEEDS_REVISION' OR (totalTestAttempts > 0 AND bestScorePercent < configuredPassingPercent)) ORDER BY lastTestedAt DESC")
    fun getWeakChapters(): Flow<List<ChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)
}
