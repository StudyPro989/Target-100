package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.Priority
import com.example.model.TargetStatus
import com.example.model.TargetType

@Entity(
    tableName = "targets",
    indices = [
        Index(value = ["targetDate"]),
        Index(value = ["status"]),
        Index(value = ["subjectId"]),
        Index(value = ["chapterId"])
    ]
)
data class TargetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subjectId: Long? = null,
    val chapterId: Long? = null,
    val type: String = TargetType.CHAPTER_STUDY.name,
    val targetDate: String, // Format: YYYY-MM-DD
    val originalDate: String, // Format: YYYY-MM-DD
    val status: String = TargetStatus.PENDING.name,
    val priority: String = Priority.MEDIUM.name,
    val notes: String = "",
    val reminderTime: String? = null, // e.g. "18:00"
    val rescheduleCount: Int = 0,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "target_history",
    indices = [Index(value = ["targetId"])]
)
data class TargetHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetId: Long,
    val action: String, // "ROLLED_OVER", "RESCHEDULED", "COMPLETED", "CANCELLED"
    val fromStatus: String,
    val toStatus: String,
    val fromDueDate: String,
    val toDueDate: String,
    val timestamp: Long = System.currentTimeMillis()
)
