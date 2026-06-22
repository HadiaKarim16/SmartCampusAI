package com.smartcampus.ai.domain.model

// ─────────────────────────────────────────────
//  USER MODEL
// ─────────────────────────────────────────────
data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val profileImagePath: String? = null
)

// ─────────────────────────────────────────────
//  ASSIGNMENT MODEL
// ─────────────────────────────────────────────
data class Assignment(
    val id: Int = 0,
    val title: String,
    val description: String,
    val subject: String,
    val deadline: Long,
    val priority: Priority,
    val status: AssignmentStatus = AssignmentStatus.PENDING,
    val isRecurring: Boolean = false,
    val recurringInterval: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderAt: Long? = null
)

enum class Priority { LOW, MEDIUM, HIGH, URGENT }
enum class AssignmentStatus { PENDING, IN_PROGRESS, COMPLETED }

// ─────────────────────────────────────────────
//  NOTE MODEL
// ─────────────────────────────────────────────
data class Note(
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "General",
    val attachmentPath: String? = null,
    val attachmentType: String? = null,
    val isPinned: Boolean = false,
    val color: String = "#1E1E2E",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
//  ATTENDANCE MODEL
// ─────────────────────────────────────────────
data class Attendance(
    val id: Int = 0,
    val subject: String,
    val attendedClasses: Int = 0,
    val totalClasses: Int = 0,
    val requiredPercentage: Float = 75f,
    val semester: String = "Current",
    val color: String = "#6C63FF"
) {
    val percentage: Float
        get() = if (totalClasses == 0) 0f else (attendedClasses.toFloat() / totalClasses) * 100

    val isShortage: Boolean
        get() = percentage < requiredPercentage

    val classesNeededToReach: Int
        get() {
            if (!isShortage) return 0
            // classes needed to reach required percentage
            val need = ((requiredPercentage * totalClasses) - (attendedClasses * 100)) / (100 - requiredPercentage)
            return kotlin.math.ceil(need.toDouble()).toInt()
        }
}

// ─────────────────────────────────────────────
//  TIMETABLE MODEL
// ─────────────────────────────────────────────
data class TimetableClass(
    val id: Int = 0,
    val subject: String,
    val teacher: String = "",
    val room: String = "",
    val day: DayOfWeek,
    val startTime: String,
    val endTime: String,
    val color: String = "#6C63FF",
    val notifyBefore: Int = 10
)

enum class DayOfWeek(val displayName: String, val shortName: String) {
    MON("Monday", "Mon"),
    TUE("Tuesday", "Tue"),
    WED("Wednesday", "Wed"),
    THU("Thursday", "Thu"),
    FRI("Friday", "Fri"),
    SAT("Saturday", "Sat")
}

// ─────────────────────────────────────────────
//  POMODORO SESSION MODEL
// ─────────────────────────────────────────────
data class PomodoroSession(
    val id: Int = 0,
    val duration: Int,
    val breakDuration: Int = 5,
    val taskTitle: String = "",
    val completed: Boolean = false,
    val focusScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
//  AI CHAT MODEL
// ─────────────────────────────────────────────
data class AiChatMessage(
    val id: Int = 0,
    val userMessage: String,
    val aiResponse: String,
    val subject: String = "General",
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
//  DASHBOARD STATS MODEL
// ─────────────────────────────────────────────
data class DashboardStats(
    val pendingTasks: Int = 0,
    val completedTasks: Int = 0,
    val totalFocusMinutes: Int = 0,
    val overallAttendance: Float = 0f,
    val todayClasses: Int = 0,
    val motivationalQuote: String = ""
)

// ─────────────────────────────────────────────
//  UI STATE WRAPPER
// ─────────────────────────────────────────────
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}
