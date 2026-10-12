package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.TargetEntity
import com.example.data.local.entities.TargetHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetDao {
    @Query("SELECT * FROM targets ORDER BY targetDate ASC, priority DESC")
    fun getAllTargets(): Flow<List<TargetEntity>>

    @Query("SELECT * FROM targets WHERE targetDate = :date ORDER BY status ASC, priority DESC")
    fun getTargetsByDate(date: String): Flow<List<TargetEntity>>

    @Query("SELECT * FROM targets WHERE targetDate < :currentDate AND status IN ('PENDING', 'IN_PROGRESS')")
    suspend fun getOverduePendingTargets(currentDate: String): List<TargetEntity>

    @Query("SELECT * FROM targets WHERE id = :id")
    suspend fun getTargetById(id: Long): TargetEntity?

    @Query("SELECT COUNT(*) FROM targets WHERE targetDate = :date AND status = 'COMPLETED'")
    fun getCompletedTargetsCountForDate(date: String): Flow<Int>

    @Query("SELECT * FROM targets WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getRecentCompletedTargets(): Flow<List<TargetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: TargetEntity): Long

    @Update
    suspend fun updateTarget(target: TargetEntity)

    @Delete
    suspend fun deleteTarget(target: TargetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: TargetHistoryEntity): Long

    @Query("SELECT * FROM target_history WHERE targetId = :targetId ORDER BY timestamp DESC")
    suspend fun getHistoryForTarget(targetId: Long): List<TargetHistoryEntity>
}
