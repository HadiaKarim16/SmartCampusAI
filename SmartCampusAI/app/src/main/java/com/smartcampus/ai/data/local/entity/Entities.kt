package com.smartcampus.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ─────────────────────────────────────────────
//  USER ENTITY
// ─────────────────────────────────────────────
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val profileImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
//  ASSIGNMENT ENTITY
// ─────────────────────────────────────────────
@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val subject: String,
    val deadline: Long,
    val priority: String,          // LOW | MEDIUM | HIGH | URGENT
    val status: String = "PENDING", // PENDING | IN_PROGRESS | COMPLETED
    val isRecurring: Boolean = false,
    val recurringInterval: String? = null, // DAILY | WEEKLY | MONTHLY
    val createdAt: Long = System.currentTimeMillis(),
    val reminderAt: Long? = null
)

// ─────────────────────────────────────────────
//  NOTE ENTITY
// ─────────────────────────────────────────────
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "General",
    val attachmentPath: String? = null,
    val attachmentType: String? = null, // IMAGE | PDF | FILE
    val isPinned: Boolean = false,
    val color: String = "#1E1E2E",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
//  ATTENDANCE ENTITY
// ─────────────────────────────────────────────
@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val attendedClasses: Int = 0,
    val totalClasses: Int = 0,
    val requiredPercentage: Float = 75f,
    val semester: String = "Current",
    val color: String = "#6C63FF",
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
//  ATTENDANCE LOG ENTITY
// ─────────────────────────────────────────────
@Entity(tableName = "attendance_log")
data class AttendanceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val attendanceId: Int,
    val date: Long,
    val status: String  // PRESENT | ABSENT | LATE
)

// ─────────────────────────────────────────────
//  TIMETABLE ENTITY
// ─────────────────────────────────────────────
@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val teacher: String = "",
    val room: String = "",
    val day: String,           // MON | TUE | WED | THU | FRI | SAT
    val startTime: String,     // HH:mm format
    val endTime: String,       // HH:mm format
    val color: String = "#6C63FF",
    val notifyBefore: Int = 10 // minutes before class
)

// ─────────────────────────────────────────────
//  POMODORO SESSION ENTITY
// ─────────────────────────────────────────────
@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val duration: Int,          // in minutes
    val breakDuration: Int = 5, // in minutes
    val taskTitle: String = "",
    val completed: Boolean = false,
    val focusScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
//  AI CHAT HISTORY ENTITY
// ─────────────────────────────────────────────
@Entity(tableName = "ai_chat_history")
data class AiChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userMessage: String,
    val aiResponse: String,
    val subject: String = "General",
    val createdAt: Long = System.currentTimeMillis()
)
