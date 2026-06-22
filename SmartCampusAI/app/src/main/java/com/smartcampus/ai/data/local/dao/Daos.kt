package com.smartcampus.ai.data.local.dao

import androidx.room.*
import com.smartcampus.ai.data.local.entity.*
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────
//  USER DAO
// ─────────────────────────────────────────────
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Int): Flow<UserEntity?>

    @Update
    suspend fun updateUser(user: UserEntity)
}

// ─────────────────────────────────────────────
//  ASSIGNMENT DAO
// ─────────────────────────────────────────────
@Dao
interface AssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Delete
    suspend fun deleteAssignment(assignment: AssignmentEntity)

    @Query("SELECT * FROM assignments ORDER BY deadline ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE status != 'COMPLETED' ORDER BY deadline ASC")
    fun getPendingAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE status = 'COMPLETED' ORDER BY deadline DESC")
    fun getCompletedAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE deadline BETWEEN :start AND :end ORDER BY deadline ASC")
    fun getAssignmentsDueBetween(start: Long, end: Long): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE id = :id LIMIT 1")
    suspend fun getAssignmentById(id: Int): AssignmentEntity?

    @Query("UPDATE assignments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("SELECT COUNT(*) FROM assignments WHERE status = 'COMPLETED'")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM assignments WHERE status != 'COMPLETED'")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT * FROM assignments WHERE deadline <= :deadline AND status != 'COMPLETED' ORDER BY deadline ASC LIMIT 5")
    fun getUpcomingAssignments(deadline: Long): Flow<List<AssignmentEntity>>
}

// ─────────────────────────────────────────────
//  NOTES DAO
// ─────────────────────────────────────────────
@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE category = :category ORDER BY updatedAt DESC")
    fun getNotesByCategory(category: String): Flow<List<NoteEntity>>

    @Query("SELECT DISTINCT category FROM notes")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Int): NoteEntity?
}

// ─────────────────────────────────────────────
//  ATTENDANCE DAO
// ─────────────────────────────────────────────
@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Delete
    suspend fun deleteAttendance(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance ORDER BY subject ASC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE id = :id LIMIT 1")
    suspend fun getAttendanceById(id: Int): AttendanceEntity?

    @Query("UPDATE attendance SET attendedClasses = attendedClasses + :delta, totalClasses = totalClasses + 1 WHERE id = :id")
    suspend fun markAttendance(id: Int, delta: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AttendanceLogEntity)

    @Query("SELECT * FROM attendance_log WHERE attendanceId = :attendanceId ORDER BY date DESC")
    fun getLogsForSubject(attendanceId: Int): Flow<List<AttendanceLogEntity>>
}

// ─────────────────────────────────────────────
//  TIMETABLE DAO
// ─────────────────────────────────────────────
@Dao
interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(timetable: TimetableEntity): Long

    @Update
    suspend fun updateClass(timetable: TimetableEntity)

    @Delete
    suspend fun deleteClass(timetable: TimetableEntity)

    @Query("SELECT * FROM timetable ORDER BY day ASC, startTime ASC")
    fun getAllClasses(): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetable WHERE day = :day ORDER BY startTime ASC")
    fun getClassesByDay(day: String): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetable WHERE id = :id LIMIT 1")
    suspend fun getClassById(id: Int): TimetableEntity?
}

// ─────────────────────────────────────────────
//  POMODORO DAO
// ─────────────────────────────────────────────
@Dao
interface PomodoroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSessionEntity): Long

    @Update
    suspend fun updateSession(session: PomodoroSessionEntity)

    @Query("SELECT * FROM pomodoro_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<PomodoroSessionEntity>>

    @Query("SELECT * FROM pomodoro_sessions WHERE createdAt >= :from ORDER BY createdAt DESC")
    fun getSessionsFrom(from: Long): Flow<List<PomodoroSessionEntity>>

    @Query("SELECT SUM(duration) FROM pomodoro_sessions WHERE completed = 1")
    fun getTotalFocusMinutes(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM pomodoro_sessions WHERE completed = 1")
    fun getCompletedSessionCount(): Flow<Int>
}

// ─────────────────────────────────────────────
//  AI CHAT DAO
// ─────────────────────────────────────────────
@Dao
interface AiChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: AiChatEntity): Long

    @Query("SELECT * FROM ai_chat_history ORDER BY createdAt DESC LIMIT 50")
    fun getRecentChats(): Flow<List<AiChatEntity>>

    @Query("DELETE FROM ai_chat_history")
    suspend fun clearHistory()
}
