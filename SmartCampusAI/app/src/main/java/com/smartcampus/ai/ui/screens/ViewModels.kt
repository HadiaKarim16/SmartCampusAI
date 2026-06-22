package com.smartcampus.ai.ui.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcampus.ai.data.repository.NoteRepository
import com.smartcampus.ai.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NoteRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val notes: StateFlow<List<Note>> = combine(
        repository.getAllNotes(), _searchQuery, _selectedCategory
    ) { list, query, category ->
        list.filter { note ->
            val matchQuery = query.isBlank() || note.title.contains(query, true) || note.content.contains(query, true)
            val matchCategory = category == "All" || note.category == category
            matchQuery && matchCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = repository.getCategories()
        .map { listOf("All") + it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("All"))

    private val _editingNote = MutableStateFlow<Note?>(null)
    val editingNote: StateFlow<Note?> = _editingNote.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<Unit>?>(null)
    val saveState: StateFlow<UiState<Unit>?> = _saveState.asStateFlow()

    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setCategory(c: String) { _selectedCategory.value = c }

    fun loadNote(id: Int) {
        viewModelScope.launch {
            _editingNote.value = if (id == -1) null else repository.getById(id)
        }
    }

    fun saveNote(title: String, content: String, category: String, isPinned: Boolean, existingId: Int) {
        if (title.isBlank()) { _saveState.value = UiState.Error("Title required"); return }
        viewModelScope.launch {
            _saveState.value = UiState.Loading
            try {
                val note = Note(
                    id = if (existingId == -1) 0 else existingId,
                    title = title.trim(),
                    content = content.trim(),
                    category = category.ifBlank { "General" },
                    isPinned = isPinned,
                    updatedAt = System.currentTimeMillis()
                )
                if (existingId == -1) repository.addNote(note) else repository.updateNote(note)
                _saveState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = UiState.Error(e.message ?: "Save failed")
            }
        }
    }

    fun deleteNote(note: Note) { viewModelScope.launch { repository.deleteNote(note) } }
    fun clearSaveState() { _saveState.value = null }
}

// ─── ATTENDANCE VIEW MODEL ──────────────────────
package com.smartcampus.ai.ui.screens.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcampus.ai.data.repository.AttendanceRepository
import com.smartcampus.ai.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val repository: AttendanceRepository) : ViewModel() {
    val attendanceList: StateFlow<List<Attendance>> = repository.getAllAttendance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveState = MutableStateFlow<UiState<Unit>?>(null)
    val saveState: StateFlow<UiState<Unit>?> = _saveState.asStateFlow()

    fun addSubject(name: String, requiredPct: Float, color: String) {
        if (name.isBlank()) { _saveState.value = UiState.Error("Subject name required"); return }
        viewModelScope.launch {
            repository.addSubject(Attendance(subject = name.trim(), requiredPercentage = requiredPct, color = color))
            _saveState.value = UiState.Success(Unit)
        }
    }

    fun markPresent(id: Int) { viewModelScope.launch { repository.markPresent(id) } }
    fun markAbsent(id: Int) { viewModelScope.launch { repository.markAbsent(id) } }
    fun deleteSubject(attendance: Attendance) { viewModelScope.launch { repository.deleteSubject(attendance) } }
    fun clearSaveState() { _saveState.value = null }
}

