package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "excuse_history")
data class ExcuseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val situation: String,
    val category: String,
    val recipient: String,
    val severityLevel: Int,
    val vibe: String,
    val timestamp: Long = System.currentTimeMillis(),
    val safeText: String,
    val safeBelievability: Int,
    val safeRisk: Int,
    val funnyText: String,
    val funnyBelievability: Int,
    val funnyRisk: Int,
    val unhingedText: String,
    val unhingedBelievability: Int,
    val unhingedRisk: Int,
    val overallBelievability: Int,
    val overallRisk: Int,
    val overallDrama: Int,
    val overallEffort: Int,
    val isDoctorCheckup: Boolean = false,
    val problemNote: String = "",
    val copiedCount: Int = 0
) {
    val selectedExcuseText: String get() = safeText.ifBlank { funnyText.ifBlank { unhingedText } }
    val believability: Int get() = overallBelievability
}
