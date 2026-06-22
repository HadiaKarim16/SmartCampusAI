package com.smartcampus.ai.data.local.entity

import com.smartcampus.ai.domain.model.*

// ─── UserEntity Mappers ───
fun UserEntity.toDomain() = User(id, name, email, password, profileImagePath)
fun User.toEntity() = UserEntity(id, name, email, password, profileImagePath)

// ─── AssignmentEntity Mappers ───
fun AssignmentEntity.toDomain() = Assignment(
    id = id,
    title = title,
    description = description,
    subject = subject,
    deadline = deadline,
    priority = Priority.valueOf(priority),
    status = AssignmentStatus.valueOf(status),
    isRecurring = isRecurring,
    recurringInterval = recurringInterval,
    createdAt = createdAt,
    reminderAt = reminderAt
)

fun Assignment.toEntity() = AssignmentEntity(
    id = id,
    title = title,
    description = description,
    subject = subject,
    deadline = deadline,
    priority = priority.name,
    status = status.name,
    isRecurring = isRecurring,
    recurringInterval = recurringInterval,
    createdAt = createdAt,
    reminderAt = reminderAt
)

// ─── NoteEntity Mappers ───
fun NoteEntity.toDomain() = Note(id, title, content, category, attachmentPath, attachmentType, isPinned, color, createdAt, updatedAt)
fun Note.toEntity() = NoteEntity(id, title, content, category, attachmentPath, attachmentType, isPinned, color, createdAt, updatedAt)

// ─── AttendanceEntity Mappers ───
fun AttendanceEntity.toDomain() = Attendance(id, subject, attendedClasses, totalClasses, requiredPercentage, semester, color)
fun Attendance.toEntity() = AttendanceEntity(id, subject, attendedClasses, totalClasses, requiredPercentage, semester, color)

// ─── TimetableEntity Mappers ───
fun TimetableEntity.toDomain() = TimetableClass(
    id = id,
    subject = subject,
    teacher = teacher,
    room = room,
    day = DayOfWeek.valueOf(day),
    startTime = startTime,
    endTime = endTime,
    color = color,
    notifyBefore = notifyBefore
)

fun TimetableClass.toEntity() = TimetableEntity(
    id = id,
    subject = subject,
    teacher = teacher,
    room = room,
    day = day.name,
    startTime = startTime,
    endTime = endTime,
    color = color,
    notifyBefore = notifyBefore
)

// ─── PomodoroEntity Mappers ───
fun PomodoroSessionEntity.toDomain() = PomodoroSession(id, duration, breakDuration, taskTitle, completed, focusScore, createdAt)
fun PomodoroSession.toEntity() = PomodoroSessionEntity(id, duration, breakDuration, taskTitle, completed, focusScore, createdAt)

// ─── AiChatEntity Mappers ───
fun AiChatEntity.toDomain() = AiChatMessage(id, userMessage, aiResponse, subject, createdAt)
fun AiChatMessage.toEntity() = AiChatEntity(id, userMessage, aiResponse, subject, createdAt)
