package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.DailyStreakEntity
import com.example.data.local.entities.XpTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertXpTransaction(tx: XpTransactionEntity): Long

    @Query("SELECT SUM(amount) FROM xp_transactions")
    fun getTotalXp(): Flow<Int?>

    @Query("SELECT * FROM xp_transactions ORDER BY timestamp DESC LIMIT 50")
    fun getRecentXpTransactions(): Flow<List<XpTransactionEntity>>

    @Query("SELECT COUNT(*) FROM xp_transactions WHERE reason = :reason AND referenceId = :referenceId")
    suspend fun countTransactionsForRef(reason: String, referenceId: String): Int

    @Query("SELECT * FROM daily_streaks WHERE date = :date")
    suspend fun getStreakForDate(date: String): DailyStreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyStreak(streak: DailyStreakEntity)

    @Query("SELECT * FROM daily_streaks WHERE streakQualified = 1 ORDER BY date DESC")
    fun getQualifiedStreakDays(): Flow<List<DailyStreakEntity>>

    @Query("SELECT * FROM daily_streaks ORDER BY date DESC LIMIT 30")
    fun getRecentStreakHistory(): Flow<List<DailyStreakEntity>>
}
