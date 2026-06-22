package com.smartcampus.ai.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcampus.ai.data.repository.AssignmentRepository
import com.smartcampus.ai.domain.model.*
import com.smartcampus.ai.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskFilter { ALL, PENDING, COMPLETED, URGENT }

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: AssignmentRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val assignments: StateFlow<List<Assignment>> = combine(
        repository.getAllAssignments(),
        _filter,
        _searchQuery
    ) { list, filter, query ->
        list
            .filter { assignment ->
                when (filter) {
                    TaskFilter.ALL -> true
                    TaskFilter.PENDING -> assignment.status != AssignmentStatus.COMPLETED
                    TaskFilter.COMPLETED -> assignment.status == AssignmentStatus.COMPLETED
                    TaskFilter.URGENT -> assignment.priority == Priority.URGENT || assignment.priority == Priority.HIGH
                }
            }
            .filter { assignment ->
                if (query.isBlank()) true
                else assignment.title.contains(query, ignoreCase = true) ||
                     assignment.subject.contains(query, ignoreCase = true)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingAssignment = MutableStateFlow<Assignment?>(null)
    val editingAssignment: StateFlow<Assignment?> = _editingAssignment.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<Unit>?>(null)
    val saveState: StateFlow<UiState<Unit>?> = _saveState.asStateFlow()

    fun setFilter(f: TaskFilter) { _filter.value = f }
    fun setSearchQuery(q: String) { _searchQuery.value = q }

    fun loadAssignment(id: Int) {
        viewModelScope.launch {
            _editingAssignment.value = if (id == -1) null else repository.getById(id)
        }
    }

    fun saveAssignment(
        title: String,
        description: String,
        subject: String,
        deadline: Long,
        priority: Priority,
        reminderAt: Long?,
        existingId: Int = -1
    ) {
        if (title.isBlank()) {
            _saveState.value = UiState.Error("Title is required")
            return
        }
        if (subject.isBlank()) {
            _saveState.value = UiState.Error("Subject is required")
            return
        }
        viewModelScope.launch {
            _saveState.value = UiState.Loading
            try {
                val assignment = Assignment(
                    id = if (existingId == -1) 0 else existingId,
                    title = title.trim(),
                    description = description.trim(),
                    subject = subject.trim(),
                    deadline = deadline,
                    priority = priority,
                    reminderAt = reminderAt
                )
                if (existingId == -1) {
                    val id = repository.addAssignment(assignment)
                    // Schedule reminder if set
                    reminderAt?.let {
                        notificationHelper.scheduleReminder(
                            taskId = id.toInt(),
                            title = "Deadline Reminder",
                            message = "\"$title\" is due soon!",
                            triggerAt = it
                        )
                    }
                } else {
                    repository.updateAssignment(assignment)
                }
                _saveState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = UiState.Error(e.message ?: "Save failed")
            }
        }
    }

    fun toggleStatus(assignment: Assignment) {
        viewModelScope.launch { repository.toggleStatus(assignment) }
    }

    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch { repository.deleteAssignment(assignment) }
    }

    fun clearSaveState() { _saveState.value = null }
}