// ─── TIMETABLE VIEW MODEL ───────────────────────
package com.smartcampus.ai.ui.screens.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcampus.ai.data.repository.TimetableRepository
import com.smartcampus.ai.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(private val repository: TimetableRepository) : ViewModel() {
    val allClasses: StateFlow<List<TimetableClass>> = repository.getAllClasses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDay = MutableStateFlow(DayOfWeek.MON)
    val selectedDay: StateFlow<DayOfWeek> = _selectedDay.asStateFlow()

    val classesByDay: StateFlow<Map<DayOfWeek, List<TimetableClass>>> = allClasses.map { list ->
        DayOfWeek.values().associateWith { day -> list.filter { it.day == day } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _saveState = MutableStateFlow<UiState<Unit>?>(null)
    val saveState: StateFlow<UiState<Unit>?> = _saveState.asStateFlow()

    fun setSelectedDay(day: DayOfWeek) { _selectedDay.value = day }

    fun addClass(subject: String, teacher: String, room: String, day: DayOfWeek, start: String, end: String, color: String) {
        if (subject.isBlank()) { _saveState.value = UiState.Error("Subject required"); return }
        viewModelScope.launch {
            repository.addClass(TimetableClass(subject = subject, teacher = teacher, room = room, day = day, startTime = start, endTime = end, color = color))
            _saveState.value = UiState.Success(Unit)
        }
    }

    fun deleteClass(cls: TimetableClass) { viewModelScope.launch { repository.deleteClass(cls) } }
    fun clearSaveState() { _saveState.value = null }
}

// ─── POMODORO VIEW MODEL ────────────────────────
package com.smartcampus.ai.ui.screens.pomodoro

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcampus.ai.data.preferences.PreferencesManager
import com.smartcampus.ai.data.repository.PomodoroRepository
import com.smartcampus.ai.domain.model.PomodoroSession
import com.smartcampus.ai.service.PomodoroService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TimerState { IDLE, RUNNING, PAUSED, BREAK }

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val repository: PomodoroRepository,
    private val preferencesManager: PreferencesManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val pomodoroDuration: StateFlow<Int> = preferencesManager.pomodoroDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 25)

    val breakDuration: StateFlow<Int> = preferencesManager.breakDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    val completedSessions: StateFlow<Int> = repository.getCompletedSessionCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalFocusMinutes: StateFlow<Int> = repository.getTotalFocusMinutes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _timeLeft = MutableStateFlow(25 * 60)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _taskTitle = MutableStateFlow("")
    val taskTitle: StateFlow<String> = _taskTitle.asStateFlow()

    private var currentSessionId = 0L

    fun setTaskTitle(title: String) { _taskTitle.value = title }

    fun startTimer(durationMinutes: Int) {
        _timeLeft.value = durationMinutes * 60
        _timerState.value = TimerState.RUNNING
        viewModelScope.launch {
            currentSessionId = repository.saveSession(
                PomodoroSession(duration = durationMinutes, taskTitle = _taskTitle.value)
            )
        }
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_START
            putExtra(PomodoroService.EXTRA_DURATION, durationMinutes * 60)
        }
        context.startForegroundService(intent)
    }

    fun pauseTimer() {
        _timerState.value = TimerState.PAUSED
        context.startService(Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_PAUSE
        })
    }

    fun resumeTimer() {
        _timerState.value = TimerState.RUNNING
        context.startService(Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_RESUME
        })
    }

    fun stopTimer() {
        _timerState.value = TimerState.IDLE
        context.startService(Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_STOP
        })
    }

    fun updateTimeLeft(seconds: Int) { _timeLeft.value = seconds }

    fun completeSession(breakMinutes: Int) {
        _timerState.value = TimerState.BREAK
        viewModelScope.launch {
            repository.saveSession(
                PomodoroSession(
                    id = currentSessionId.toInt(),
                    duration = pomodoroDuration.value,
                    breakDuration = breakMinutes,
                    taskTitle = _taskTitle.value,
                    completed = true,
                    focusScore = 100
                )
            )
        }
    }
}

// ─── SETTINGS VIEW MODEL ────────────────────────
package com.smartcampus.ai.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcampus.ai.data.preferences.PreferencesManager
import com.smartcampus.ai.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val authRepository: AuthRepository
) : ViewModel() {
    val isDarkMode: StateFlow<Boolean> = preferencesManager.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notificationsEnabled: StateFlow<Boolean> = preferencesManager.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val pomodoroDuration: StateFlow<Int> = preferencesManager.pomodoroDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 25)

    val breakDuration: StateFlow<Int> = preferencesManager.breakDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    val attendanceThreshold: StateFlow<Float> = preferencesManager.attendanceThreshold
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 75f)

    val userName: StateFlow<String> = preferencesManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userEmail: StateFlow<String> = preferencesManager.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun setDarkMode(enabled: Boolean) { viewModelScope.launch { preferencesManager.setDarkMode(enabled) } }
    fun setNotifications(enabled: Boolean) { viewModelScope.launch { preferencesManager.setNotificationsEnabled(enabled) } }
    fun setPomodoroDuration(min: Int) { viewModelScope.launch { preferencesManager.setPomodoroDuration(min) } }
    fun setBreakDuration(min: Int) { viewModelScope.launch { preferencesManager.setBreakDuration(min) } }
    fun setAttendanceThreshold(t: Float) { viewModelScope.launch { preferencesManager.setAttendanceThreshold(t) } }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogout()
        }
    }
}
