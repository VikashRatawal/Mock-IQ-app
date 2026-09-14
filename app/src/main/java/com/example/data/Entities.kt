package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mock_tests")
data class MockTestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val name: String,
    val subject: String,
    val type: String, // "Sectional" or "Full Mock"
    val totalQuestions: Int,
    val totalTimeMinutes: Int,
    val score: Double,
    val percentile: Double,
    val isComplete: Boolean = true
)

@Entity(tableName = "question_records")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mockId: Long, // Associates with MockTestEntity.id
    val qNo: Int,
    val status: String, // "Correct", "Incorrect", "Unattempted"
    val chapter: String,
    val topic: String,
    val subtopic: String,
    val difficultyLevel: String, // "Very Easy", "Easy", "Medium", "Hard", "Very Hard"
    val timeTakenSeconds: Int,
    val timeDeltaSeconds: Int, // positive = overtime, negative = saved
    val reason: String, // e.g. "Silly Mistake", "Unknown Concept"
    val tags: String, // Comma-separated: "Must Revise,Trap Question"
    val notes: String,
    val formula: String,
    val confidence: String, // "High", "Medium", "Low"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_reasons")
data class CustomReasonEntity(
    @PrimaryKey val reason: String
)

@Entity(tableName = "custom_tags")
data class CustomTagEntity(
    @PrimaryKey val tag: String
)
