package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameEn: String,
    val nameHi: String,
    val code: String,
    val iconName: String,
    val displayOrder: Int,
    val isOptional: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
