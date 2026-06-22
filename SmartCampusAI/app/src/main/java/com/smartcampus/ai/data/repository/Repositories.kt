package com.smartcampus.ai.data.repository

import com.smartcampus.ai.data.local.dao.*
import com.smartcampus.ai.data.local.entity.*
import com.smartcampus.ai.data.preferences.PreferencesManager
import com.smartcampus.ai.data.remote.AiService
import com.smartcampus.ai.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// ─────────────────────────────────────────────
//  AUTH REPOSITORY
// ─────────────────────────────────────────────
@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val preferencesManager: PreferencesManager
) {
    val isLoggedIn: Flow<Boolean> = preferencesManager.isLoggedIn
    val userId: Flow<Int> = preferencesManager.userId
    val userName: Flow<String> = preferencesManager.userName

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = userDao.login(email.trim(), password)
            if (user != null) {
                preferencesManager.setLoggedIn(user.id, user.name, user.email)
                Result.success(user.toDomain())
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(name: String, email: String, password: String): Result<User> {
        return try {
            val existing = userDao.getUserByEmail(email.trim())
            if (existing != null) return Result.failure(Exception("Email already registered"))
            val user = UserEntity(name = name.trim(), email = email.trim(), password = password)
            val id = userDao.insertUser(user).toInt()
            preferencesManager.setLoggedIn(id, name, email)
            Result.success(user.copy(id = id).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() = preferencesManager.logout()
}

// ─────────────────────────────────────────────
//  ASSIGNMENT REPOSITORY
// ─────────────────────────────────────────────
@Singleton
class AssignmentRepository @Inject constructor(
    private val assignmentDao: AssignmentDao
) {
    fun getAllAssignments(): Flow<List<Assignment>> =
        assignmentDao.getAllAssignments().map { list -> list.map { it.toDomain() } }

    fun getPendingAssignments(): Flow<List<Assignment>> =
        assignmentDao.getPendingAssignments().map { list -> list.map { it.toDomain() } }

    fun getCompletedAssignments(): Flow<List<Assignment>> =
        assignmentDao.getCompletedAssignments().map { list -> list.map { it.toDomain() } }

    fun getUpcomingAssignments(deadline: Long): Flow<List<Assignment>> =
        assignmentDao.getUpcomingAssignments(deadline).map { list -> list.map { it.toDomain() } }

    fun getPendingCount(): Flow<Int> = assignmentDao.getPendingCount()
    fun getCompletedCount(): Flow<Int> = assignmentDao.getCompletedCount()

    suspend fun addAssignment(assignment: Assignment): Long =
        assignmentDao.insertAssignment(assignment.toEntity())

    suspend fun updateAssignment(assignment: Assignment) =
        assignmentDao.updateAssignment(assignment.toEntity())

    suspend fun deleteAssignment(assignment: Assignment) =
        assignmentDao.deleteAssignment(assignment.toEntity())

    suspend fun toggleStatus(assignment: Assignment) {
        val newStatus = if (assignment.status == AssignmentStatus.COMPLETED)
            AssignmentStatus.PENDING else AssignmentStatus.COMPLETED
        assignmentDao.updateStatus(assignment.id, newStatus.name)
    }

    suspend fun getById(id: Int): Assignment? =
        assignmentDao.getAssignmentById(id)?.toDomain()
}

// ─────────────────────────────────────────────
//  NOTES REPOSITORY
// ─────────────────────────────────────────────
@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { list -> list.map { it.toDomain() } }

    fun searchNotes(query: String): Flow<List<Note>> =
        noteDao.searchNotes(query).map { list -> list.map { it.toDomain() } }

    fun getNotesByCategory(category: String): Flow<List<Note>> =
        noteDao.getNotesByCategory(category).map { list -> list.map { it.toDomain() } }

    fun getCategories(): Flow<List<String>> = noteDao.getCategories()

    suspend fun addNote(note: Note): Long = noteDao.insertNote(note.toEntity())
    suspend fun updateNote(note: Note) = noteDao.updateNote(note.toEntity())
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note.toEntity())
    suspend fun getById(id: Int): Note? = noteDao.getNoteById(id)?.toDomain()
}

// ─────────────────────────────────────────────
//  ATTENDANCE REPOSITORY
// ─────────────────────────────────────────────
@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao
) {
    fun getAllAttendance(): Flow<List<Attendance>> =
        attendanceDao.getAllAttendance().map { list -> list.map { it.toDomain() } }

    suspend fun addSubject(attendance: Attendance): Long =
        attendanceDao.insertAttendance(attendance.toEntity())

    suspend fun updateSubject(attendance: Attendance) =
        attendanceDao.updateAttendance(attendance.toEntity())

    suspend fun deleteSubject(attendance: Attendance) =
        attendanceDao.deleteAttendance(attendance.toEntity())

    suspend fun markPresent(id: Int) {
        attendanceDao.markAttendance(id, 1)
        attendanceDao.insertLog(
            AttendanceLogEntity(
                attendanceId = id,
                date = System.currentTimeMillis(),
                status = "PRESENT"
            )
        )
    }

    suspend fun markAbsent(id: Int) {
        attendanceDao.markAttendance(id, 0)
        attendanceDao.insertLog(
            AttendanceLogEntity(
                attendanceId = id,
                date = System.currentTimeMillis(),
                status = "ABSENT"
            )
        )
    }

    suspend fun getById(id: Int): Attendance? =
        attendanceDao.getAttendanceById(id)?.toDomain()
}

// ─────────────────────────────────────────────
//  TIMETABLE REPOSITORY
// ─────────────────────────────────────────────
@Singleton
class TimetableRepository @Inject constructor(
    private val timetableDao: TimetableDao
) {
    fun getAllClasses(): Flow<List<TimetableClass>> =
        timetableDao.getAllClasses().map { list -> list.map { it.toDomain() } }

    fun getClassesByDay(day: String): Flow<List<TimetableClass>> =
        timetableDao.getClassesByDay(day).map { list -> list.map { it.toDomain() } }

    suspend fun addClass(timetableClass: TimetableClass): Long =
        timetableDao.insertClass(timetableClass.toEntity())

    suspend fun updateClass(timetableClass: TimetableClass) =
        timetableDao.updateClass(timetableClass.toEntity())

    suspend fun deleteClass(timetableClass: TimetableClass) =
        timetableDao.deleteClass(timetableClass.toEntity())

    suspend fun getById(id: Int): TimetableClass? =
        timetableDao.getClassById(id)?.toDomain()
}

// ─────────────────────────────────────────────
//  POMODORO REPOSITORY
// ─────────────────────────────────────────────
@Singleton
class PomodoroRepository @Inject constructor(
    private val pomodoroDao: PomodoroDao
) {
    fun getAllSessions(): Flow<List<PomodoroSession>> =
        pomodoroDao.getAllSessions().map { list -> list.map { it.toDomain() } }

    fun getTotalFocusMinutes(): Flow<Int> =
        pomodoroDao.getTotalFocusMinutes().map { it ?: 0 }

    fun getCompletedSessionCount(): Flow<Int> =
        pomodoroDao.getCompletedSessionCount()

    suspend fun saveSession(session: PomodoroSession): Long =
        pomodoroDao.insertSession(session.toEntity())

    suspend fun completeSession(id: Int) {
        val session = pomodoroDao.getAllSessions()
        // Update handled via full object update
    }
}

// ─────────────────────────────────────────────
//  AI REPOSITORY
// ─────────────────────────────────────────────
@Singleton
class AiRepository @Inject constructor(
    private val aiService: AiService,
    private val aiChatDao: AiChatDao
) {
    fun getChatHistory(): Flow<List<AiChatMessage>> =
        aiChatDao.getRecentChats().map { list -> list.map { it.toDomain() } }

    suspend fun sendMessage(userMessage: String, subject: String = "General"): Result<AiChatMessage> {
        val result = aiService.sendMessage(userMessage, subject)
        return result.map { response ->
            val chat = AiChatMessage(
                userMessage = userMessage,
                aiResponse = response,
                subject = subject
            )
            aiChatDao.insertChat(chat.toEntity())
            chat
        }
    }

    suspend fun getMotivationalQuote(): Result<String> =
        aiService.getMotivationalQuote()

    suspend fun clearHistory() = aiChatDao.clearHistory()
}
