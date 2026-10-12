package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.ChapterStatus

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"]), Index(value = ["displayOrder"])]
)
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val chapterNumber: Int,
    val titleEn: String,
    val titleHi: String,
    val status: String = ChapterStatus.NOT_STARTED.name,
    val configuredPassingPercent: Int = 75,
    val isHidden: Boolean = false,
    val displayOrder: Int = 0,
    val totalTestAttempts: Int = 0,
    val bestScorePercent: Int = 0,
    val lastScorePercent: Int? = null,
    val lastTestedAt: Long? = null,
    val lastStudiedAt: Long? = null,
    val verifiedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
