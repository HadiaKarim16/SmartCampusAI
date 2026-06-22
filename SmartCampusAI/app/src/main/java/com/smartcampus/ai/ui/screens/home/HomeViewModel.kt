package com.smartcampus.ai.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcampus.ai.data.preferences.PreferencesManager
import com.smartcampus.ai.data.repository.*
import com.smartcampus.ai.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val assignmentRepository: AssignmentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val timetableRepository: TimetableRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val aiRepository: AiRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val userName: StateFlow<String> = preferencesManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Student")

    val pendingTasksCount: StateFlow<Int> = assignmentRepository.getPendingCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedTasksCount: StateFlow<Int> = assignmentRepository.getCompletedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalFocusMinutes: StateFlow<Int> = pomodoroRepository.getTotalFocusMinutes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Upcoming assignments in next 7 days
    val upcomingAssignments: StateFlow<List<Assignment>> = assignmentRepository
        .getUpcomingAssignments(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's classes
    val todayClasses: StateFlow<List<TimetableClass>> = run {
        val today = getCurrentDay()
        timetableRepository.getClassesByDay(today)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Attendance with shortages
    val attendanceList: StateFlow<List<Attendance>> = attendanceRepository.getAllAttendance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _motivationalQuote = MutableStateFlow("Loading inspiration...")
    val motivationalQuote: StateFlow<String> = _motivationalQuote.asStateFlow()

    init {
        fetchMotivationalQuote()
    }

    private fun fetchMotivationalQuote() {
        viewModelScope.launch {
            aiRepository.getMotivationalQuote().onSuccess {
                _motivationalQuote.value = it
            }
        }
    }

    fun refreshQuote() = fetchMotivationalQuote()

    private fun getCurrentDay(): String {
        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            else -> "MON"
        }
    }

    val overallAttendance: StateFlow<Float> = attendanceList.map { list ->
        if (list.isEmpty()) return@map 0f
        list.map { it.percentage }.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val shortageSubjects: StateFlow<List<Attendance>> = attendanceList.map { list ->
        list.filter { it.isShortage }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
