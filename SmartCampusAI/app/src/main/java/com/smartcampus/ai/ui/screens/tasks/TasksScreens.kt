package com.smartcampus.ai.ui.screens.tasks

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcampus.ai.domain.model.*
import com.smartcampus.ai.ui.components.*
import com.smartcampus.ai.ui.theme.SmartCampusColors
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────
//  TASKS SCREEN
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onAddTask: () -> Unit,
    onEditTask: (Int) -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf<Assignment?>(null) }

    Scaffold(
        containerColor = SmartCampusColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SmartCampusColors.Surface)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "My Tasks",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    FloatingActionButton(
                        onClick = onAddTask,
                        modifier = Modifier.size(44.dp),
                        containerColor = SmartCampusColors.Primary,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add Task", modifier = Modifier.size(20.dp))
                    }
                }
                // Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::setSearchQuery,
                    placeholder = { Text("Search tasks...", color = SmartCampusColors.OnSurfaceVariant) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = SmartCampusColors.OnSurfaceVariant)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, null, tint = SmartCampusColors.OnSurfaceVariant)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SmartCampusColors.Primary,
                        unfocusedBorderColor = SmartCampusColors.SurfaceVariant
                    )
                )
                // Filters
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(TaskFilter.values()) { f ->
                        FilterChip(
                            selected = filter == f,
                            onClick = { viewModel.setFilter(f) },
                            label = {
                                Text(
                                    f.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SmartCampusColors.Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (assignments.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Assignment,
                title = "No tasks yet",
                subtitle = "Add your first assignment or task",
                actionLabel = "Add Task",
                onAction = onAddTask,
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(assignments, key = { it.id }) { assignment ->
                    AssignmentCard(
                        assignment = assignment,
                        onToggleStatus = { viewModel.toggleStatus(assignment) },
                        onEdit = { onEditTask(assignment.id) },
                        onDelete = { showDeleteDialog = assignment }
                    )
                }
            }
        }
    }

    // Delete confirmation
    showDeleteDialog?.let { assignment ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete \"${assignment.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAssignment(assignment)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = SmartCampusColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
            },
            containerColor = SmartCampusColors.Surface
        )
    }
}

// ─────────────────────────────────────────────
//  ADD / EDIT TASK SCREEN
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: Int,
    onNavigateBack: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val editingAssignment by viewModel.editingAssignment.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var deadlineDate by remember { mutableStateOf(System.currentTimeMillis() + 86400000L) }
    var showDatePicker by remember { mutableStateOf(false) }
    var enableReminder by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        viewModel.loadAssignment(taskId)
    }

    LaunchedEffect(editingAssignment) {
        editingAssignment?.let { a ->
            title = a.title
            description = a.description
            subject = a.subject
            selectedPriority = a.priority
            deadlineDate = a.deadline
            enableReminder = a.reminderAt != null
        }
    }

    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) {
            viewModel.clearSaveState()
            onNavigateBack()
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = deadlineDate)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { deadlineDate = it }
                        showDatePicker = false
                    }
                ) { Text("OK", color = SmartCampusColors.Primary) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
            colors = DatePickerDefaults.colors(containerColor = SmartCampusColors.Surface)
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = SmartCampusColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (taskId == -1) "New Task" else "Edit Task",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SmartCampusColors.Surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title *") },
                leadingIcon = { Icon(Icons.Default.Assignment, null, tint = SmartCampusColors.Primary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
            )

            // Subject
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject *") },
                leadingIcon = { Icon(Icons.Default.Book, null, tint = SmartCampusColors.Primary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                leadingIcon = { Icon(Icons.Default.Notes, null, tint = SmartCampusColors.Primary) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
            )

            // Priority
            Text(
                "Priority",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Priority.values()) { priority ->
                    val color = when (priority) {
                        Priority.LOW -> SmartCampusColors.PriorityLow
                        Priority.MEDIUM -> SmartCampusColors.PriorityMedium
                        Priority.HIGH -> SmartCampusColors.PriorityHigh
                        Priority.URGENT -> SmartCampusColors.PriorityUrgent
                    }
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = {
                            Text(
                                priority.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.2f),
                            selectedLabelColor = color
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedPriority == priority,
                            selectedBorderColor = color,
                            borderColor = SmartCampusColors.SurfaceVariant
                        )
                    )
                }
            }

            // Deadline
            Text(
                "Deadline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SmartCampusColors.SurfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, null, tint = SmartCampusColors.Primary)
                    Text(
                        SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault()).format(Date(deadlineDate)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Reminder Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SmartCampusColors.SurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = SmartCampusColors.Tertiary)
                        Column {
                            Text("Enable Reminder", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Get notified 1 day before deadline",
                                style = MaterialTheme.typography.bodySmall,
                                color = SmartCampusColors.OnSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = enableReminder,
                        onCheckedChange = { enableReminder = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = SmartCampusColors.Primary)
                    )
                }
            }

            // Error Message
            AnimatedVisibility(visible = saveState is UiState.Error) {
                val errorMsg = (saveState as? UiState.Error)?.message ?: ""
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SmartCampusColors.Error.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, null, tint = SmartCampusColors.Error, modifier = Modifier.size(16.dp))
                    Text(errorMsg, color = SmartCampusColors.Error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.saveAssignment(
                        title = title,
                        description = description,
                        subject = subject,
                        deadline = deadlineDate,
                        priority = selectedPriority,
                        reminderAt = if (enableReminder) deadlineDate - 86400000L else null,
                        existingId = taskId
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartCampusColors.Primary),
                enabled = saveState !is UiState.Loading
            ) {
                if (saveState is UiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (taskId == -1) "Create Task" else "Save Changes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
