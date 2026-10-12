package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "xp_transactions",
    indices = [Index(value = ["reason", "referenceId"], unique = true)]
)
data class XpTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int,
    val reason: String, // "TARGET_COMPLETED", "CHAPTER_VERIFIED", "REVISION_DONE", "STREAK_BONUS"
    val referenceId: String, // e.g. "target_12" or "chapter_5_verified" to prevent duplicate rewards
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_streaks")
data class DailyStreakEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val completedTargetsCount: Int = 0,
    val verifiedTestsCount: Int = 0,
    val streakQualified: Boolean = false
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val key: String,
    val value: String
)
