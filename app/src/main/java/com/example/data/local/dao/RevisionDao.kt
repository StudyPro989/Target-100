package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.RevisionSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RevisionDao {
    @Query("SELECT * FROM revision_sessions ORDER BY scheduledDate ASC")
    fun getAllSessions(): Flow<List<RevisionSessionEntity>>

    @Query("SELECT * FROM revision_sessions WHERE chapterId = :chapterId ORDER BY scheduledDate DESC")
    fun getSessionsForChapter(chapterId: Long): Flow<List<RevisionSessionEntity>>

    @Query("SELECT * FROM revision_sessions WHERE isCompleted = 0 ORDER BY scheduledDate ASC")
    fun getPendingSessions(): Flow<List<RevisionSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: RevisionSessionEntity): Long

    @Update
    suspend fun updateSession(session: RevisionSessionEntity)

    @Delete
    suspend fun deleteSession(session: RevisionSessionEntity)
}
