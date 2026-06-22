package com.smartcampus.ai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartcampus.ai.data.local.dao.*
import com.smartcampus.ai.data.local.entity.*

/**
 * SmartCampusDatabase - Central Room database
 * Contains all DAOs and entities for the application
 */
@Database(
    entities = [
        UserEntity::class,
        AssignmentEntity::class,
        NoteEntity::class,
        AttendanceEntity::class,
        AttendanceLogEntity::class,
        TimetableEntity::class,
        PomodoroSessionEntity::class,
        AiChatEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmartCampusDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun noteDao(): NoteDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun timetableDao(): TimetableDao
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun aiChatDao(): AiChatDao

    companion object {
        const val DATABASE_NAME = "smart_campus_db"
    }
}
